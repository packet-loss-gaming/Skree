/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.random;

import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import static com.skelril.nitro.probability.Probability.getRangedRandom;
import static org.spongepowered.api.command.args.GenericArguments.player;

public class HeartsCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player targetPlayer = args.<Player>getOne(Text.of("player")).get();

        IntegratedRunnable runnable = new IntegratedRunnable() {
            @Override
            public boolean run(int times) {
                for (int i = 0; i < 75; ++i) {
                    ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.HEART).quantity(1).build();

                    targetPlayer.getWorld().spawnParticles(effect, targetPlayer.getLocation().getPosition().add(
                            getRangedRandom(-5.0, 5.0),
                            getRangedRandom(-2, 5.0),
                            getRangedRandom(-5.0, 5.0)
                    ));
                }
                return true;
            }

            @Override
            public void end() { }
        };

        TimedRunnable timedRunnable = new TimedRunnable<>(runnable, 20);

        timedRunnable.setTask(Task.builder().execute(
                timedRunnable
        ).intervalTicks(5).submit(SkreePlugin.inst()));


        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Spawn hearts around a target player"))
                .permission("skree.hearts")
                .arguments(player(Text.of("player")))
                .executor(new HeartsCommand()).build();
    }
}