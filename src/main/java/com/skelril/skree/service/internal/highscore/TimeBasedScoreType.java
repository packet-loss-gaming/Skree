/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.highscore;

import com.google.common.base.Joiner;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class TimeBasedScoreType extends ScoreType {
  protected TimeBasedScoreType(int id, boolean incremental, Order order) {
    super(id, incremental, order);
  }

  public String format(int score) {
    Duration duration = Duration.ofSeconds(score);

    List<String> components = new ArrayList<>();

    if (duration.toHours() > 0) {
      components.add(duration.toHours() + " hours");
      duration = duration.minusHours(duration.toHours());
    }
    if (duration.toMinutes() > 0) {
      components.add(duration.toMinutes() + " minutes");
      duration = duration.minusMinutes(duration.toMinutes());
    }
    if (duration.getSeconds() > 0) {
      components.add(duration.getSeconds() + " seconds");
    }

    return Joiner.on(' ').join(components);
  }
}
