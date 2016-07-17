/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;


import com.skelril.skree.service.MarketService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static org.spongepowered.api.command.args.GenericArguments.optional;
import static org.spongepowered.api.command.args.GenericArguments.remainingJoinedStrings;

public class MarketLookupCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<MarketService> optService = Sponge.getServiceManager().provide(MarketService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Text.of(TextColors.DARK_RED, "The market service is not currently running."));
            return CommandResult.empty();
        }

        MarketService service = optService.get();

        Optional<String> optAlias = args.getOne("alias");
        Optional<BigDecimal> optPrice = Optional.empty();
        Optional<Integer> optStock = Optional.empty();
        double percentageSale = 1;

        if (optAlias.isPresent()) {
            optPrice = service.getPrice(optAlias.get());
            optAlias = service.getAlias(optAlias.get());
            optStock = service.getStock(optAlias.get());
        } else {
            Optional<ItemStack> held = src instanceof Player ? ((Player) src).getItemInHand(HandTypes.MAIN_HAND) : Optional.empty();
            if (held.isPresent()) {
                optPrice = service.getPrice(held.get());
                optAlias = service.getAlias(held.get());
                optStock = service.getStock(held.get());
                net.minecraft.item.ItemStack stack = tf(held.get());
                if (stack.isItemStackDamageable()) {
                    percentageSale = 1 - ((double) stack.getItemDamage() / (double) stack.getMaxDamage());
                }
            }
        }

        if (!optPrice.isPresent()) {
            src.sendMessage(Text.of(TextColors.RED, "No valid alias specified, and you're not holding a tracked item."));
            return CommandResult.empty();
        }

        BigDecimal price = optPrice.get();
        Integer stockCount = optStock.orElse(0);
        BigDecimal sellPrice = price.multiply(service.getSellFactor(price));

        DecimalFormat df = new DecimalFormat("#,###.##");

        String buyPrice = df.format(price);
        String stock = df.format(stockCount);
        String sellUsedPrice = df.format(sellPrice.multiply(new BigDecimal(percentageSale)));
        String sellNewPrice = df.format(sellPrice);

        String alias = optAlias.get();

        Text itemDisplay = Text.of(
                TextColors.BLUE,
                TextActions.showItem(service.getItem(alias).get()), alias.toUpperCase()
        );

        Text.Builder itemSteps = Text.builder();
        for (int i : new int[]{1, 16, 32, 64, 128, 256}) {
            if (i != 1) {
                itemSteps.append(Text.of(TextColors.YELLOW, ", "));
            }

            BigDecimal intervalPrice = price.multiply(new BigDecimal(i));

            itemSteps.append(Text.of(
                    TextColors.BLUE,
                    TextActions.runCommand("/market buy -a " + i + " " + alias),
                    TextActions.showText(Text.of("Buy ", i, " for ", df.format(intervalPrice))),
                    i
            ));
        }

        List<Text> information = new ArrayList<>(6);
        Collections.addAll(
                information,
                Text.of(TextColors.GOLD, "Price information for: ", itemDisplay),
                Text.of(TextColors.YELLOW, "There are currently ", TextColors.GRAY, stock, TextColors.YELLOW, " in stock."),
                Text.of(TextColors.YELLOW, "When you buy it you pay:"),
                Text.of(TextColors.YELLOW, " - ", TextColors.WHITE, buyPrice, TextColors.YELLOW, " each."),
                Text.of(TextColors.YELLOW, "When you sell it you get:"),
                Text.of(TextColors.YELLOW, " - ", TextColors.WHITE, sellUsedPrice, TextColors.YELLOW, " each."),
                Text.of(TextColors.YELLOW, "Quick buy: ", itemSteps.build())
        );

        if (percentageSale != 1) {
            information.add(
                    Text.of(TextColors.YELLOW, " - ", TextColors.WHITE, sellNewPrice, TextColors.YELLOW, " each when new.")
            );
        }

        src.sendMessages(information);
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Lookup the price information for an item"))
                .arguments(optional(remainingJoinedStrings(Text.of("alias"))))
                .executor(new MarketLookupCommand())
                .build();
    }
}
