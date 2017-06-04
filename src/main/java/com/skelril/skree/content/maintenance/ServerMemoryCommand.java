/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.maintenance;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ServerMemoryCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    int mb = 1024 * 1024;

    Runtime runtime = Runtime.getRuntime();

    src.sendMessage(Text.of(TextColors.YELLOW, "Used memory: ", (runtime.totalMemory() - runtime.freeMemory()) / mb));
    src.sendMessage(Text.of(TextColors.YELLOW, "Free memory: ", runtime.freeMemory() / mb));
    src.sendMessage(Text.of(TextColors.YELLOW, "Total memory: ", runtime.totalMemory() / mb));
    src.sendMessage(Text.of(TextColors.YELLOW, "Max memory: ", runtime.maxMemory() / mb));

    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("View memory information"))
        .executor(new ServerMemoryCommand())
        .build();
  }
}
