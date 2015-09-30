/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.data.util;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.item.Enchantment;

import java.util.List;


public class EnchantmentUtil {
    public static List<ItemEnchantment> getEnchantment(CompositeValueStore<?, ?> valueStore, Enchantment enchantment) {
        /* Optional<List<ItemEnchantment>> results = valueStore.get(Keys.ITEM_ENCHANTMENTS);
        if (results.isPresent()) {
            List<ItemEnchantment> enchantments = results.get();
            return enchantments.stream().filter(e -> e.getEnchantment().equals(enchantment)).collect(Collectors.toList());
        }
        */
        return Lists.newArrayList();
    }

    public static Optional<ItemEnchantment> getHighestEnchantment(CompositeValueStore<?, ?> valueStore, Enchantment enchantment) {
        List<ItemEnchantment> enchantments = getEnchantment(valueStore, enchantment);
        if (!enchantments.isEmpty()) {
            return Optional.of(enchantments.stream().sorted((a, b) -> a.getLevel() - b.getLevel()).findFirst().get());
        }
        return Optional.absent();
    }
}
