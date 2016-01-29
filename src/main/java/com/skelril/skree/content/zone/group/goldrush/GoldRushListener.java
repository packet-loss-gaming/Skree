/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.goldrush;

import com.google.common.collect.Lists;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.registry.item.currency.CofferItem;
import com.skelril.skree.content.registry.item.currency.CofferValueMap;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class GoldRushListener {

    private final GoldRushManager manager;

    public GoldRushListener(GoldRushManager manager) {
        this.manager = manager;
    }

    private Map<String, Player> tileEntityClaimMap = new WeakHashMap<>();

    @Listener
    public void onPlayerInteractEvent(InteractBlockEvent.Secondary event) {
        Object obj = event.getCause().root();
        if (!(obj instanceof Player)) {
            return;
        }

        Player player = (Player) obj;

        Optional<GoldRushInstance> optInst = manager.getApplicableZone(player);
        if (!optInst.isPresent()) return;

        GoldRushInstance inst = optInst.get();

        BlockSnapshot snapshot = event.getTargetBlock();
        BlockState state = snapshot.getState();
        Location<World> targetBlock = snapshot.getLocation().get();
        if (state.getType() == BlockTypes.WALL_SIGN && inst.getLockLocations().contains(targetBlock)) {
            List<Text> texts = snapshot.get(Keys.SIGN_LINES).orElse(Lists.newArrayList(Text.EMPTY, Text.EMPTY, Text.EMPTY, Text.EMPTY));

            boolean unlocked = false;

            String text = texts.get(1).toPlain().toLowerCase();
            /*net.minecraft.item.ItemStack[] itemStacks = tf(player).inventory.mainInventory;
            for (int i = 0; i < itemStacks.length; ++i) {
                ItemStack is = itemStacks[i];

                if (is == null || is.getItem() != CustomItemTypes.GOLD_RUSH_KEY) {
                    continue;
                }

                if (text.contains("blue")) {
                    if (is.getItemDamage() != 1) {
                        continue;
                    }
                } else if (text.contains("red")) {
                    if (is.getItemDamage() != 0) {
                        continue;
                    }
                } else {
                    continue;
                }

                unlocked = true;
                itemStacks[i] = null;
                break;
            }*/

            for (Inventory inv : player.getInventory().query((ItemType) CustomItemTypes.GOLD_RUSH_KEY)) {
                Optional<org.spongepowered.api.item.inventory.ItemStack> optStack = inv.peek();
                if (optStack.isPresent()) {
                    if (text.contains("blue")) {
                        if (tf(optStack.get()).getItemDamage() != 1) {
                            continue;
                        }
                    } else if (text.contains("red")) {
                        if (tf(optStack.get()).getItemDamage() != 0) {
                            continue;
                        }
                    } else {
                        continue;
                    }

                    unlocked = true;
                    inv.poll();
                    break;
                }
            }

            if (unlocked) {
                tf(player).inventoryContainer.detectAndSendChanges();

                snapshot.with(
                        Keys.SIGN_LINES,
                        Lists.newArrayList(
                                texts.get(0),
                                texts.get(1),
                                Text.of("Locked"),
                                Text.of("- Unlocked -")
                        )
                ).orElse(snapshot).restore(true, false);
            }
        } else if (state.getType() == BlockTypes.LEVER) {
            Task.builder().execute(() -> {
                if (inst.checkLevers()) inst.unlockLevers();
            }).delayTicks(1).submit(SkreePlugin.inst());
        } else if (targetBlock.equals(inst.getRewardChestLoc()) && inst.isComplete()) {
            event.setCancelled(true);

            player.sendMessage(Text.of(TextColors.YELLOW, "You have successfully robbed the bank!"));
            inst.payPlayer(player);
        } else if (!inst.isLocked()) {
            if (state.getType() == BlockTypes.STONE_BUTTON) {
                inst.tryToStart();
            }
        }
    }

    @Listener
    public void onChestOpen(InteractInventoryEvent.Open event) {
        Object optPlayer = event.getCause().root();
        if (optPlayer instanceof Player) {
            Player player = (Player) optPlayer;

            Optional<GoldRushInstance> optInst = manager.getApplicableZone(player);
            if (!optInst.isPresent()) return;

            GoldRushInstance inst = optInst.get();

            Inventory inventory = event.getTargetInventory();
            if (!inst.isLocked() && inventory instanceof ContainerChest) {
                IInventory chestInv = ((ContainerChest) inventory).getLowerChestInventory();
                if (chestInv instanceof ILockableContainer) {
                    LockCode newLockCode = new LockCode(UUID.randomUUID().toString());
                    tileEntityClaimMap.put(newLockCode.getLock(), player);
                    ((ILockableContainer) chestInv).setLockCode(newLockCode);

                    BigDecimal risk = Optional.ofNullable(
                            inst.cofferRisk.get(player.getUniqueId())
                    ).orElse(BigDecimal.ZERO);

                    Collection<org.spongepowered.api.item.inventory.ItemStack> queue = CofferValueMap.inst().satisfy(risk.toBigInteger());
                    Iterator<org.spongepowered.api.item.inventory.ItemStack> it = queue.iterator();
                    for (int i = 0; i < chestInv.getSizeInventory(); ++i) {
                        if (it.hasNext()) {
                            chestInv.setInventorySlotContents(i, tf(it.next()));
                            continue;
                        }
                        chestInv.setInventorySlotContents(i, null);
                    }
                }
            }

        }
    }

    @Listener
    public void onChestClose(InteractInventoryEvent.Close event) {
        Inventory inventory = event.getTargetInventory();
        if (inventory instanceof ContainerChest) {
            IInventory chestInv = ((ContainerChest) inventory).getLowerChestInventory();
            if (chestInv instanceof ILockableContainer) {
                String lockCode = ((ILockableContainer) chestInv).getLockCode().getLock();
                Optional<Player> optPlayer = Optional.ofNullable(tileEntityClaimMap.remove(lockCode));
                if (optPlayer.isPresent()) {
                    Player player = optPlayer.get();
                    ((ILockableContainer) chestInv).setLockCode(LockCode.EMPTY_CODE);

                    Optional<GoldRushInstance> optInst = manager.getApplicableZone(player);
                    if (!optInst.isPresent()) return;

                    // TODO Sponge port
                    GoldRushInstance inst = optInst.get();
                    List<org.spongepowered.api.item.inventory.ItemStack> toEvaluate = new ArrayList<>();
                    ArrayDeque<org.spongepowered.api.item.inventory.ItemStack> toReturn = new ArrayDeque<>();

                    for (int i = 0; i < chestInv.getSizeInventory(); ++i) {
                        ItemStack stack = chestInv.getStackInSlot(i);
                        if (stack == null) {
                            continue;
                        }

                        if (stack.getItem() instanceof CofferItem) {
                            toEvaluate.add(tf(stack));
                        } else {
                            toReturn.add(tf(stack));
                        }
                        chestInv.setInventorySlotContents(i, null);
                    }

                    BigDecimal value = BigDecimal.ZERO;
                    for (org.spongepowered.api.item.inventory.ItemStack stack : toEvaluate) {
                        value = value.add(new BigDecimal(
                                CofferValueMap.inst().getValue(Collections.singleton(stack)).orElse(BigInteger.ZERO)
                        ));
                    }

                    inst.cofferRisk.put(
                            player.getUniqueId(),
                            value
                    );

                    for (Inventory slot : player.getInventory().slots()) {
                        if (toReturn.isEmpty()) {
                            break;
                        }

                        if (slot.size() == 0) {
                            slot.set(toReturn.poll());
                        }
                    }

                    if (!toReturn.isEmpty()) {
                        new ItemDropper(player.getLocation()).dropItems(toReturn, Cause.of(inst));
                    }

                    player.sendMessage(Text.of(TextColors.YELLOW, "You are now risking ", value, " coffers."));
                }
            }
        }
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        Optional<GoldRushInstance> optInst = manager.getApplicableZone(player);
        if (optInst.isPresent()) {
            String deathMessage;
            switch (Probability.getRandom(6)) {
                case 1:
                    deathMessage = " needs to find a new profession";
                    break;
                case 2:
                    deathMessage = " is now at the mercy of Hallow";
                    break;
                case 3:
                    deathMessage = " is now folding and hanging... though mostly hanging...";
                    break;
                case 4:
                    if (event.getMessage().orElse(Text.EMPTY).toPlain().contains("drown")) {
                        deathMessage = " discovered H2O is not always good for ones health";
                        break;
                    }
                case 5:
                    if (event.getMessage().orElse(Text.EMPTY).toPlain().contains("starved")) {
                        deathMessage = " should take note of the need to bring food with them";
                        break;
                    }
                default:
                    deathMessage = " was killed by police while attempting to rob a bank";
                    break;
            }
            event.setMessage(Text.of(player.getName(), deathMessage));

            player.sendMessage(Text.of(TextColors.YELLOW, "Your partner posted bail as promised."));
        }
    }
}
