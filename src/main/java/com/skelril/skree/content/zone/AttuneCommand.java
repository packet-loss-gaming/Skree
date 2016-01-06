/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.skelril.skree.service.ZoneService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.string;

public class AttuneCommand implements CommandExecutor {

    private ZoneService service;

    public AttuneCommand(ZoneService service) {
        this.service = service;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        service.requestZone(args.<String>getOne("zone").get(), (Player) src);
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(ZoneService service) {
        return CommandSpec.builder()
                .description(Text.of("Attune an orb to a Zone"))
                .permission("skree.zone.zoneme")
                .arguments(onlyOne(string(Text.of("zone")))).executor(new AttuneCommand(service)).build();
    }
}