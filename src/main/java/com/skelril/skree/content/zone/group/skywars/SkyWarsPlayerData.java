/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.skywars;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Set;

public class SkyWarsPlayerData {

  private Set<Player> team;

  private long nextFlight = 0;
  private long nextPushBack = 0;
  private long nextOmen = 0;
  private long nextDefrost = 0;

  public boolean canFly() {
    return nextFlight == 0 || System.currentTimeMillis() >= nextFlight;
  }

  public void stopFlight() {
    stopFlight(2250);
  }

  public void stopFlight(long time) {
    nextFlight = System.currentTimeMillis() + time;
  }

  public boolean canPushBack() {
    return nextPushBack == 0 || System.currentTimeMillis() >= nextPushBack;
  }

  public void stopPushBack() {
    stopPushBack(5000);
  }

  public void stopPushBack(long time) {
    nextPushBack = System.currentTimeMillis() + time;
  }

  public boolean canUseOmen() {
    return nextOmen == 0 || System.currentTimeMillis() >= nextOmen;
  }

  public void stopOmen() {
    stopOmen(1000);
  }

  public void stopOmen(long time) {
    nextOmen = System.currentTimeMillis() + time;
  }

  public boolean canDefrost() {
    return nextDefrost == 0 || System.currentTimeMillis() >= nextDefrost;
  }

  public void stopDefrost() {
    stopDefrost(1000);
  }

  public void stopDefrost(long time) {
    nextDefrost = System.currentTimeMillis() + time;
  }

  public Set<Player> getTeam() {
    return team;
  }

  public void setTeam(Set<Player> team) {
    this.team = team;
  }
}
