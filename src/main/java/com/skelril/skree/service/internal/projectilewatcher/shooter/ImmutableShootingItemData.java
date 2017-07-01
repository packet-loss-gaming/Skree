/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.projectilewatcher.shooter;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import static com.skelril.skree.service.ProjectileWatcherService.SHOOTING_ITEM_DATA_KEY;

public class ImmutableShootingItemData extends AbstractImmutableData<ImmutableShootingItemData, ShootingItemData> {

  private final ItemStackSnapshot snapshot;

  public ImmutableShootingItemData() {
    this(null);
  }

  public ImmutableShootingItemData(ItemStackSnapshot snapshot) {
    this.snapshot = snapshot;
    registerGetters();
  }

  public ImmutableOptionalValue<ItemStackSnapshot> value() {
    return (ImmutableOptionalValue<ItemStackSnapshot>) Sponge.getRegistry().getValueFactory().createOptionalValue(SHOOTING_ITEM_DATA_KEY, snapshot, ItemStackSnapshot.NONE).asImmutable();
  }

  @Override
  protected void registerGetters() {
    registerFieldGetter(SHOOTING_ITEM_DATA_KEY, this::value);
    registerKeyValue(SHOOTING_ITEM_DATA_KEY, this::value);
  }

  @Override
  public ShootingItemData asMutable() {
    return new ShootingItemData(this.snapshot);
  }

  @Override
  public int getContentVersion() {
    return 1;
  }

  @Override
  public DataContainer toContainer() {
    return DataContainer.createNew().set(SHOOTING_ITEM_DATA_KEY.getQuery(), this.snapshot);
  }
}
