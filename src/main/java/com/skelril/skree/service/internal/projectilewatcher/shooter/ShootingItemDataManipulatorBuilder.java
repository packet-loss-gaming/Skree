/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.projectilewatcher.shooter;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import static com.skelril.skree.service.ProjectileWatcherService.SHOOTING_ITEM_DATA_KEY;

public class ShootingItemDataManipulatorBuilder implements DataManipulatorBuilder<ShootingItemData, ImmutableShootingItemData> {
  @Override
  public ShootingItemData create() {
    return new ShootingItemData();
  }

  @Override
  public Optional<ShootingItemData> createFrom(DataHolder dataHolder) {
    return Optional.of(dataHolder.get(ShootingItemData.class).orElse(new ShootingItemData()));
  }

  @Override
  public Optional<ShootingItemData> build(DataView container) throws InvalidDataException {
    if (container.contains(SHOOTING_ITEM_DATA_KEY)) {
      final ItemStackSnapshot snapshot = container.getSerializable(SHOOTING_ITEM_DATA_KEY.getQuery(), ItemStackSnapshot.class).orElse(null);
      return Optional.of(new ShootingItemData(snapshot));
    }

    return Optional.empty();
  }
}
