/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.goldrush;

import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.registry.item.currency.CofferItem;
import com.skelril.skree.content.registry.item.currency.CofferValueMap;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static com.skelril.skree.content.market.MarketImplUtil.format;

public class GoldRushListener {

  private final GoldRushManager manager;

  public GoldRushListener(GoldRushManager manager) {
    this.manager = manager;
  }

  private Map<String, Player> tileEntityClaimMap = new WeakHashMap<>();

  private BlockType[] allowedChanges = {
      BlockTypes.REDSTONE_LAMP, BlockTypes.LIT_REDSTONE_LAMP,
      BlockTypes.FLOWING_LAVA, BlockTypes.LAVA,
      BlockTypes.FLOWING_WATER, BlockTypes.WATER
  };

  private boolean isAllowedChange(BlockType originalType, BlockType finalType) {
    if (originalType != finalType) {
      for (int i = 0; i < allowedChanges.length - 1; ++i) {
        if (originalType == allowedChanges[i] && finalType == allowedChanges[i + 1]) {
          return true;
        }
        if (originalType == allowedChanges[i + 1] && finalType == allowedChanges[i]) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  @Listener
  public void onBlockChange(ChangeBlockEvent event) {
    Optional<Player> player = event.getCause().get(NamedCause.SOURCE, Player.class);
    for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
      if (manager.getApplicableZone(transaction.getOriginal().getLocation().get()).isPresent()) {
        BlockType originalType = transaction.getOriginal().getState().getType();
        BlockType finalType = transaction.getFinal().getState().getType();
        if (player.isPresent()) {
          if (!isAllowedChange(originalType, finalType)) {
            event.setCancelled(true);
            break;
          }
        } else {
          if (originalType == BlockTypes.LEVER && finalType != BlockTypes.LEVER) {
            event.setCancelled(true);
            break;
          }
        }
      }
    }
  }

  @Listener(order = Order.FIRST)
  public void onPlayerInteractEvent(InteractBlockEvent.Secondary.MainHand event, @Root Player player) {
    Optional<GoldRushInstance> optInst = manager.getApplicableZone(player);
    if (!optInst.isPresent()) {
      return;
    }

    GoldRushInstance inst = optInst.get();

    BlockSnapshot snapshot = event.getTargetBlock();
    BlockState state = snapshot.getState();

    if (!snapshot.getLocation().isPresent()) {
      return;
    }

    Location<World> targetBlock = snapshot.getLocation().get();
    if (state.getType() == BlockTypes.WALL_SIGN && inst.getLockLocations().contains(targetBlock)) {
      Optional<TileEntity> optTileEnt = snapshot.getLocation().get().getTileEntity();

      if (!optTileEnt.isPresent()) {
        return;
      }

      TileEntity tileEntity = optTileEnt.get();

      Optional<List<Text>> optTexts = tileEntity.get(Keys.SIGN_LINES);

      if (!optTexts.isPresent()) {
        return;
      }

      List<Text> texts = optTexts.get();

      boolean unlocked = false;

      String text = texts.get(1).toPlain().toLowerCase();
      net.minecraft.item.ItemStack[] itemStacks = tf(player).inventory.mainInventory;
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
      }

      /*
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
      */

      if (unlocked) {
        tf(player).inventoryContainer.detectAndSendChanges();

        texts.set(2, Text.of("Locked"));
        texts.set(3, Text.of("- Unlocked -"));
        tileEntity.offer(Keys.SIGN_LINES, texts);
      }
    } else if (state.getType() == BlockTypes.LEVER) {
      Task.builder().execute(() -> {
        if (inst.checkLevers()) {
          inst.completeGame();
        }
      }).delayTicks(1).submit(SkreePlugin.inst());
    } else if (targetBlock.equals(inst.getRewardChestLoc()) && inst.isComplete()) {
      event.setUseItemResult(Tristate.FALSE);
      event.setUseBlockResult(Tristate.FALSE);

      player.sendMessage(Text.of(TextColors.YELLOW, "You have successfully robbed the bank!"));
      inst.payPlayer(player);
    } else if (!inst.isLocked()) {
      if (state.getType() == BlockTypes.STONE_BUTTON) {
        inst.tryToStart();
      }
    }
  }

  @Listener
  public void onChestOpen(InteractInventoryEvent.Open event, @Root Player player) {
    Optional<GoldRushInstance> optInst = manager.getApplicableZone(player);
    if (!optInst.isPresent()) {
      return;
    }

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
          if (!optInst.isPresent()) {
            return;
          }

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
            new ItemDropper(player.getLocation()).dropStacks(toReturn, SpawnTypes.PLUGIN);
          }

          player.sendMessage(Text.of(TextColors.YELLOW, "You are now risking ", format(value), " coffers."));

          MessageChannel targetChannel = inst.getPlayerMessageChannel(PlayerClassifier.SPECTATOR);
          targetChannel.send(Text.of(TextColors.YELLOW, "Group risk of ", format(inst.getTotalRisk()), " coffers."));
          targetChannel.send(Text.of(TextColors.YELLOW, "Risk of lava ", inst.getChanceOfLava(), " / 100."));
          targetChannel.send(Text.of(TextColors.YELLOW, "Security system delay ", inst.getBaseTime(), " +/- ", inst.getTimeVariance(), " seconds."));
        }
      }
    }
  }

  @Listener
  public void onPlayerTeleport(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player player) {
    Optional<GoldRushInstance> optInst = manager.getApplicableZone(event.getFromTransform().getLocation());
    if (optInst.isPresent() && !manager.getApplicableZone(event.getToTransform().getLocation()).isPresent()) {
      GoldRushInstance inst = optInst.get();

      inst.invalidate(player);
      inst.tryInventoryRestore(player);
    }
  }

  @Listener
  public void onClientLeave(ClientConnectionEvent.Disconnect event) {
    Player player = event.getTargetEntity();
    Optional<GoldRushInstance> optInst = manager.getApplicableZone(player);
    if (optInst.isPresent()) {
      GoldRushInstance inst = optInst.get();

      inst.invalidate(player);
    }
  }

  @Listener
  public void onPlayerDeath(DestructEntityEvent.Death event, @Getter("getTargetEntity") Player player) {
    Optional<GoldRushInstance> optInst = manager.getApplicableZone(player);
    if (optInst.isPresent()) {
      GoldRushInstance inst = optInst.get();
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
          if (event.getMessage().toPlain().contains("drown")) {
            deathMessage = " discovered H2O is not always good for ones health";
            break;
          }
        case 5:
          if (event.getMessage().toPlain().contains("starved")) {
            deathMessage = " should take note of the need to bring food with them";
            break;
          }
        default:
          deathMessage = " was killed by police while attempting to rob a bank";
          break;
      }
      event.setMessage(Text.of(player.getName(), deathMessage));

      inst.invalidate(player);
      player.sendMessage(Text.of(TextColors.YELLOW, "Your partner posted bail as promised."));
    }
  }
}
