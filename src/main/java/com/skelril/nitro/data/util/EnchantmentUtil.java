/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.data.util;


import com.google.common.collect.Lists;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class EnchantmentUtil {
  public static List<Enchantment> getEnchantment(CompositeValueStore<?, ?> valueStore, EnchantmentType enchantment) {
    Optional<List<Enchantment>> results = valueStore.get(Keys.ITEM_ENCHANTMENTS);
    if (results.isPresent()) {
      List<Enchantment> enchantments = results.get();
      return enchantments.stream().filter(e -> e.getType().equals(enchantment)).collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }

  public static Optional<Enchantment> getHighestEnchantment(CompositeValueStore<?, ?> valueStore, EnchantmentType enchantment) {
    List<Enchantment> enchantments = getEnchantment(valueStore, enchantment);
    if (!enchantments.isEmpty()) {
      return Optional.of(enchantments.stream().sorted(Comparator.comparingInt(Enchantment::getLevel)).findFirst().get());
    }
    return Optional.empty();
  }
}
