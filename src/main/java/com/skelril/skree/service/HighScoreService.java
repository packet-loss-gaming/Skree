/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.highscore.ScoreType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;

import java.util.List;
import java.util.Optional;

public interface HighScoreService {
  Optional<Integer> get(Player player, ScoreType scoreType);
  void update(Player player, ScoreType scoreType, int value);

  default List<Clause<Optional<GameProfile>, Integer>> getTop(ScoreType scoreType) {
    return getTop(scoreType, 5);
  }

  List<Clause<Optional<GameProfile>, Integer>> getTop(ScoreType scoreType, int amt);
}
