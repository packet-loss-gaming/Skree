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
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

import static com.skelril.skree.content.market.MarketImplUtil.format;
import static org.spongepowered.api.command.args.GenericArguments.*;

public class MarketQuickAddCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Texts.of("You must be a player to use this command!"));
            return CommandResult.empty();
        }

        Optional<MarketService> optService = Sponge.getServiceManager().provide(MarketService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "The market service is not currently running."));
            return CommandResult.empty();
        }

        MarketService service = optService.get();

        Optional<ItemStack> held = ((Player) src).getItemInHand();
        if (!held.isPresent()) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "You are not holding an item."));
            return CommandResult.empty();
        }

        ItemStack item = held.get();
        String alias = args.<String>getOne("alias").get();
        BigDecimal price;
        try {
            price = new BigDecimal(args.<String>getOne("price").get());
        } catch (NumberFormatException ex) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid price specified"));
            return CommandResult.empty();
        }

        if (service.addItem(item)) {
            if (service.addAlias(alias, item)) {
                if (service.setPrice(alias, price)) {
                    if (service.setPrimaryAlias(alias)) {
                        src.sendMessage(Texts.of(TextColors.YELLOW, alias + " added to the market with a price of " + format(price)));
                        return CommandResult.success();
                    }
                    // Same error, fall through
                }
                src.sendMessage(Texts.of(TextColors.DARK_RED, alias + " is not a valid alias"));
                return CommandResult.empty();
            }
            src.sendMessage(Texts.of(TextColors.DARK_RED, "Your held item is not currently tracked, or the alias is already in use."));
            return CommandResult.empty();
        }
        src.sendMessage(Texts.of(TextColors.DARK_RED, "Your held item is already tracked."));
        return CommandResult.empty();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Texts.of("Add an item to the market"))
                .arguments(seq(string(Texts.of("price")), remainingJoinedStrings(Texts.of("alias"))))
                .executor(new MarketQuickAddCommand())
                .build();
    }
}
