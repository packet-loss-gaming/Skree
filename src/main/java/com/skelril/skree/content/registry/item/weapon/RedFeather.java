/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.weapon;

import com.skelril.nitro.Clause;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.registry.item.DegradableItem;
import com.skelril.nitro.registry.item.ICustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.content.world.wilderness.WildernessTeleportCommand;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class RedFeather extends CustomItem implements ICustomItem, DegradableItem, EventAwareContent {

    @Override
    public String __getID() {
        return "red_feather";
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.COMBAT;
    }

    @Override
    public int __getMaxUses() {
        return 10000;
    }

    @Listener(order = Order.LATE)
    public void onEntityDamage(DamageEntityEvent event, @Getter(value = "getTargetEntity") Player player, @First DamageSource dmgSrc) {
        if (!isBlockable(dmgSrc)) {
            return;
        }

        Optional<Clause<Integer, Clause<ItemStack, Clause<Integer, Long>>>> optFeatherDetail = getHighestPoweredFeather(player);
        if (!optFeatherDetail.isPresent()) {
            return;
        }

        Clause<Integer, Clause<ItemStack, Clause<Integer, Long>>> featherDetail = optFeatherDetail.get();

        int redQ = featherDetail.getValue().getValue().getKey();
        final double dmg = event.getBaseDamage();
        final int k = (dmg > 80 ? 16 : dmg > 40 ? 8 : dmg > 20 ? 4 : 2);

        final double blockable = redQ * k;
        final double blocked = blockable - (blockable - dmg);

        event.setBaseDamage(Math.max(0, dmg - blocked));

        redQ = (int) ((blockable - blocked) / k);
        updateFeatherPower(featherDetail.getValue().getKey(), redQ, (long) blocked * 75);
        tf(player).inventory.mainInventory[featherDetail.getKey()] = tf(featherDetail.getValue().getKey());
    }

    public boolean isBlockable(DamageSource source) {
        if (source.getType() == WildernessTeleportCommand.DAMAGE_TYPE) {
            return false;
        }
        return true;
    }

    public Optional<Clause<Integer, Clause<ItemStack, Clause<Integer, Long>>>> getHighestPoweredFeather(Player player) {
        return getHighestPoweredFeather(tf(player));
    }

    public Optional<Clause<Integer, Clause<ItemStack, Clause<Integer, Long>>>> getHighestPoweredFeather(EntityPlayer player) {
        net.minecraft.item.ItemStack[] itemStacks = player.inventory.mainInventory;

        int index = -1;
        ItemStack stack = null;
        Clause<Integer, Long> details = new Clause<>(0, 0L);

        for (int i = 0; i < itemStacks.length; ++i) {
            ItemStack curStack = tf(itemStacks[i]);
            Clause<Integer, Long> powerCooldown = getFeatherPower(curStack);
            if (powerCooldown.getValue() > System.currentTimeMillis()) {
                return Optional.empty();
            }

            if (powerCooldown.getKey() > details.getKey()) {
                index = i;
                details = powerCooldown;
                stack = curStack;
            }
        }

        return index != -1 ? Optional.of(new Clause<>(index, new Clause<>(stack, details))) : Optional.empty();
    }

    public Clause<Integer, Long> getFeatherPower(net.minecraft.item.ItemStack stack) {
        if (stack != null && stack.getItem() == this) {
            long coolDown = 0;

            if (stack.getTagCompound() == null) {
                stack.setTagCompound(new NBTTagCompound());
            }

            if (stack.getTagCompound().hasKey("skree_feather_data")) {
                NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_feather_data");
                coolDown = tag.getLong("cool_down");
            }

            return new Clause<>((__getMaxUses() - stack.getItemDamage()) - 1, coolDown);
        }
        return new Clause<>(0, 0L);
    }

    public Clause<Integer, Long> getFeatherPower(ItemStack stack) {
        return getFeatherPower(tf(stack));
    }

    public void updateFeatherPower(net.minecraft.item.ItemStack stack, int newPower, long coolDown) {
        stack.setItemDamage(__getMaxUses() - (newPower + 1));

        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (!stack.getTagCompound().hasKey("skree_feather_data")) {
            stack.getTagCompound().setTag("skree_feather_data", new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_feather_data");
        tag.setLong("cool_down", System.currentTimeMillis() + coolDown);
    }

    public void updateFeatherPower(ItemStack stack, int newPower, long coolDown) {
        updateFeatherPower(tf(stack), newPower, coolDown);
    }
}
