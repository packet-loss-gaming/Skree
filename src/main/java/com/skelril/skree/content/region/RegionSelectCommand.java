/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.region;

import com.skelril.skree.service.RegionService;
import com.skelril.skree.service.internal.region.Region;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class RegionSelectCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

    if (!(src instanceof Player)) {
      src.sendMessage(Text.of("You must be a player to use this command (for now ;) )!"));
      return CommandResult.empty();
    }

    Optional<RegionService> optService = Sponge.getServiceManager().provide(RegionService.class);
    if (!optService.isPresent()) {
      src.sendMessage(Text.of(TextColors.DARK_RED, "The region service is not currently running."));
      return CommandResult.empty();
    }

    RegionService service = optService.get();

    Player player = (Player) src;

    Optional<Player> optPlayer = args.getOne("player");
    Optional<Location<World>> optLocation = args.getOne("location");
    Optional<Region> optRegion;

    if (optPlayer.isPresent()) {
      Player targPlayer = optPlayer.get();
      optRegion = service.get(targPlayer.getLocation());
      player.sendMessage(Text.of(TextColors.YELLOW, "Searching for ", targPlayer.getName(), "'s local region..."));
    } else if (optLocation.isPresent()) {
      optRegion = service.get(optLocation.get());
      player.sendMessage(Text.of(TextColors.YELLOW, "Searching for a region at the given location..."));
    } else {
      optRegion = service.get(player.getLocation());
      player.sendMessage(Text.of(TextColors.YELLOW, "Searching for your local region..."));
    }

    service.setSelectedRegion(player, optRegion.orElse(null));

    if (optRegion.isPresent()) {
      player.sendMessage(Text.of(
          TextColors.YELLOW, "Region found! View information with ",
          Text.of(TextColors.GREEN, TextActions.runCommand("/region info"), "/region info")
      ));
    } else {
      player.sendMessage(Text.of(
          TextColors.YELLOW, "No region found, your region selection has been cleared"
      ));
    }

    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("Select a region"))
        .arguments(optional(firstParsing(player(Text.of("player"))), location(Text.of("position"))))
        .executor(new RegionSelectCommand())
        .build();
  }
}
