/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world;


import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class WorldListCommand implements CommandExecutor {

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);

    List<WorldEffectWrapper> worldEffectWrapperList = service.getEffectWrappers().stream().sorted(
        (a, b) -> a.getName().compareToIgnoreCase(b.getName())
    ).collect(Collectors.toList());

    for (WorldEffectWrapper wrapper : worldEffectWrapperList) {
      String worldType = wrapper.getName();
      if (!src.hasPermission("skree.world." + worldType.toLowerCase() + ".teleport")) {
        continue;
      }

      src.sendMessage(Text.of(TextColors.GOLD, "Available ", worldType, " worlds (click to teleport):"));
      for (World world : wrapper.getWorlds()) {
        String worldName = world.getName();
        String prettyName = worldName.replaceAll("_", " ");

        src.sendMessage(Text.of(
            TextColors.YELLOW,
            TextActions.runCommand("/world " + worldName),
            TextActions.showText(Text.of("Teleport to " + prettyName)),
            " - ", prettyName
        ));
      }
    }
    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("List available worlds"))
        .executor(new WorldListCommand()).build();
  }
}
