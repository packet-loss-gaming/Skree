/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs;

import com.skelril.skree.content.zone.ZoneBossDetail;
import org.spongepowered.api.entity.living.player.Player;

import java.lang.ref.WeakReference;
import java.util.Optional;

public class CatacombsBossDetail extends ZoneBossDetail<CatacombsInstance> {

  private final int wave;
  private WeakReference<Player> marked = new WeakReference<>(null);

  public CatacombsBossDetail(CatacombsInstance zone, int wave) {
    super(zone);
    this.wave = wave;
  }

  public int getWave() {
    return wave;
  }

  public Optional<Player> getMarked() {
    return Optional.ofNullable(marked.get());
  }

  public void setMarked(Player player) {
    marked = new WeakReference<>(player);
  }
}
