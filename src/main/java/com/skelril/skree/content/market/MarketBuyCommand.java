/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.skree.service.MarketService;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.content.market.MarketImplUtil.format;
import static org.spongepowered.api.command.args.GenericArguments.*;

public class MarketBuyCommand implements CommandExecutor {

    private Game game;

    public MarketBuyCommand(Game game) {
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

        Player player = (Player) src;

        String itemName = args.<String>getOne("item").get();
        List<String> targetItems;
        if (itemName.endsWith("#armor")) {
            String armorType = itemName.replace("#armor", " ");
            targetItems = Lists.newArrayList(
                    armorType + "helmet",
                    armorType + "chestplate",
                    armorType + "leggings",
                    armorType + "boots"
            );
        } else {
            targetItems = Lists.newArrayList(itemName);
        }

        BigDecimal price = BigDecimal.ZERO;

        for (String anItem : targetItems) {
            Optional<BigDecimal> optPrice = service.getPrice(anItem);
            if (!optPrice.isPresent()) {
                src.sendMessage(Texts.of(TextColors.DARK_RED, "That item is not available for purchase."));
                return CommandResult.empty();
            }

            price = price.add(optPrice.get());
        }
        Optional<Integer> optAmt = args.<Integer>getOne("amount");
        int amt = Math.max(1, optAmt.isPresent() ? optAmt.get() : 0);
        price = price.multiply(BigDecimal.valueOf(amt));

        BigDecimal funds = MarketImplUtil.getMoney(player);
        BigDecimal newBalance = funds.subtract(price);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have enough money to purchase that item(s)."));
            return CommandResult.empty();
        }

        // Accumulate items
        List<ItemStack> itemStacks = new ArrayList<>(targetItems.size());
        for (String anItem : targetItems) {
            Optional<ItemStack> stack = service.getItem(anItem);
            if (!stack.isPresent()) {
                // TODO Auto reporting
                src.sendMessage(Texts.of(TextColors.DARK_RED, "An item stack could not be resolved, please report this!"));
                return CommandResult.empty();
            }
            int total = amt;
            while (total > 0) {
                int increment = Math.min(total, stack.get().getMaxStackQuantity());
                total -= increment;
                itemStacks.add(newItemStack(stack.get(), increment));
            }
        }

        // Alright, all items have been found
        if (!MarketImplUtil.setBalanceTo(game, player, newBalance, Cause.of(this))) {
            // TODO Auto reporting
            src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to adjust your balance, please report this!"));
            return CommandResult.empty();
        }

        Clause<Boolean, List<Clause<ItemStack, Integer>>> transactions = MarketImplUtil.giveItems(game, player, itemStacks, Cause.of(this));

        if (!transactions.getKey()) {
            // TODO Auto reporting
            src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to give all items, please report this!"));
            return CommandResult.empty();
        }

        if (!service.logTransactionByStack(player.getUniqueId(), transactions.getValue())) {
            // TODO Auto reporting
            // Not critical, continue
            src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to log transactions, please report this!"));
        }

        player.sendMessage(Texts.of(TextColors.YELLOW, "Item(s) purchased for ", TextColors.WHITE, format(price), TextColors.YELLOW, "!"));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Purchase an item"))
                .arguments(flags().valueFlag(integer(Texts.of("amount")), "a").buildWith(remainingJoinedStrings(Texts.of("item"))))
                .executor(new MarketBuyCommand(game))
                .build();
    }
}
