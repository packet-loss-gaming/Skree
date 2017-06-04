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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
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
      src.sendMessage(Text.of(TextColors.DARK_RED, "The player state service is not currently running."));
      return CommandResult.empty();
    }
    PlayerStateService service = optService.get();

    GameMode mode = args.<GameMode>getOne("mode").get();
    Player target = args.<Player>getOne("target").get();

    if (service.hasInventoryStored(target) && !args.hasAny("f")) {
      src.sendMessage(Text.of(TextColors.RED, "Player has an omni-state stored, action denied."));
      src.sendMessage(Text.of(TextColors.RED, "This can be overwritten using -f."));
      return CommandResult.empty();
    }

    service.save(target, target.get(Keys.GAME_MODE).get().getId());
    target.offer(Keys.FALL_DISTANCE, 0F);
    target.offer(Keys.GAME_MODE, mode);
    service.load(target, mode.getId());

    target.sendMessage(Text.of(TextColors.YELLOW, "Changed game mode to " + mode.getName() + '.'));
    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    Map<String, GameMode> map = new HashMap<>();

    map.put("survival", GameModes.SURVIVAL);
    map.put("creative", GameModes.CREATIVE);
    map.put("adventure", GameModes.ADVENTURE);
    map.put("spectator", GameModes.SPECTATOR);

    return CommandSpec.builder()
        .description(Text.of("Change gamemode"))
        .permission("skree.gamemode")
        .arguments(
            flags().flag("f").buildWith(
                seq(
                    onlyOne(choices(Text.of("mode"), map)),
                    onlyOne(playerOrSource(Text.of("target")))
                )
            )
        ).executor(new GameModeCommand()).build();
  }
}
