/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.playerstate;

import com.skelril.skree.service.PlayerStateService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class GameModeCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<PlayerStateService> optService = Sponge.getServiceManager().provide(PlayerStateService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "The player state service is not currently running."));
            return CommandResult.empty();
        }
        PlayerStateService service = optService.get();

        GameMode mode = args.<GameMode>getOne("mode").get();
        Player target = args.<Player>getOne("target").get();

        GameModeData current = target.getGameModeData();
        service.save(target, current.type().get().getId());
        target.offer(current.set(Keys.GAME_MODE, mode));
        service.load(target, current.type().get().getId());

        target.sendMessage(Texts.of(TextColors.YELLOW, "Changed game mode to " + mode.getName() + '.'));
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        Map<String, GameMode> map = new HashMap<>();

        map.put("survival", GameModes.SURVIVAL);
        map.put("creative", GameModes.CREATIVE);
        map.put("adventure", GameModes.ADVENTURE);
        map.put("spectator", GameModes.SPECTATOR);

        return CommandSpec.builder()
                .description(Texts.of("Change gamemode"))
                .permission("skree.gamemode")
                .arguments(
                        seq(
                                onlyOne(choices(Texts.of("mode"), map)),
                                onlyOne(playerOrSource(Texts.of("target")))
                        )
                ).executor(new GameModeCommand()).build();
    }
}
