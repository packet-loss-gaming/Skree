/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.region;

import com.skelril.skree.service.RegionService;
import com.skelril.skree.service.internal.region.RegionReference;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Identifiable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.spongepowered.api.command.args.GenericArguments.player;

public class RegionAddMemberCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be a player to use this command (for now ;) )!"));
            return CommandResult.empty();
        }

        Optional<RegionService> optService = Sponge.getServiceManager().provide(RegionService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Text.of(TextColors.DARK_RED, "The region service is not currently running."));
            return CommandResult.empty();
        }

        RegionService service = optService.get();

        Player player = (Player) src;

        Optional<RegionReference> optRef = service.getSelectedRegion(player);
        if (!optRef.isPresent()) {
            player.sendMessage(Text.of(TextColors.RED, "You do not currently have a region selected."));
            return CommandResult.empty();
        }

        List<UUID> newMembers = args.<Player>getAll("player").stream().map(
                Identifiable::getUniqueId
        ).collect(Collectors.toList());

        RegionReference ref = optRef.get();
        ref.addMember(newMembers);

        player.sendMessage(Text.of(TextColors.YELLOW, "Added ", newMembers.size(), " players to the region."));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Add a player to a region"))
                .arguments(GenericArguments.allOf(player(Text.of("player"))))
                .executor(new RegionAddMemberCommand())
                .build();
    }
}

