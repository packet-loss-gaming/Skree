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

public class MarketSetPriceCommand implements CommandExecutor {

    private Game game;

    public MarketSetPriceCommand(Game game) {
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

        String alias = null;

        Optional<String> optAlias = args.<String>getOne("alias");
        Optional<ItemStack> held = src instanceof Player ? ((Player) src).getItemInHand() : Optional.empty();
        if (optAlias.isPresent()) {
            alias = optAlias.get();
        } else if (held.isPresent()) {
            alias = service.getAlias(held.get());
        }

        if (alias == null) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "No alias specified, and you're not holding an item."));
            return CommandResult.empty();
        }

        BigDecimal price = new BigDecimal(args.<String>getOne("price").get());

        service.setPrice(alias, price);

        src.sendMessage(Texts.of(TextColors.YELLOW, alias + "'s price has been set to " + price.toPlainString()));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Set the price of an item"))
                .arguments(seq(onlyOne(string(Texts.of("price"))), optional(remainingJoinedStrings(Texts.of("alias")))))
                .executor(new MarketSetPriceCommand(game))
                .build();
    }
}
