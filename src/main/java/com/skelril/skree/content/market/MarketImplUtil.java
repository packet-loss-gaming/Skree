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
import com.skelril.skree.service.MarketService;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;

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

    public enum QueryMode {
        EVERYTHING,
        HOT_BAR,
        HELD
    }

    public static Clause<BigDecimal, List<Integer>> getChanges(Player player, MarketService service, QueryMode mode, Optional<ItemStack> filter) {
        EntityPlayer playerEnt = tf(player);

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Integer> ints = new ArrayList<>();

        int min, max;
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
                max = playerEnt.inventory.mainInventory.length;
                break;
            default:
                throw new IllegalArgumentException("Invalid query mode provided!");
        }

        for (int i = min; i < max; ++i) {
            net.minecraft.item.ItemStack stack = playerEnt.inventory.mainInventory[i];
            if (stack == null) {
                continue;
            }

            if (filter.isPresent()) {
                if (!ItemComparisonUtil.isSimilar(filter.get(), tf(stack))) {
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

                totalPrice = totalPrice.add(unitPrice.multiply(new BigDecimal(stack.stackSize)));
                ints.add(i);
            }
        }

        return new Clause<>(totalPrice, ints);
    }

    public static List<Clause<ItemStack, Integer>> removeAtPos(Player player, List<Integer> ints) {
        EntityPlayer playerEnt = tf(player);
        net.minecraft.item.ItemStack[] mainInv = playerEnt.inventory.mainInventory;
        List<Clause<ItemStack, Integer>> transactions = new ArrayList<>(ints.size());
        for (int i : ints) {
            transactions.add(new Clause<>(tf(mainInv[i]), -(mainInv[i].stackSize)));
            mainInv[i] = null;
        }
        return transactions;
    }

    public static boolean setBalanceTo(Player player, BigDecimal decimal, Cause cause) {
        EntityPlayer playerEnt = tf(player);
        net.minecraft.item.ItemStack[] mainInv = playerEnt.inventory.mainInventory;

        Collection<ItemStack> results = CofferValueMap.inst().satisfy(decimal.toBigInteger());
        Iterator<ItemStack> resultIt = results.iterator();

        // Loop through replacing empty slots and the old coffers with the new balance
        for (int i = 0; i < mainInv.length; ++i) {
            Optional<BigInteger> value = CofferValueMap.inst().getValue(Lists.newArrayList(tf(mainInv[i])));
            if (value.isPresent()) {
                mainInv[i] = null;
            }

            if (mainInv[i] == null && resultIt.hasNext()) {
                mainInv[i] = tf(resultIt.next());
                resultIt.remove();
            }
        }

        // Add remaining currency
        new ItemDropper(player.getLocation()).dropItems(results, cause);
        return true;
    }

    public static Clause<Boolean, List<Clause<ItemStack, Integer>>> giveItems(Player player, Collection<ItemStack> stacks, Cause cause) {
        EntityPlayer playerEnt = tf(player);
        net.minecraft.item.ItemStack[] mainInv = playerEnt.inventory.mainInventory;

        List<Clause<ItemStack, Integer>> transactions = new ArrayList<>(stacks.size());
        Iterator<ItemStack> stackIt = stacks.iterator();

        // Loop through replacing empty space with the requested items
        for (int i = 0; i < mainInv.length; ++i) {
            if (mainInv[i] == null) {
                if (!stackIt.hasNext()) {
                    break;
                }

                ItemStack next = stackIt.next();
                mainInv[i] = tf(next);
                transactions.add(new Clause<>(next, next.getQuantity()));

                stackIt.remove();
            }
        }

        // Add remaining transactions
        transactions.addAll(stacks.stream().map(stack -> new Clause<>(stack, stack.getQuantity())).collect(Collectors.toList()));
        new ItemDropper(player.getLocation()).dropItems(stacks, cause);

        return new Clause<>(true, transactions);
    }
}
