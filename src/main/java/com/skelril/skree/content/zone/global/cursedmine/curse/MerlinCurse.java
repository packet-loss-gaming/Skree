/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.curse;

import org.spongepowered.api.entity.living.player.Player;

import java.util.function.Consumer;

public class MerlinCurse implements Consumer<Player> {
  @Override
  public void accept(Player player) {
    new FireCurse().accept(player);
    new BlindnessCurse().accept(player);
    new SmokeCurse().accept(player);
    new MushroomCurse().accept(player);
    new ButterFingersCurse().accept(player);
  }
}
