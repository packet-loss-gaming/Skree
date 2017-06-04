/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.curse;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.entity.living.player.Player;

import java.util.function.Consumer;

public class SlapCurse implements Consumer<Player> {
  @Override
  public void accept(Player player) {
    player.setVelocity(new Vector3d(
        Probability.getRandom(5.0) - 2.5,
        Probability.getRandom(4),
        Probability.getRandom(5.0) - 2.5)
    );
  }
}
