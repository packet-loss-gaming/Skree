/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.projectilewatcher.shooter;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import static com.skelril.skree.service.ProjectileWatcherService.SHOOTING_ITEM_DATA_KEY;

public class ShootingItemData extends AbstractData<ShootingItemData, ImmutableShootingItemData> {
  private ItemStackSnapshot snapshot;

  public ShootingItemData() {
    this(null);
  }

  public ShootingItemData(ItemStackSnapshot snapshot) {
    this.snapshot = snapshot;
    registerGettersAndSetters();
  }

  public OptionalValue<ItemStackSnapshot> value() {
    return Sponge.getRegistry().getValueFactory().createOptionalValue(SHOOTING_ITEM_DATA_KEY, snapshot, ItemStackSnapshot.NONE);
  }

  @Override
  protected void registerGettersAndSetters() {
    registerFieldGetter(SHOOTING_ITEM_DATA_KEY, () -> Optional.ofNullable(this.snapshot));
    registerFieldSetter(SHOOTING_ITEM_DATA_KEY, value -> this.snapshot = value.orElse(null));
    registerKeyValue(SHOOTING_ITEM_DATA_KEY, this::value);
  }

  @Override
  public Optional<ShootingItemData> fill(DataHolder dataHolder, MergeFunction overlap) {
    return Optional.empty(); // Yes, this should be implemented properly, but it isn't necessary currently.
  }

  @Override
  public Optional<ShootingItemData> from(DataContainer container) {
    if (!container.contains(SHOOTING_ITEM_DATA_KEY.getQuery())) {
      return Optional.empty();
    }

    this.snapshot = container.getSerializable(SHOOTING_ITEM_DATA_KEY.getQuery(), ItemStackSnapshot.class).orElse(null);

    return Optional.of(this);
  }

  @Override
  public ShootingItemData copy() {
    return new ShootingItemData(this.snapshot);
  }

  @Override
  public ImmutableShootingItemData asImmutable() {
    return new ImmutableShootingItemData(this.snapshot);
  }

  @Override
  public int getContentVersion() {
    return 1;
  }

  @Override
  public DataContainer toContainer() {
    return super.toContainer().set(SHOOTING_ITEM_DATA_KEY.getQuery(), this.snapshot);
  }
}