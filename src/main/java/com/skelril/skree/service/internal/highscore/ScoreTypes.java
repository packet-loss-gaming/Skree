/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.highscore;

public class ScoreTypes {
  public static final ScoreType WILDERNESS_ORES_MINED = new ScoreType(0, true, ScoreType.Order.DESC);
  public static final ScoreType WILDERNESS_MOB_KILLS = new ScoreType(1, true, ScoreType.Order.DESC);
  public static final ScoreType WILDERNESS_DEATHS = new ScoreType(2, true, ScoreType.Order.DESC);
  public static final ScoreType JUNGLE_RAID_WINS = new ScoreType(3, true, ScoreType.Order.DESC);
  public static final ScoreType GOLD_RUSH_ROBBERIES = new ScoreType(4, true, ScoreType.Order.DESC);
  public static final ScoreType HIGHEST_CATACOMB_WAVE = new ScoreType(5, false, ScoreType.Order.DESC);
  public static final ScoreType GOLD_RUSH_LOOT_VALUE = new ScoreType(6, false, ScoreType.Order.DESC);
  public static final ScoreType FASTEST_TEMPLE_OF_FATE_RUN = new TimeBasedScoreType(7, false, ScoreType.Order.ASC);
}
