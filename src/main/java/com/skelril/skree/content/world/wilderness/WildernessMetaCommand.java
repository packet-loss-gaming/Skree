/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.skree.service.WorldService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WildernessMetaCommand implements CommandExecutor {
    private DecimalFormat df = new DecimalFormat("#,###.##");

    private Text createLine(Map.Entry<Player, WildernessPlayerMeta> entry) {
        WildernessPlayerMeta meta = entry.getValue();

        return Text.of(
                TextColors.GREEN, entry.getKey().getName(), TextColors.YELLOW, ":  ",
                TextColors.YELLOW, "Level ", TextColors.WHITE, meta.getLevel(),
                TextColors.YELLOW, ", Ratio ", TextColors.WHITE, meta.getAttacks(),
                TextColors.YELLOW, ":", TextColors.WHITE, meta.getHits(),
                TextColors.YELLOW,  " (", TextColors.WHITE, df.format(meta.getRatio()),
                TextColors.YELLOW, ")"
        );
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);
        PaginationService pagination = Sponge.getServiceManager().provideUnchecked(PaginationService.class);

        WildernessWorldWrapper wrapper = service.getEffectWrapper(WildernessWorldWrapper.class).get();

        List<Text> result = wrapper.getMetaInformation().stream()
                .sorted((a, b) -> a.getKey().getName().compareTo(b.getKey().getName()))
                .map(this::createLine)
                .collect(Collectors.toList());

        pagination.builder()
                .contents(result)
                .title(Text.of(TextColors.GOLD, "Meta Info List"))
                .padding(Text.of(" "))
                .sendTo(src);

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Provides wilderness meta information"))
                .permission("skree.wilderness.meta")
                .executor(new WildernessMetaCommand()).build();
    }
}