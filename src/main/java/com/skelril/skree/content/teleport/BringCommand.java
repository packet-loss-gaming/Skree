/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.teleport;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.entity.GameModeData;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.gamemode.GameModes;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import static org.spongepowered.api.util.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.util.command.args.GenericArguments.player;

public class BringCommand implements CommandExecutor {
    private final Game game;

    public BringCommand(Game game) {
        this.game = game;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        TextBuilder builder = Texts.builder();

        Vector3d dest;
        Vector3d rotation;
        Extent targetExtent;
        if (src instanceof Player) {
            Player srcPlayer = (Player) src;
            Location loc = srcPlayer.getLocation();
            dest = loc.getPosition();
            rotation = srcPlayer.getRotation();
            targetExtent = loc.getExtent();
        } else {
            builder.append(Texts.of("You are not a player and teleporting other players is not currently supported!"));
            builder.color(TextColors.RED);
            src.sendMessage(Texts.of(builder.build()));
            return CommandResult.empty();
        }


        Player target = args.<Player>getOne("target").get();

        Optional<GameModeData> data = target.getData(GameModeData.class);
        if (!data.isPresent() || !(data.get().getGameMode() == GameModes.CREATIVE || data.get().getGameMode() == GameModes.SPECTATOR)) {
            while (dest.getFloorY() > 0 && targetExtent.getBlock(dest.toInt()).getType().equals(BlockTypes.AIR)) {
                dest = dest.add(0, -1, 0);
            }
        }
        target.setLocationAndRotation(new Location(targetExtent, dest.add(0, 1, 0)), rotation);

        builder.append(Texts.of("Player brought to you, my lord."));
        builder.color(TextColors.YELLOW);
        src.sendMessage(builder.build());

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Bring a player to your current location"))
                .permission("skree.teleport.bring")
                .arguments(
                        onlyOne(
                                player(Texts.of("target"), game)
                        )
                ).executor(new BringCommand(game)).build();
    }
}
