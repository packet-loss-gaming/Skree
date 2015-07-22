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
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import java.util.HashMap;
import java.util.Map;

import static org.spongepowered.api.util.command.args.GenericArguments.*;

public class WeatherCommand implements CommandExecutor {
    private final Game game;

    public WeatherCommand(Game game) {
        this.game = game;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        TextBuilder builder = Texts.builder();

        // World resolution
        Optional<WorldProperties> optWorldProps = args.getOne("world");
        Optional<World> optWorld;

        if (!optWorldProps.isPresent()) {
            if (!(src instanceof Player)) {
                builder.append(Texts.of("You are not a player and need to specify a world!")).color(TextColors.RED);
                src.sendMessage(Texts.of(builder.build()));
                return CommandResult.empty();
            }
            optWorld = Optional.of(((Player) src).getWorld());
        } else {
            optWorld = game.getServer().getWorld(optWorldProps.get().getUniqueId());
        }

        // Handled by command spec, so always provided
        Weather weather = args.<Weather>getOne("type").get();

        Optional<Integer> duration = args.getOne("duration");
        if (!duration.isPresent()) {
            duration = Optional.of(Probability.getRangedRandom(5 * 60, 15 * 60)); // Between 5 and 15 minutes
        }

        if (duration.get() < 1) {
            builder.append(Texts.of("Weather duration must be at least 1 second!")).color(TextColors.RED);
            src.sendMessage(builder.build());
            return CommandResult.empty();
        }

        World world = optWorld.get();
        if (!world.isLoaded()) {
            builder.append(Texts.of("The specified world was not loaded!")).color(TextColors.RED);
            src.sendMessage(Texts.of(builder.build()));
            return CommandResult.empty();
        }

        world.forecast(weather, duration.get() * 20);

        builder.append(Texts.of("Changed weather state in " + world.getName() + " to: " + weather.getName() + '.'));
        builder.color(TextColors.YELLOW);
        src.sendMessage(builder.build());

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        Map<String, Weather> map = new HashMap<>();

        map.put("clear", Weathers.CLEAR);
        map.put("rain", Weathers.RAIN);
        map.put("thunder", Weathers.THUNDER_STORM);

        return CommandSpec.builder()
                .description(Texts.of("Change the weather"))
                .permission("skree.weathercommand")
                .arguments(
                        seq(
                                onlyOne(choices(Texts.of("type"), map)),
                                // TODO should be onlyOne(catalogedElement(Texts.of("type"), game, Weather.class)),
                                onlyOne(optionalWeak(integer(Texts.of("duration")))),
                                onlyOne(optional(world(Texts.of("world"), game)))
                        )
                )
                .executor(new WeatherCommand(game)).build();
    }
}
