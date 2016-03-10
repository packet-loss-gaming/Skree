/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import com.skelril.nitro.Clause;
import com.skelril.skree.content.market.MarketImplUtil.QueryMode;
import com.skelril.skree.service.MarketService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.skelril.skree.content.market.MarketImplUtil.canBuyOrSell;
import static org.spongepowered.api.command.args.GenericArguments.flags;
import static org.spongepowered.api.command.args.GenericArguments.none;

public class MarketSellCommand implements CommandExecutor {

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

        Player player = (Player) src;

        if (!canBuyOrSell(player)) {
            player.sendMessage(Text.of(TextColors.DARK_RED, "You cannot buy or sell from this area."));
            return CommandResult.empty();
        }

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
                src.sendMessage(Text.of(TextColors.DARK_RED, "You're not holding an item to filter with!"));
                return CommandResult.empty();
            }
        }

        Clause<BigDecimal, List<Integer>> changes = MarketImplUtil.getChanges(player, service, mode, filter.orElse(null));

        if (changes.getValue().isEmpty()) {
            src.sendMessage(Text.of(TextColors.DARK_RED, "No sellable items found" + (filter.isPresent() ? " that matched the filter" : "") + "!"));
            return CommandResult.empty();
        }

        List<Clause<ItemStack, Integer>> transactions = MarketImplUtil.removeAtPos(player, changes.getValue());

        if (!service.logTransactionByStack(player.getUniqueId(), transactions)) {
            // TODO Auto reporting
            // Not critical, continue
            src.sendMessage(Text.of(TextColors.DARK_RED, "Failed to log transactions, please report this!"));
        }

        BigDecimal newBalance = changes.getKey().add(MarketImplUtil.getMoney(player));
        if (!MarketImplUtil.setBalanceTo(player, newBalance, Cause.source(this).build())) {
            // TODO Auto reporting
            src.sendMessage(Text.of(TextColors.DARK_RED, "Failed to adjust your balance, please report this!"));
            return CommandResult.empty();
        }

        player.sendMessage(Text.of(TextColors.YELLOW, "Item(s) sold for: ", TextColors.WHITE, MarketImplUtil.format(changes.getKey()), TextColors.YELLOW, "!"));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Sell an item"))
                .arguments(flags().flag("h").flag("a").flag("u").buildWith(none()))
                .executor(new MarketSellCommand())
                .build();
    }
}