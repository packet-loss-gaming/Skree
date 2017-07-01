/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;


import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import static org.spongepowered.api.data.DataQuery.of;
import static org.spongepowered.api.data.key.KeyFactory.makeOptionalKey;

public interface ProjectileWatcherService {
  void track(Projectile projectile, Cause cause);

  Key<OptionalValue<ItemStackSnapshot>> SHOOTING_ITEM_DATA_KEY = makeOptionalKey(
      new TypeToken<Optional<ItemStackSnapshot>>() {},
      new TypeToken<OptionalValue<ItemStackSnapshot>>() {},
      of("ShootingItem"),
      "skree:shooting_item",
      "Shooting Item"
  );
}
