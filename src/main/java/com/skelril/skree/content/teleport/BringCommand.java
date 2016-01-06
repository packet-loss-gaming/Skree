/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.teleport;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
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

        Optional<Boolean> optIsFlying = target.get(Keys.IS_FLYING);
        if (optIsFlying.isPresent() && !optIsFlying.get()) {
            while (dest.getFloorY() > 0 && targetExtent.getBlock(dest.toInt()).getType().equals(BlockTypes.AIR)) {
                dest = dest.add(0, -1, 0);
            }
        }
        target.setLocationAndRotation(new Location<>(targetExtent, dest.add(0, 1, 0)), rotation);

        src.sendMessage(Text.of(TextColors.YELLOW, "Player brought to you, my lord."));

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
