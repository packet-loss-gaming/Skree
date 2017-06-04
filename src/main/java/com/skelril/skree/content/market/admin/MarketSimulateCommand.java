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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class MarketSimulateCommand implements CommandExecutor {

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

    Optional<MarketService> optService = Sponge.getServiceManager().provide(MarketService.class);
    if (!optService.isPresent()) {
      src.sendMessage(Text.of(TextColors.DARK_RED, "The market service is not currently running."));
      return CommandResult.empty();
    }

    MarketService service = optService.get();

    src.sendMessage(Text.of(TextColors.YELLOW, "Starting market update..."));
    service.updatePrices();
    src.sendMessage(Text.of(TextColors.YELLOW, "Update completed!"));

    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("Simulate market price and stock changes"))
        .executor(new MarketSimulateCommand())
        .build();
  }
}

