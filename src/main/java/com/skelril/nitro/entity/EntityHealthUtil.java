/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;

public class EntityHealthUtil {

  public static double getHealth(Living living) {
    return living.get(Keys.HEALTH).get();
  }

  public static double getMaxHealth(Living living) {
    return living.get(Keys.MAX_HEALTH).get();
  }

  public static void heal(Living living, double amt) {
    double health = getHealth(living);
    double maxHealth = getMaxHealth(living);
    living.offer(Keys.HEALTH, Math.min(health + amt, maxHealth));
  }

  public static void forceDamage(Living living, double amt) {
    double health = getHealth(living);
    living.offer(Keys.HEALTH, Math.max(0, health - amt));
  }

  public static void toFullHealth(Living living) {
    living.offer(Keys.HEALTH, living.get(Keys.MAX_HEALTH).get());
  }

  public static void setMaxHealth(Living living, double amt) {
    setMaxHealth(living, amt, false);
  }

  public static void setMaxHealth(Living living, double amt, boolean fill) {
    if (amt < living.get(Keys.MAX_HEALTH).get()) {
      living.offer(Keys.HEALTH, amt);
    }

    living.offer(Keys.MAX_HEALTH, amt);
    if (fill) {
      toFullHealth(living);
    }
  }
}
