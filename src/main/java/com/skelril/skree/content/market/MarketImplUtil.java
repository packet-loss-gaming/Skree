/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.item.ItemComparisonUtil;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.skree.content.registry.item.currency.CofferValueMap;
import com.skelril.skree.content.world.build.BuildWorldWrapper;
import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.WorldService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.NonNullList;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class MarketImplUtil {
  public static String format(BigDecimal decimal) {
    DecimalFormat df = new DecimalFormat("#,###.##");
    return df.format(decimal);
  }

  public static BigDecimal getMoney(Player player) {
    EntityPlayer playerEnt = tf(player);
    BigInteger totalValue = BigInteger.ZERO;
    for (net.minecraft.item.ItemStack stack : playerEnt.inventory.mainInventory) {
      Optional<BigInteger> value = CofferValueMap.inst().getValue(Lists.newArrayList(tf(stack)));
      if (value.isPresent()) {
        totalValue = totalValue.add(value.get());
      }
    }
    return new BigDecimal(totalValue);
  }

  public static boolean canBuyOrSell(Player player) {
    Optional<WorldService> optService = Sponge.getServiceManager().provide(WorldService.class);
    if (optService.isPresent()) {
      WorldService service = optService.get();
      Collection<World> okayWorlds = new HashSet<>();
      okayWorlds.addAll(service.getEffectWrapper(MainWorldWrapper.class).get().getWorlds());
      okayWorlds.addAll(service.getEffectWrapper(BuildWorldWrapper.class).get().getWorlds());
      return okayWorlds.contains(player.getWorld());
    }
    return true;
  }

  public enum QueryMode {
    EVERYTHING,
    HOT_BAR,
    HELD
  }

  public static Clause<BigDecimal, List<Integer>> getChanges(Player player, MarketService service, QueryMode mode, @Nullable ItemStack filter) {
    EntityPlayer playerEnt = tf(player);

    BigDecimal totalPrice = BigDecimal.ZERO;
    List<Integer> ints = new ArrayList<>();

    int min;
    int max;
    switch (mode) {
      case HELD:
        min = playerEnt.inventory.currentItem;
        max = min + 1;
        break;
      case HOT_BAR:
        min = 0;
        max = 9;
        break;
      case EVERYTHING:
        min = 0;
        max = playerEnt.inventory.mainInventory.size();
        break;
      default:
        throw new IllegalArgumentException("Invalid query mode provided!");
    }

    for (int i = min; i < max; ++i) {
      net.minecraft.item.ItemStack stack = playerEnt.inventory.mainInventory.get(i);
      if (stack == net.minecraft.item.ItemStack.EMPTY) {
        continue;
      }

      if (filter != null) {
        if (!ItemComparisonUtil.isSimilar(filter, tf(stack))) {
          continue;
        }
      }

      Optional<BigDecimal> optPrice = service.getPrice(tf(stack));
      if (optPrice.isPresent()) {
        double percentageSale = 1;
        if (stack.isItemStackDamageable()) {
          percentageSale = 1 - ((double) stack.getItemDamage() / (double) stack.getMaxDamage());
        }

        BigDecimal unitPrice = optPrice.get().multiply(new BigDecimal(percentageSale));
        unitPrice = unitPrice.multiply(service.getSellFactor(unitPrice));

        totalPrice = totalPrice.add(unitPrice.multiply(new BigDecimal(stack.getCount())));
        ints.add(i);
      }
    }

    return new Clause<>(totalPrice, ints);
  }

  public static List<Clause<ItemStack, Integer>> removeAtPos(Player player, List<Integer> ints) {
    EntityPlayer playerEnt = tf(player);
    NonNullList<net.minecraft.item.ItemStack> mainInv = playerEnt.inventory.mainInventory;
    List<Clause<ItemStack, Integer>> transactions = new ArrayList<>(ints.size());
    for (int i : ints) {
      Clause<ItemStack, Integer> existingTransaction = null;
      for (Clause<ItemStack, Integer> transaction : transactions) {
        if (ItemComparisonUtil.isSimilar(transaction.getKey(), tf(mainInv.get(i)))) {
          existingTransaction = transaction;
          break;
        }
      }

      if (existingTransaction != null) {
        existingTransaction.setValue(existingTransaction.getValue() + -(mainInv.get(i).getCount()));
      } else {
        transactions.add(new Clause<>(tf(mainInv.get(i)), -(mainInv.get(i).getCount())));
      }

      mainInv.set(i, net.minecraft.item.ItemStack.EMPTY);
    }
    return transactions;
  }

  public static boolean setBalanceTo(Player player, BigDecimal decimal, Cause cause) {
    EntityPlayer playerEnt = tf(player);
    NonNullList<net.minecraft.item.ItemStack> mainInv = playerEnt.inventory.mainInventory;

    Collection<ItemStack> results = CofferValueMap.inst().satisfy(decimal.toBigInteger());
    Iterator<ItemStack> resultIt = results.iterator();

    // Loop through replacing empty slots and the old coffers with the new balance
    for (int i = 0; i < mainInv.size(); ++i) {
      Optional<BigInteger> value = CofferValueMap.inst().getValue(Lists.newArrayList(tf(mainInv.get(i))));
      if (value.isPresent()) {
        mainInv.set(i, net.minecraft.item.ItemStack.EMPTY);
      }

      if (mainInv.get(i) == net.minecraft.item.ItemStack.EMPTY && resultIt.hasNext()) {
        mainInv.set(i, tf(resultIt.next()));
        resultIt.remove();
      }
    }

    // Add remaining currency
    new ItemDropper(player.getLocation()).dropStacks(results, SpawnTypes.PLUGIN);
    return true;
  }

  public static Clause<Boolean, List<Clause<ItemStack, Integer>>> giveItems(Player player, Collection<ItemStack> stacks, Cause cause) {
    List<Clause<ItemStack, Integer>> transactions = new ArrayList<>(stacks.size());
    List<ItemStackSnapshot> itemBuffer = new ArrayList<>();
    itemBuffer.addAll(stacks.stream().map(ItemStack::createSnapshot).collect(Collectors.toList()));

    PlayerInventory playerInventory = player.getInventory().query(PlayerInventory.class);
    List<Inventory> inventories = new ArrayList<>();
    inventories.add(playerInventory.getHotbar());
    inventories.add(playerInventory.getMain());

    // Loop through replacing empty space with the requested items
    for (Inventory inventory : inventories) {
      List<ItemStackSnapshot> newBuffer = new ArrayList<>();
      for (ItemStackSnapshot snapshot : itemBuffer) {
        ItemStack stack = snapshot.createStack();

        InventoryTransactionResult result = inventory.offer(stack);
        newBuffer.addAll(result.getRejectedItems());

        transactions.add(new Clause<>(stack, stack.getQuantity()));
      }
      itemBuffer = newBuffer;
    }

    // Drop remaining items
    new ItemDropper(player.getLocation()).dropStackSnapshots(itemBuffer, SpawnTypes.PLUGIN);

    return new Clause<>(true, transactions);
  }
}
