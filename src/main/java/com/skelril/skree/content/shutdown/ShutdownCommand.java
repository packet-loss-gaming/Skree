/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.shutdown;


import com.skelril.skree.service.ShutdownService;
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

import static org.spongepowered.api.command.args.GenericArguments.*;

public class ShutdownCommand implements CommandExecutor {

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Optional<ShutdownService> optService = Sponge.getServiceManager().provide(ShutdownService.class);
    if (!optService.isPresent()) {
      src.sendMessage(Text.of(TextColors.DARK_RED, "The shutdown service is not currently running."));
      return CommandResult.empty();
    }

    ShutdownService service = optService.get();

    Integer seconds = args.<Integer>getOne("seconds").get();
    Optional<String> message = args.getOne("message");

    seconds = Math.min(Math.max(seconds, 10), 120);

    if (args.<Boolean>getOne("f").isPresent()) {
      if (message.isPresent()) {
        service.forceShutdown(Text.of(message.get()));
      } else {
        service.forceShutdown();
      }
    } else {
      if (message.isPresent()) {
        service.shutdown(seconds, Text.of(message.get()));
      } else {
        service.shutdown(seconds);
      }
    }

    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("Shut the server off"))
        .permission("skree.shutdown")
        .arguments(
            flags().flag("f").buildWith(
                seq(
                    onlyOne(optionalWeak(integer(Text.of("seconds")), 60)),
                    optional(remainingJoinedStrings(Text.of("message")))
                )
            )
        ).executor(new ShutdownCommand()).build();
  }
}
