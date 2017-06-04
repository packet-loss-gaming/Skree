/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.predicate;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;

import java.util.Collection;
import java.util.function.Predicate;

public class EntityTypePredicate implements Predicate<Entity> {

  private Collection<EntityType> entityTypes;

  public EntityTypePredicate(Collection<EntityType> entityTypes) {
    this.entityTypes = entityTypes;
  }

  @Override
  public boolean test(Entity entity) {
    return entityTypes.contains(entity.getType());
  }
}
