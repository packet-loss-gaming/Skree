/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness.wanderer;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.monster.Skeleton;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public abstract class SkeletonArcherWanderer implements WanderingBoss<Skeleton> {
  @Override
  public Entity createEntity(Location<World> location) {
    Skeleton skeleton = (Skeleton) location.getExtent().createEntity(getEntityType(), location.getPosition());
    skeleton.setItemInHand(HandTypes.MAIN_HAND, newItemStack(ItemTypes.BOW));
    return skeleton;
  }
}
