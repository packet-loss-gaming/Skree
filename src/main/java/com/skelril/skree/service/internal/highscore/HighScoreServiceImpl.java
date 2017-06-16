/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.highscore;

import com.skelril.nitro.Clause;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.HighScoreService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HighScoreServiceImpl implements HighScoreService {
  private Lock highScoreLock = new ReentrantLock();
  private List<HighScoreUpdate> highScoreUpdates = new ArrayList<>();

  public HighScoreServiceImpl() {
    Task.builder().execute(() -> {
      highScoreLock.lock();

      List<HighScoreUpdate> scoresToUpdate;
      try {
        scoresToUpdate = highScoreUpdates;
        highScoreUpdates = new ArrayList<>();
      } finally {
        highScoreLock.unlock();
      }

      scoresToUpdate.forEach(HighScoreUpdate::process);
    }).intervalTicks(20).async().submit(SkreePlugin.inst());
  }

  @Override
  public Optional<Integer> get(Player player, ScoreType scoreType) {
    return HighScoreDatabaseUtil.get(player.getUniqueId(), scoreType);
  }

  private void queueUpdate(HighScoreUpdate update) {
    highScoreLock.lock();
    try {
      highScoreUpdates.add(update);
    } finally {
      highScoreLock.unlock();
    }
  }

  @Override
  public void update(Player player, ScoreType scoreType, int value) {
    queueUpdate(new HighScoreUpdate(player.getUniqueId(), scoreType, value));
  }

  @Override
  public List<Clause<Optional<GameProfile>, Integer>> getTop(ScoreType scoreType, int amt) {
    return HighScoreDatabaseUtil.getTop(scoreType, amt);
  }
}
