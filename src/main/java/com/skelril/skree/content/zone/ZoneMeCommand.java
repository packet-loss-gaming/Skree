/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.skelril.skree.service.ZoneService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.string;

public class ZoneMeCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ZoneService service = Sponge.getServiceManager().provide(ZoneService.class).get();
        service.requestZone(args.<String>getOne("zone").get(), (Player) src);
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Texts.of("Create a zone"))
                .permission("skree.zone.zoneme")
                .arguments(onlyOne(string(Texts.of("zone")))).executor(new ZoneMeCommand()).build();
    }
}
