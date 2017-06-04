/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.teleport;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.entity.SafeTeleportHelper;
import com.skelril.skree.content.world.WorldEntryPermissionCheck;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.player;

public class BringCommand implements CommandExecutor {

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Vector3d dest;
    Vector3d rotation;
    World targetExtent;
    if (src instanceof Player) {
      Player srcPlayer = (Player) src;
      Location<World> loc = srcPlayer.getLocation();
      dest = loc.getPosition();
      rotation = srcPlayer.getRotation();
      targetExtent = loc.getExtent();
    } else {
      src.sendMessage(Text.of(TextColors.RED, "You are not a player and teleporting other players is not currently supported!"));
      return CommandResult.empty();
    }

    Player target = args.<Player>getOne("target").get();

    Optional<Location<World>> optSafeDest = SafeTeleportHelper.getSafeDest(
        target,
        new Location<>(targetExtent, dest)
    );

    if (optSafeDest.isPresent()) {
      if (!WorldEntryPermissionCheck.checkDestination((Player) src, optSafeDest.get().getExtent())) {
        src.sendMessage(Text.of(TextColors.RED, "You do not have permission to teleport players to worlds of this type."));
        return CommandResult.empty();
      }
      target.setLocationAndRotation(optSafeDest.get(), rotation);

      src.sendMessage(Text.of(TextColors.YELLOW, "Player brought to you, my lord."));
    } else {
      src.sendMessage(Text.of(TextColors.RED, "The player could not be safely teleported here."));
    }

    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("Bring a player to your current location"))
        .permission("skree.teleport.bring")
        .arguments(
            onlyOne(
                player(Text.of("target"))
            )
        ).executor(new BringCommand()).build();
  }
}
