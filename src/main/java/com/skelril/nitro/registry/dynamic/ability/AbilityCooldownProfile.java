/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability;

public class AbilityCooldownProfile {
  private String pool;
  private boolean invertedActivation = false;
  private double seconds = -1;

  public String getPool() {
    return pool;
  }

  public boolean isAllowedWhileOnCooldown() {
    return invertedActivation;
  }

  public boolean isAllowedWhileOffCooldown() {
    return !invertedActivation;
  }

  public double getSeconds() {
    return seconds;
  }

  public boolean isEnforced() {
    return seconds >= 0;
  }
}
