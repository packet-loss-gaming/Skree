/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class SummonWandererCommand implements CommandExecutor {

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

    if (!(src instanceof Player)) {
      src.sendMessage(Text.of("You must be a player to use this command!"));
      return CommandResult.empty();
    }

    WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);

    World world = ((Player) src).getWorld();
    Optional<WorldEffectWrapper> optWrapper = service.getEffectWrapperFor(world);
    if (!optWrapper.isPresent()) {
      src.sendMessage(Text.of("This command can only be used in the Wilderness."));
      return CommandResult.empty();
    }

    WorldEffectWrapper wrapper = optWrapper.get();
    if (!(wrapper instanceof WildernessWorldWrapper)) {
      src.sendMessage(Text.of("This command can only be used in the Wilderness."));
      return CommandResult.empty();
    }

    Location<World> targetLocation = ((Player) src).getLocation();
    int playerWildernessLevel = ((WildernessWorldWrapper) wrapper).getLevel(targetLocation).get();
    int targetLevel = args.<Integer>getOne("target level").orElse(playerWildernessLevel);

    if (targetLevel < 1) {
      src.sendMessage(Text.of("The target level must be at least 1."));
      return CommandResult.empty();
    }

    String wanderer = args.<String>getOne("wanderer").get();
    ((WildernessWorldWrapper) wrapper).getWanderingMobManager().summon(wanderer, targetLevel, targetLocation);

    return CommandResult.success();
  }

  private static Collection<String> supportedWanderers() {
    WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);
    WildernessWorldWrapper wrapper = service.getEffectWrapper(WildernessWorldWrapper.class).get();
    return wrapper.getWanderingMobManager().getSupportedWanderers();
  }

  public static CommandSpec aquireSpec() {
    SummonWandererCommand command = new SummonWandererCommand();

    return CommandSpec.builder()
        .description(Text.of("Summons a wandering boss"))
        .permission("skree.world.wilderness.wanderer")
        .arguments(choices(
            Text.of("wanderer"), SummonWandererCommand::supportedWanderers, Function.identity()),
            optional(integer(Text.of("target level"))))
        .executor(command).build();
  }
}
