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
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class RegionInfoCommand implements CommandExecutor {
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

        RegionReference ref = optRef.get();
        player.sendMessage(Text.of(TextColors.GOLD, "Region information for: ", (ref.isActive() ? TextColors.BLUE : TextColors.RED), ref.getReferred().getName().toUpperCase()));
        player.sendMessage(Text.of(ref.getReferred().getPowerLevel(), TextColors.YELLOW, " block power range"));
        player.sendMessage(Text.of(ref.getReferred().getMembers().size(), TextColors.YELLOW, " members"));
        player.sendMessage(Text.of(ref.getPoints().size(), TextColors.YELLOW, " active markers"));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Get region information"))
                .executor(new RegionInfoCommand())
                .build();
    }
}
