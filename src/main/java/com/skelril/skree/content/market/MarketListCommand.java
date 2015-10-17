/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import com.skelril.nitro.Clause;
import com.skelril.skree.service.MarketService;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.spongepowered.api.util.command.args.GenericArguments.optional;
import static org.spongepowered.api.util.command.args.GenericArguments.remainingJoinedStrings;

public class MarketListCommand implements CommandExecutor {

    private Game game;

    public MarketListCommand(Game game) {
        this.game = game;
    }

    private Text createLine(Clause<String, BigDecimal> entry, MarketService service) {
        DecimalFormat df = new DecimalFormat("#,###.##");
        String buy = df.format(entry.getValue());
        String sell = df.format(entry.getValue().multiply(service.getSellFactor(entry.getValue())));

        Text buyText = Texts.of(TextColors.WHITE, buy);
        Text sellText = Texts.of(TextColors.WHITE, sell);

        return Texts.of(
                TextColors.BLUE, entry.getKey().toUpperCase(),
                TextColors.YELLOW, " (Quick Price: ", buyText, " - ", sellText, ")"
        );
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<MarketService> optService = game.getServiceManager().provide(MarketService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "The market service is not currently running."));
            return CommandResult.empty();
        }

        MarketService service = optService.get();
        PaginationService pagination = game.getServiceManager().provideUnchecked(PaginationService.class);

        Optional<String> optFilter = args.<String>getOne("name");
        String filter = optFilter.isPresent() ? optFilter.get() : "";

        if (!filter.matches(MarketService.VALID_ALIAS_REGEX)) {
            src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid filter supplied."));
            return CommandResult.empty();
        }

        List<Clause<String, BigDecimal>> prices = filter.isEmpty() ? service.getPrices()
                                                                   : service.getPrices(filter + "%");

        if (prices.isEmpty()) {
            src.sendMessage(Texts.of(TextColors.YELLOW, "No items matched."));
            return CommandResult.success();
        }

        List<Text> result = prices.stream()
                .filter(a -> filter.isEmpty() || a.getKey().startsWith(filter))
                .sorted((a, b) -> a.getValue().compareTo(b.getValue()))
                .map(a -> createLine(a, service))
                .collect(Collectors.toList());

        pagination.builder()
                .contents(result)
                .title(Texts.of(TextColors.GOLD, "Item List"))
                .paddingString(" ")
                .sendTo(src);

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Manipulate the market"))
                .arguments(optional(remainingJoinedStrings(Texts.of("name"))))
                .executor(new MarketListCommand(game))
                .build();
    }
}
