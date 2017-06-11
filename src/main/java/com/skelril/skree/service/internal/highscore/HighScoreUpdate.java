/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.highscore;

import java.util.UUID;

public class HighScoreUpdate {
  private final UUID playerId;
  private final ScoreType scoreType;
  private final int value;

  public HighScoreUpdate(UUID playerId, ScoreType scoreType, int value) {
    this.playerId = playerId;
    this.scoreType = scoreType;
    this.value = value;
  }

  public void process() {
    if (scoreType.isIncremental()) {
      HighScoreDatabaseUtil.incrementalUpdate(playerId, scoreType, value);
    } else {
      HighScoreDatabaseUtil.overrideIfBetter(playerId, scoreType, value);
    }
  }
}
