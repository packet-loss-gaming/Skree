/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world;


import com.skelril.nitro.entity.SafeTeleportHelper;
import com.skelril.skree.service.WorldService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class WorldCommand implements CommandExecutor {

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

    if (!(src instanceof Player)) {
      src.sendMessage(Text.of("You must be a player to use this command!"));
      return CommandResult.empty();
    }

    WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);

    Optional<WorldProperties> optProperties = args.getOne("world");

    if (!optProperties.isPresent()) {
      src.sendMessage(Text.of(TextColors.YELLOW, "You are in: " + ((Player) src).getWorld().getName() + "."));
      return CommandResult.empty();
    }

    Optional<World> optWorld = Sponge.getServer().getWorld(optProperties.get().getWorldName());
    if (!optWorld.isPresent()) {
      src.sendMessage(Text.of(TextColors.RED, "No loaded world by that name found."));
      return CommandResult.empty();
    }

    World world = optWorld.get();
    if (!WorldEntryPermissionCheck.checkDestination((Player) src, world)) {
      src.sendMessage(Text.of(TextColors.RED, "You do not have permission to access worlds of this type."));
      return CommandResult.empty();
    }

    Optional<Location<World>> optLoc = SafeTeleportHelper.teleport((Entity) src, optWorld.get().getSpawnLocation());
    if (optLoc.isPresent()) {
      src.sendMessage(Text.of(TextColors.YELLOW, "Entered world: " + world.getName() + " successfully!"));
    } else if (args.hasAny("f")) {
      ((Player) src).setLocation(optWorld.get().getSpawnLocation());
      src.sendMessage(Text.of(TextColors.YELLOW, "Force entered world: " + world.getName() + " successfully!"));
    } else {
      src.sendMessage(Text.of(TextColors.YELLOW, "Failed to enter " + world.getName() + " please report this!"));
    }

    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("Teleport to a different world"))
        .arguments(flags().flag("f").buildWith(optional(onlyOne(world(Text.of("world"))))))
        .executor(new WorldCommand()).build();
  }
}