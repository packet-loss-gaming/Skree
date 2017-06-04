/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.aid;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.system.aid.AntiPeskConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class AntiPeskListener {
  private AntiPeskConfig config;

  public AntiPeskListener(AntiPeskConfig config) {
    this.config = config;
  }

  @Listener
  public void onCommandSend(SendCommandEvent event) {
    String command = event.getCommand();
    String arguments = event.getArguments();
    if (arguments.length() > 0) {
      command += " " + arguments;
    }

    for (String pattern : config.getTriggeringCommandPatterns()) {
      if (Pattern.matches(pattern, command.toLowerCase())) {
        event.getCause().first(Player.class).ifPresent(this::punish);
        event.setCancelled(true);
      }
    }
  }

  private void punish(Player player) {
    player.sendMessage(Text.of(TextColors.RED, "Command forbidden..."));
    player.sendMessage(Text.of(TextColors.RED, "... prepare to hate your life ..."));


    IntegratedRunnable integratedRunnable = new IntegratedRunnable() {
      @Override
      public boolean run(int times) {
        player.setVelocity(new Vector3d(
            Probability.getRangedRandom(-3.0, 3.0),
            Probability.getRangedRandom(-3.0, 3.0),
            Probability.getRangedRandom(-3.0, 3.0)
        ));
        return true;
      }

      @Override
      public void end() {
        player.kick(Text.of("How about you don't do that again, K?"));
      }
    };
    TimedRunnable<IntegratedRunnable> runnable = new TimedRunnable<>(integratedRunnable, 12);

    Task task = Task.builder().execute(
        runnable
    ).interval(
        1, TimeUnit.SECONDS
    ).delay(
        5, TimeUnit.SECONDS
    ).submit(SkreePlugin.inst());

    runnable.setTask(task);
  }
}
