/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.region;

import com.skelril.skree.service.RegionService;
import com.skelril.skree.service.internal.region.Region;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RegionListMembersCommand implements CommandExecutor {
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

    Optional<Region> optRef = service.getSelectedRegion(player);
    if (!optRef.isPresent()) {
      player.sendMessage(Text.of(TextColors.RED, "You do not currently have a region selected."));
      return CommandResult.empty();
    }

    Region ref = optRef.get();

    UserStorageService userService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
    PaginationService pagination = Sponge.getServiceManager().provideUnchecked(PaginationService.class);

    List<Text> result = ref.getMembers().stream()
        .map(userService::get)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
        .map(a -> Text.of((a.isOnline() ? TextColors.GREEN : TextColors.RED), a.getName()))
        .collect(Collectors.toList());

    pagination.builder()
        .contents(result)
        .title(Text.of(TextColors.GOLD, "Region Members"))
        .padding(Text.of(" "))
        .sendTo(src);

    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("List members of region"))
        .executor(new RegionListMembersCommand())
        .build();
  }
}
