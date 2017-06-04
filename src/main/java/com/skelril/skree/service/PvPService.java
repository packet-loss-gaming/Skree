/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.skree.service.internal.pvp.PvPState;
import org.spongepowered.api.entity.living.player.Player;

public interface PvPService {
  void setPvPState(Player player, PvPState state);

  PvPState getPvPState(Player player);

  PvPState getDefaultState(Player player);
}
