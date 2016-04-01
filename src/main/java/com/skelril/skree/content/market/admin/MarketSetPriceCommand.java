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

import java.math.BigDecimal;
import java.util.Optional;

import static com.skelril.skree.content.market.MarketImplUtil.format;
import static org.spongepowered.api.command.args.GenericArguments.*;

public class MarketSetPriceCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<MarketService> optService = Sponge.getServiceManager().provide(MarketService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Text.of(TextColors.DARK_RED, "The market service is not currently running."));
            return CommandResult.empty();
        }

        MarketService service = optService.get();

        BigDecimal price;
        try {
            price = new BigDecimal(args.<String>getOne("price").get());
        } catch (NumberFormatException ex) {
            src.sendMessage(Text.of(TextColors.RED, "Invalid price specified"));
            return CommandResult.empty();
        }

        Optional<String> optAlias = args.getOne("alias");
        Optional<ItemStack> held = src instanceof Player ? ((Player) src).getItemInHand() : Optional.empty();
        if (optAlias.isPresent()) {
            String alias = optAlias.get();
            if (service.setPrice(alias, price)) {
                src.sendMessage(Text.of(TextColors.YELLOW, alias + "'s price has been set to " + format(price)));
                return CommandResult.success();
            }
        } else if (held.isPresent()) {
            if (service.setPrice(held.get(), price)) {
                src.sendMessage(Text.of(TextColors.YELLOW, "Your held item's price has been set to " + format(price)));
                return CommandResult.success();
            }
        }

        src.sendMessage(Text.of(TextColors.RED, "No valid alias specified, and you're not holding a tracked item."));
        return CommandResult.empty();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Set the price of an item"))
                .arguments(seq(onlyOne(string(Text.of("price"))), optional(remainingJoinedStrings(Text.of("alias")))))
                .executor(new MarketSetPriceCommand())
                .build();
    }
}
