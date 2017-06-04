/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.resolver.point;

import com.skelril.nitro.point.ItemStackValueMapping;
import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Function;

public abstract class AbstractSlipperyPointResolver<PointType extends Comparable<PointType>> implements PointDropResolver {
  private int points = getBasePointCount();
  private ItemStackValueMapping<PointType> mapping;
  private Function<Integer, PointType> pointTypeFromInt;

  protected AbstractSlipperyPointResolver(ItemStackValueMapping<PointType> mapping, Function<Integer, PointType> pointTypeFromInt) {
    this.mapping = mapping;
    this.pointTypeFromInt = pointTypeFromInt;
  }

  public int getBasePointCount() {
    return 0;
  }

  @Override
  public void enqueue(double modifier) {
    points += Probability.getRandom(getMaxPoints(modifier));
  }

  @Override
  public Collection<ItemStack> flush() {
    Collection<ItemStack> results = mapping.satisfy(pointTypeFromInt.apply(points));
    points = getBasePointCount();
    return results;
  }
}
