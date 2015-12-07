/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market.admin;

import com.skelril.skree.service.MarketService;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.remainingJoinedStrings;

public class MarketSetPrimaryAliasCommand implements CommandExecutor {

    private Game game;

    public MarketSetPrimaryAliasCommand(Game game) {
        this.game = game;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<MarketService> optService = game.getServiceManager().provide(MarketService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "The market service is not currently running."));
            return CommandResult.empty();
        }

        MarketService service = optService.get();

        String alias = args.<String>getOne("alias").get();
        if (service.setPrimaryAlias(alias)) {
            src.sendMessage(Texts.of(TextColors.YELLOW, alias + " set as a primary alias."));
        } else {
            src.sendMessage(Texts.of(TextColors.DARK_RED, alias + " is not a valid alias."));
        }

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Set the primary alias (name) of an item"))
                .arguments(onlyOne(remainingJoinedStrings(Texts.of("alias"))))
                .executor(new MarketSetPrimaryAliasCommand(game))
                .build();
    }
}
