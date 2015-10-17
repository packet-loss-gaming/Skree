/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market.admin;

import com.skelril.skree.service.MarketService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.math.BigDecimal;
import java.util.Optional;

import static org.spongepowered.api.util.command.args.GenericArguments.*;

public class MarketQuickAddCommand implements CommandExecutor {

    private Game game;

    public MarketQuickAddCommand(Game game) {
        this.game = game;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Texts.of("You must be a player to use this command!"));
            return CommandResult.empty();
        }

        Optional<MarketService> optService = game.getServiceManager().provide(MarketService.class);
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
        BigDecimal price = new BigDecimal(args.<String>getOne("price").get());

        service.addItem(item);
        service.addAlias(alias, item);
        service.setPrice(alias, price);
        service.setPrimaryAlias(alias);

        src.sendMessage(Texts.of(TextColors.YELLOW, alias + " added to the market with a price of " + price.toPlainString()));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Add an item to the market"))
                .arguments(seq(string(Texts.of("price")), remainingJoinedStrings(Texts.of("alias"))))
                .executor(new MarketAddAliasCommand(game))
                .build();
    }
}
