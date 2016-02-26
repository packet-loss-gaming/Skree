/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine;

import com.skelril.nitro.data.util.EnchantmentUtil;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.block.DropRegistry;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.modifier.Modifiers;
import com.skelril.skree.content.zone.global.cursedmine.hitlist.HitList;
import com.skelril.skree.content.zone.global.cursedmine.restoration.BlockRecord;
import com.skelril.skree.service.ModifierService;
import net.minecraft.item.ItemPickaxe;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.animal.Wolf;
import org.spongepowered.api.entity.living.monster.Blaze;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class CursedMineListener {
    
    private final CursedMineManager manager;

    public CursedMineListener(CursedMineManager manager) {
        this.manager = manager;
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        for (Entity entity : event.getEntities()) {
            if (manager.getApplicableZone(entity).isPresent() && entity instanceof Agent) {
                if (entity instanceof Blaze || entity instanceof Wolf) {
                    continue;
                }
                event.setCancelled(true);
                break;
            }
        }
    }

    private static Set<BlockType> triggerBlocks = new HashSet<>();

    static {
        triggerBlocks.add(BlockTypes.STONE_BUTTON);
        triggerBlocks.add(BlockTypes.TRIPWIRE);
    }

    @Listener
    public void onPlayerInteract(InteractBlockEvent event) {
        if (event instanceof InteractBlockEvent.Primary || event instanceof InteractBlockEvent.Secondary) {
            return;
        }

        BlockSnapshot snapshot = event.getTargetBlock();

        Optional<CursedMineInstance> optInst = manager.getApplicableZone(snapshot);
        if (optInst.isPresent() && triggerBlocks.contains(snapshot.getState().getType())) {
            optInst.get().activatePumps();
        }
    }

    private boolean hasSilkTouch(ItemStack stack) {
        Optional<ItemEnchantment> optSilkTouch = EnchantmentUtil.getHighestEnchantment(
                stack,
                Enchantments.SILK_TOUCH
        );

        return optSilkTouch.isPresent();
    }

    private static Set<BlockType> cursedOres = new HashSet<>();

    static {
        cursedOres.add(BlockTypes.DIAMOND_ORE);
        cursedOres.add(BlockTypes.EMERALD_ORE);
        cursedOres.add(BlockTypes.LAPIS_ORE);
        cursedOres.add(BlockTypes.GOLD_ORE);
        cursedOres.add(BlockTypes.IRON_ORE);
        cursedOres.add(BlockTypes.REDSTONE_ORE);
        cursedOres.add(BlockTypes.LIT_REDSTONE_ORE);
    }

    private static Set<BlockType> stealableFluids = new HashSet<>();

    static {
        stealableFluids.add(BlockTypes.WATER);
        stealableFluids.add(BlockTypes.FLOWING_WATER);
        stealableFluids.add(BlockTypes.LAVA);
        stealableFluids.add(BlockTypes.FLOWING_LAVA);
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        Optional<Player> optPlayer = event.getCause().get(NamedCause.SOURCE, Player.class);

        if (!optPlayer.isPresent()) {
            return;
        }

        Player player = optPlayer.get();
        Optional<CursedMineInstance> optInst = manager.getApplicableZone(player);

        if (!optInst.isPresent()) {
            return;
        }

        CursedMineInstance inst = optInst.get();
        Optional<ItemStack> optHeldItem = player.getItemInHand();

        if (!optHeldItem.isPresent()) {
            event.setCancelled(true);
            return;
        }

        for (Transaction<BlockSnapshot> block : event.getTransactions()) {
            BlockType originalType = block.getOriginal().getState().getType();
            if (cursedOres.contains(originalType)) {
                ItemStack held = optHeldItem.get();

                if (!(held.getItem() instanceof ItemPickaxe)) {
                    event.setCancelled(true);
                    break;
                }

                // Check to see if the block has already been broken
                // we were having some multi-firing problems
                if (inst.recordBlockBreak(player, new BlockRecord(block.getOriginal()))) {
                    if (Probability.getChance(4)) {
                        Collection<ItemStack> drops = new ArrayList<>();

                        Optional<ItemEnchantment> optFortune = EnchantmentUtil.getHighestEnchantment(
                                held,
                                Enchantments.FORTUNE
                        );

                        int times = 1;

                        if (optFortune.isPresent() && !DropRegistry.dropsSelf(originalType)) {
                            times += optFortune.get().getLevel();
                        }
                        for (int i = times; i > 0; --i) {
                            Collection<ItemStack> items = DropRegistry.createDropsFor(originalType, hasSilkTouch(held));
                            if (items != null) {
                                drops.addAll(items);
                            }
                        }

                        for (ItemStack stack : drops) {
                            stack.setQuantity(Math.min(
                                    stack.getQuantity() * Probability.getRangedRandom(4, 8),
                                    stack.getMaxStackQuantity()
                            ));
                            player.getInventory().offer(stack);
                        }

                        Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
                        if (optService.isPresent()) {
                            ModifierService service = optService.get();
                            if (service.isActive(Modifiers.DOUBLE_CURSED_ORES)) {
                                for (ItemStack stack : drops) {
                                    player.getInventory().offer(stack.copy());
                                }
                            }
                        }
                    }

                    Optional<Entity> optXPOrb = player.getWorld().createEntity(EntityTypes.EXPERIENCE_ORB, block.getOriginal().getLocation().get().getPosition());
                    if (optXPOrb.isPresent()) {
                        ExperienceOrb xpOrb = (ExperienceOrb) optXPOrb.get();
                        xpOrb.offer(Keys.HELD_EXPERIENCE, (70 - player.getLocation().getBlockY()) / 2);
                    }

                    /*if (Probability.getChance(3000)) {
                        ChatUtil.send(player, "You feel as though a spirit is trying to tell you something...");
                        player.getInventory().addItem(BookUtil.Lore.Areas.theGreatMine());
                    }*/

                    inst.eatFood(player);
                    inst.poison(player, 6);
                    inst.ghost(player, originalType);

                    // TODO Work around for the item dropped by blocks being indiscernible
                    // from items dropped from players
                    event.setCancelled(true);
                    Task.builder().execute(() -> {
                        block.getOriginal().getLocation().get().setBlockType(BlockTypes.AIR);
                    }).delayTicks(1).submit(SkreePlugin.inst());
                }
            } else if (stealableFluids.contains(originalType)) {
                inst.recordBlockBreak(player, new BlockRecord(block.getOriginal()));
            } else {
                event.setCancelled(true);
                break;
            }
        }
    }

    @Listener
    public void onBlockBurn(ChangeBlockEvent event) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockType originalType = transaction.getOriginal().getState().getType();
            BlockType finalType = transaction.getFinal().getState().getType();
            if (originalType != BlockTypes.PLANKS && originalType != BlockTypes.OAK_STAIRS && finalType != BlockTypes.FIRE) {
                continue;
            }

            if (finalType == BlockTypes.FIRE && event.getCause().first(PluginContainer.class).isPresent()) {
                continue;
            }

            if (manager.getApplicableZone(transaction.getOriginal().getLocation().get()).isPresent()) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event) {
        Optional<Player> optPlayer = event.getCause().get(NamedCause.SOURCE, Player.class);

        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();

            Optional<CursedMineInstance> optInst = manager.getApplicableZone(player);
            if (optInst.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onPlayerTeleport(DisplaceEntityEvent.Teleport.TargetPlayer event) {
        Player player = event.getTargetEntity();

        Optional<CursedMineInstance> optInst = manager.getApplicableZone(player);
        HitList hitList = manager.getHitList();
        if ((optInst.isPresent() && optInst.get().hasRecordForPlayer(player)) || hitList.isOnHitList(player)) {
            event.setCancelled(true);
            player.sendMessage(Text.of(TextColors.RED, "You have been tele-blocked!"));
        }
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        Optional<CursedMineInstance> optInst = manager.getApplicableZone(player);
        HitList hitList = manager.getHitList();
        if (optInst.isPresent() || hitList.isOnHitList(player)) {
            if (!optInst.isPresent()) {
                optInst = manager.getActiveZone();
            }

            if (optInst.isPresent()) {
                optInst.get().revertPlayer(player);
                optInst.get().clearCurses(player);
            }

            /*if (optInst.isPresent() && Probability.getChance(500)) {
                ChatUtil.send(player, "You feel as though a spirit is trying to tell you something...");
                event.getDrops().add(BookUtil.Lore.Areas.theGreatMine());
            }*/

            if (hitList.isOnHitList(player)) {
                hitList.remPlayer(player);
            }

            String deathMessage;
            switch (Probability.getRandom(11)) {
                case 1:
                    deathMessage = " was killed by Dave";
                    break;
                case 2:
                    deathMessage = " got on Dave's bad side";
                    break;
                case 3:
                    deathMessage = " was slain by an evil spirit";
                    break;
                case 4:
                    deathMessage = " needs to stay away from the cursed mine";
                    break;
                case 5:
                    deathMessage = " enjoys death a little too much";
                    break;
                case 6:
                    deathMessage = " seriously needs to stop mining";
                    break;
                case 7:
                    deathMessage = " angered an evil spirit";
                    break;
                case 8:
                    deathMessage = " doesn't get a cookie from COOKIE";
                    break;
                case 9:
                    deathMessage = " should stay away";
                    break;
                case 10:
                    deathMessage = " needs to consider retirement";
                    break;
                default:
                    deathMessage = "'s head is now on Dave's mantel";
                    break;
            }

            event.setMessage(Text.of(player.getName(), deathMessage));
            // addSkull(player);
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (manager.getHitList().isOnHitList(player)) {
            player.offer(Keys.HEALTH, 0D);
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        Optional<CursedMineInstance> optInst = manager.getApplicableZone(player);
        if (optInst.isPresent() && optInst.get().hasRecordForPlayer(player)) {
            manager.getHitList().addPlayer(player);
        }
    }
}
