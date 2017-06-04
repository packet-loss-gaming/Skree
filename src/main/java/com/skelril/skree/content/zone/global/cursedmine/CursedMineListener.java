/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine;

import com.google.common.collect.Lists;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.content.modifier.Modifiers;
import com.skelril.skree.content.zone.global.cursedmine.hitlist.HitList;
import com.skelril.skree.content.zone.global.cursedmine.restoration.BlockRecord;
import com.skelril.skree.service.ModifierService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.BlockSpawnCause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Named;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class CursedMineListener {
    
    private final CursedMineManager manager;

    public CursedMineListener(CursedMineManager manager) {
        this.manager = manager;
    }

    private static Set<BlockType> triggerBlocks = new HashSet<>();

    static {
        triggerBlocks.add(BlockTypes.STONE_BUTTON);
        triggerBlocks.add(BlockTypes.TRIPWIRE);
    }

    @Listener
    public void onPlayerInteract(ChangeBlockEvent event) {
        for (Transaction<BlockSnapshot> block : event.getTransactions()) {
            BlockSnapshot snapshot = block.getOriginal();
            Optional<CursedMineInstance> optInst = manager.getApplicableZone(snapshot);
            if (optInst.isPresent() && triggerBlocks.contains(snapshot.getState().getType())) {
                optInst.get().activatePumps();
                break;
            }
        }
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
    public void onBlockBreak(ChangeBlockEvent.Break event, @Named(NamedCause.SOURCE) Player player) {
        Optional<CursedMineInstance> optInst = manager.getApplicableZone(player);

        if (!optInst.isPresent()) {
            return;
        }

        CursedMineInstance inst = optInst.get();
        Optional<ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (!optHeldItem.isPresent()) {
            event.setCancelled(true);
            return;
        }

        for (Transaction<BlockSnapshot> block : event.getTransactions()) {
            BlockType originalType = block.getOriginal().getState().getType();
            if (cursedOres.contains(originalType)) {
                // Check to see if the block has already been broken
                // we were having some multi-firing problems
                if (inst.recordBlockBreak(player, new BlockRecord(block.getOriginal()))) {
                    /*if (Probability.getChance(3000)) {
                        ChatUtil.send(player, "You feel as though a spirit is trying to tell you something...");
                        player.getInventory().addItem(BookUtil.Lore.Areas.theGreatMine());
                    }*/

                    ExperienceOrb xpOrb = (ExperienceOrb) player.getWorld().createEntity(EntityTypes.EXPERIENCE_ORB, block.getOriginal().getLocation().get().getPosition());
                    xpOrb.offer(Keys.CONTAINED_EXPERIENCE, (70 - player.getLocation().getBlockY()) / 2);

                    inst.eatFood(player);
                    inst.poison(player, 6);
                    inst.ghost(player, originalType);
                }
            } else if (stealableFluids.contains(originalType)) {
                inst.recordBlockBreak(player, new BlockRecord(block.getOriginal()));
            } else {
                block.setCustom(block.getOriginal());
            }
        }
    }

    @Listener
    public void onItemDrop(
            DropItemEvent.Destruct event,
            @Named(NamedCause.SOURCE) BlockSpawnCause spawnCause,
            @Named(NamedCause.NOTIFIER) Player player
    ) {
        if (!Probability.getChance(4)) {
            return;
        }

        BlockSnapshot blockSnapshot = spawnCause.getBlockSnapshot();

        Optional<Location<World>> optLocation = blockSnapshot.getLocation();
        if (!optLocation.isPresent()) {
            return;
        }

        Location<World> loc = optLocation.get();
        Optional<CursedMineInstance> optInst = manager.getApplicableZone(loc);

        if (!optInst.isPresent()) {
            return;
        }

        CursedMineInstance inst = optInst.get();
        if (!inst.hasrecordForPlayerAt(player, loc)) {
            return;
        }

        List<ItemStackSnapshot> itemStacks = new ArrayList<>();
        Iterator<Entity> entityIterator = event.getEntities().iterator();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            if (entity instanceof Item) {
                ItemStackSnapshot snapshot = ((Item) entity).item().get();
                itemStacks.add(snapshot);
                entityIterator.remove();
            }
        }

        int times = 1;

        Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
        if (optService.isPresent()) {
            ModifierService service = optService.get();
            if (service.isActive(Modifiers.DOUBLE_CURSED_ORES)) {
                times *= 2;
            }
        }

        for (ItemStackSnapshot stackSnapshot : itemStacks) {
            int quantity = Math.min(
                    stackSnapshot.getCount() * Probability.getRangedRandom(4, 8),
                    stackSnapshot.getType().getMaxStackQuantity()
            );

            for (int i = 0; i < times; ++i) {
                ItemStack stack = stackSnapshot.createStack();
                stack.setQuantity(quantity);
                player.getInventory().offer(stack);
            }
        }
    }

    private boolean isCausedbyFire(ChangeBlockEvent event) {
        Optional<LocatableBlock> optLocatableBlock = event.getCause().get(NamedCause.SOURCE, LocatableBlock.class);
        return optLocatableBlock.filter(locatableBlock -> locatableBlock.getBlockState().getType() == BlockTypes.FIRE).isPresent();
    }

    @Listener
    public void onBlockBurn(ChangeBlockEvent event) {
        boolean isCausedByFire = isCausedbyFire(event);

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockType finalType = transaction.getFinal().getState().getType();
            if (!isCausedByFire && finalType != BlockTypes.FIRE) {
                continue;
            }

            if (event.getCause().first(PluginContainer.class).isPresent()) {
                continue;
            }

            if (manager.getApplicableZone(transaction.getOriginal().getLocation().get()).isPresent()) {
                event.setCancelled(true);
                break;
            }
        }
    }

    private boolean isRedstoneTransition(BlockType originalType, BlockType finalType) {
        List<BlockType> redstoneOres = Lists.newArrayList(BlockTypes.REDSTONE_ORE, BlockTypes.LIT_REDSTONE_ORE);

        return redstoneOres.contains(originalType) && redstoneOres.contains(finalType);
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Named(NamedCause.SOURCE) Player player) {
        Optional<CursedMineInstance> optInst = manager.getApplicableZone(player);
        if (!optInst.isPresent()) {
            return;
        }

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockType originalType = transaction.getFinal().getState().getType();
            BlockType finalType = transaction.getFinal().getState().getType();

            if (isRedstoneTransition(originalType, finalType)) {
                continue;
            }

            event.setCancelled(true);
            break;
        }
    }

    @Listener
    public void onPlayerTeleport(MoveEntityEvent.Teleport event) {
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;

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
