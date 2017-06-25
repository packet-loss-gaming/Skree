/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.seteffect;

import java.util.Collection;
import java.util.Set;

public class ArmorSetEffectConfig extends SetEffectConfig {
  private Set<String> matchingHelmets;
  private Set<String> matchingChestplates;
  private Set<String> matchingLeggings;
  private Set<String> matchingBoots;

  public Collection<String> getMatchingHelmets() {
    return matchingHelmets;
  }

  public Collection<String> getMatchingChestplates() {
    return matchingChestplates;
  }

  public Collection<String> getMatchingLeggings() {
    return matchingLeggings;
  }

  public Collection<String> getMatchingBoots() {
    return matchingBoots;
  }
}
