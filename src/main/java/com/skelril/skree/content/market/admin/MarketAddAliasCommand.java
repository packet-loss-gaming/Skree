/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market.admin;

import com.skelril.skree.service.MarketService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.remainingJoinedStrings;

public class MarketAddAliasCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be a player to use this command!"));
            return CommandResult.empty();
        }

        Optional<MarketService> optService = Sponge.getServiceManager().provide(MarketService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Text.of(TextColors.DARK_RED, "The market service is not currently running."));
            return CommandResult.empty();
        }

        MarketService service = optService.get();

        Optional<ItemStack> held = ((Player) src).getItemInHand();
        if (!held.isPresent()) {
            src.sendMessage(Text.of(TextColors.RED, "You are not holding an item."));
            return CommandResult.empty();
        }

        ItemStack item = held.get();
        String alias = args.<String>getOne("alias").get();

        if (service.addAlias(alias, item)) {
            src.sendMessage(Text.of(TextColors.YELLOW, alias + " added to the market."));
        } else {
            src.sendMessage(Text.of(TextColors.RED, "Your held item is not currently tracked, or the alias is already in use."));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Add an alias for an item"))
                .arguments(remainingJoinedStrings(Text.of("alias")))
                .executor(new MarketAddAliasCommand())
                .build();
    }
}
