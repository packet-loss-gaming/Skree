/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import com.skelril.nitro.Clause;
import com.skelril.skree.content.market.MarketImplUtil.QueryMode;
import com.skelril.skree.service.MarketService;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
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
import java.util.List;
import java.util.Optional;

import static org.spongepowered.api.util.command.args.GenericArguments.flags;
import static org.spongepowered.api.util.command.args.GenericArguments.remainingJoinedStrings;

public class MarketSellCommand implements CommandExecutor {

    private Game game;

    public MarketSellCommand(Game game) {
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

        Optional<ItemStack> filter = Optional.empty();

        QueryMode mode = QueryMode.HELD;

        if (args.hasAny("a")) {
            mode = QueryMode.EVERYTHING;
        } else if (args.hasAny("h")) {
            mode = QueryMode.HOT_BAR;
        }

        if (mode != QueryMode.HELD && !args.hasAny("u")) {
            filter = player.getItemInHand();
            if (!filter.isPresent()) {
                src.sendMessage(Texts.of(TextColors.DARK_RED, "You're not holding an item to filter with!"));
                return CommandResult.empty();
            }

            ItemStack newFilter = filter.get().copy();
            newFilter.setQuantity(1);

            Optional<MutableBoundedValue<Integer>> optDurability = newFilter.getValue(Keys.ITEM_DURABILITY);
            if (optDurability.isPresent()) {
                newFilter.offer(Keys.ITEM_DURABILITY, 0);
            }
            filter = Optional.of(newFilter);
        }

        Clause<BigDecimal, List<Integer>> changes = MarketImplUtil.getChanges(player, service, mode, filter);

        if (changes.getValue().isEmpty()) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "No sellable items found" + (filter.isPresent() ? " that matched the filter" : "") + "!"));
            return CommandResult.empty();
        }

        List<Clause<ItemStack, Integer>> transactions = MarketImplUtil.removeAtPos(player, changes.getValue());

        if (!service.logTransactionByStack(player.getUniqueId(), transactions)) {
            // TODO Auto reporting
            // Not critical, continue
            src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to log transactions, please report this!"));
        }

        BigDecimal newBalance = changes.getKey().add(MarketImplUtil.getMoney(player));
        if (!MarketImplUtil.setBalanceTo(game, player, newBalance)) {
            // TODO Auto reporting
            src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to adjust your balance, please report this!"));
            return CommandResult.empty();
        }

        player.sendMessage(Texts.of(TextColors.YELLOW, "Item(s) sold for: ", TextColors.WHITE, MarketImplUtil.format(changes.getKey()), TextColors.YELLOW, "!"));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Sell an item"))
                .arguments(flags().flag("h", "a", "u").buildWith(remainingJoinedStrings(Texts.of("item"))))
                .executor(new MarketSellCommand(game))
                .build();
    }
}