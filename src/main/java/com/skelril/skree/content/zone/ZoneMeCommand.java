/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.skelril.skree.service.ZoneService;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;
import java.util.stream.Collectors;

import static org.spongepowered.api.command.args.GenericArguments.choices;
import static org.spongepowered.api.command.args.GenericArguments.onlyOne;

public class ZoneMeCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ZoneService service = Sponge.getServiceManager().provide(ZoneService.class).get();
        service.requestZone(args.<String>getOne("zone").get(), (Player) src, () -> {
            src.sendMessage(Text.of(TextColors.YELLOW, "Job completed."));
        }, (clause) -> {
            if (clause.isPresent()) {
                ZoneStatus status = clause.get().getValue();
                src.sendMessage(Text.of(status == ZoneStatus.ADDED ? TextColors.GREEN : TextColors.RED, "Added with status: ", status));
            }
        });
        src.sendMessage(Text.of(TextColors.YELLOW, "Creating requested zone."));
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        ZoneService service = Sponge.getServiceManager().provide(ZoneService.class).get();

        Map<String, String> options = service.getManagerNames().stream().collect(Collectors.toMap(a -> a, a -> a));

        return CommandSpec.builder()
                .description(Text.of("Create a zone"))
                .permission("skree.zone.zoneme")
                .arguments(onlyOne(choices(Text.of("zone"), options))).executor(new ZoneMeCommand()).build();
    }
}
