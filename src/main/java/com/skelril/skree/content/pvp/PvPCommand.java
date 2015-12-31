/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.pvp;

import com.skelril.skree.service.PvPService;
import com.skelril.skree.service.internal.pvp.PvPState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class PvPCommand implements CommandExecutor {

    private TextColor getColor(PvPState state) {
        return state.allowByDefault() ? TextColors.DARK_RED : TextColors.DARK_GREEN;
    }
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Texts.of("You must be a player to use this command!"));
            return CommandResult.empty();
        }

        Optional<PvPService> optService = Sponge.getServiceManager().provide(PvPService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "The PvP service is not currently running."));
            return CommandResult.empty();
        }
        PvPService service = optService.get();

        Player player = (Player) src;
        Optional<PvPState> stateArg = args.<PvPState>getOne("status");
        PvPState state = stateArg.orElse(service.getPvPState(player));
        service.setPvPState(player, state);

        player.sendMessage(Texts.of(
                TextColors.BLUE,
                "Your Opt-in PvP Settings", (stateArg.isPresent() ? " Changed!" : "")
        ));
        player.sendMessage(Texts.of(
                TextColors.YELLOW,
                "  Currently: ", getColor(state), state.toString()
        ));

        PvPState defaultState = service.getDefaultState(player);
        player.sendMessage(Texts.of(
                TextColors.YELLOW,
                "  Upon disconnect: ", getColor(defaultState), defaultState.toString()
        ));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        Map<String, PvPState> map = new HashMap<>();

        map.put("allow", PvPState.ALLOWED);
        map.put("deny", PvPState.DENIED);

        return CommandSpec.builder()
                .description(Texts.of("Change your opt-in PvP status"))
                .permission("skree.pvp")
                .arguments(optional(onlyOne(choices(Texts.of("status"), map))))
                .executor(new PvPCommand()).build();
    }
}