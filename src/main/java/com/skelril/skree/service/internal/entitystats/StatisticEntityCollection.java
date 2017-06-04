/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.entitystats;

import org.spongepowered.api.entity.Entity;

import java.util.Collection;
import java.util.function.Predicate;

public interface StatisticEntityCollection {
  Collection<Entity> getEntities();

  default int getCountOf(Predicate<Entity> predicate) {
    return (int) getEntities().stream().filter(predicate).count();
  }

  default float getPercentComposition(Predicate<Entity> predicate) {
    return (float) getCountOf(predicate) / getEntities().size();
  }
}
