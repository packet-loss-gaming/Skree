/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.shutdown;


import com.skelril.skree.service.ShutdownService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Texts;

import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class ShutdownCommand implements CommandExecutor {

    private ShutdownService service;

    public ShutdownCommand(ShutdownService service) {
        this.service = service;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Integer seconds = args.<Integer>getOne("seconds").get();
        Optional<String> message = args.<String>getOne("message");

        seconds = Math.min(Math.max(seconds, 10), 120);

        if (args.<Boolean>getOne("f").isPresent()) {
            if (message.isPresent()) {
                service.forceShutdown(Texts.of(message.get()));
            } else {
                service.forceShutdown();
            }
        } else {
            if (message.isPresent()) {
                service.shutdown(seconds, Texts.of(message.get()));
            } else {
                service.shutdown(seconds);
            }
        }

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(ShutdownService service) {
        return CommandSpec.builder()
                .description(Texts.of("Shut the server off"))
                .permission("skree.shutdown")
                .arguments(
                        flags().flag("f").buildWith(
                                seq(
                                        onlyOne(optionalWeak(integer(Texts.of("seconds")), 60)),
                                        optional(remainingJoinedStrings(Texts.of("message")))
                                )
                        )
                ).executor(new ShutdownCommand(service)).build();
    }
}
