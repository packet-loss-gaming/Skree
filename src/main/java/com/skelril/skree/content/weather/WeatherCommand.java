/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.weather;

import com.google.common.base.Optional;
import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import static org.spongepowered.api.util.command.args.GenericArguments.*;

public class WeatherCommand implements CommandExecutor {
    private final Game game;

    public WeatherCommand(Game game) {
        this.game = game;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        TextBuilder builder = Texts.builder();
        Optional<World> world;

        if (!(args.getOne("World Name").isPresent())) {
            if (!(src instanceof Player)) {
                builder.append(Texts.of("You need to specify a world!")).color(TextColors.RED);
                src.sendMessage(builder.build());

                return CommandResult.empty();

            } else {
                world = Optional.of(((Player) src).getWorld());
            }
        } else {
            world = game.getServer().getWorld(args.<String>getOne("World Name").get());
        }

        if (!world.isPresent()) {
            builder.append(Texts.of("Failed to find a world named: " + args.<String>getOne("World Name").get())).color(TextColors.RED);
            src.sendMessage(Texts.of(builder.build()));
            return CommandResult.empty();
        }

        String weatherType = args.getOne("Weather Type").get().toString();
        Weather weather;

        switch (weatherType.toLowerCase()) {
            case "sun":
            case "sunny":
            case "clear":
                weather = Weathers.CLEAR;
                break;

            case "rain":
            case "rainy":
                weather = Weathers.RAIN;
                break;

            case "storm":
            case "stormy":
            case "thunder":
                weather = Weathers.THUNDER_STORM;
                break;

            default:
                builder.append(Texts.of("Not a valid type of weather!")).color(TextColors.RED);
                src.sendMessage(builder.build());

                return CommandResult.empty();
        }

        Optional<Integer> duration = args.<Integer>getOne("duration");

        if (!(duration.isPresent())) {
            duration = Optional.of(Probability.getRangedRandom(6000, 18000));
        }

        if (duration.get() < 1 || duration.get() > 1000000) {
            builder.append(Texts.of("\"" + duration.get().toString() + "\" not a valid number!")).color(TextColors.RED);
            src.sendMessage(builder.build());
            return CommandResult.empty();
        }

        world.get().forecast(weather, duration.get());

        src.sendMessage(Texts.of("Changed weather to " + weatherType + " in " + world.get().getName()));
        return CommandResult.success();

    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Change the weather"))
                .permission("skree.weathercommand")
                .arguments(
                        seq(
                                onlyOne(string(Texts.of("Weather Type"))),
                                onlyOne(optionalWeak(integer(Texts.of("duration")))),
                                onlyOne(optionalWeak(string(Texts.of("World Name"))))
                        )
                )
                .executor(new WeatherCommand(game)).build();
    }
}
