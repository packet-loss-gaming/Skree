/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.internal.market.ItemDescriptor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.skelril.skree.content.market.MarketImplUtil.format;
import static org.spongepowered.api.command.args.GenericArguments.optional;
import static org.spongepowered.api.command.args.GenericArguments.remainingJoinedStrings;

public class MarketListCommand implements CommandExecutor {

  private Text createLine(ItemDescriptor entry, MarketService service) {
    String buy = format(entry.getCurrentValue());
    String sell = format(entry.getCurrentValue().multiply(service.getSellFactor(entry.getCurrentValue())));

    Text buyText = Text.of(TextColors.WHITE, buy);
    Text sellText = Text.of(TextColors.WHITE, sell);

    return Text.of(
        TextActions.runCommand("/market lookup " + entry.getName()),
        TextActions.showText(Text.of("Show detailed item information")),
        TextColors.BLUE, entry.getName().toUpperCase(),
        TextColors.GRAY, " x", format(new BigDecimal(entry.getStock())),
        TextColors.YELLOW, " (Quick Price: ", buyText, " - ", sellText, ")"
    );
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Optional<MarketService> optService = Sponge.getServiceManager().provide(MarketService.class);
    if (!optService.isPresent()) {
      src.sendMessage(Text.of(TextColors.DARK_RED, "The market service is not currently running."));
      return CommandResult.empty();
    }

    MarketService service = optService.get();
    PaginationService pagination = Sponge.getServiceManager().provideUnchecked(PaginationService.class);

    Optional<String> optFilter = args.getOne("name");
    String filter = optFilter.isPresent() ? optFilter.get() : "";

    if (!filter.matches(MarketService.VALID_ALIAS_REGEX)) {
      src.sendMessage(Text.of(TextColors.RED, "Invalid filter supplied."));
      return CommandResult.empty();
    }

    List<ItemDescriptor> prices = filter.isEmpty() ? service.getPrices()
        : service.getPrices(filter + "%");

    if (prices.isEmpty()) {
      src.sendMessage(Text.of(TextColors.YELLOW, "No items matched."));
      return CommandResult.success();
    }

    List<Text> result = prices.stream()
        .filter(a -> filter.isEmpty() || a.getName().startsWith(filter))
        .sorted(Comparator.comparing(ItemDescriptor::getCurrentValue))
        .map(a -> createLine(a, service))
        .collect(Collectors.toList());

    pagination.builder()
        .contents(result)
        .title(Text.of(TextColors.GOLD, "Item List"))
        .padding(Text.of(" "))
        .sendTo(src);

    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("Manipulate the market"))
        .arguments(optional(remainingJoinedStrings(Text.of("name"))))
        .executor(new MarketListCommand())
        .build();
  }
}
