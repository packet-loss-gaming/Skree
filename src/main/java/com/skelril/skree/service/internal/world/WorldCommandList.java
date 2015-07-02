package com.skelril.skree.service.internal.world;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

/**
 * Created by cow_fu on 7/1/15 at 5:19 PM
 */
public class WorldCommandList implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)){
            src.sendMessage(Texts.of("You're not a player!"));
            return CommandResult.empty();
        }

        src.sendMessage(Texts.of("Please Select A World To Teleport To:"));

        TextBuilder worldMain = Texts.builder();
        TextBuilder worldSion = Texts.builder();
        TextBuilder worldWilderness = Texts.builder();

        worldMain.color(TextColors.AQUA);
        worldSion.color(TextColors.GREEN);
        worldWilderness.color(TextColors.DARK_RED);

        worldMain.append(Texts.of("Main World: Main"));
        worldSion.append(Texts.of("Building World: Sion"));
        worldWilderness.append(Texts.of("Wilderness World: Wilderness"));

        worldMain.onClick(TextActions.runCommand("/world Main"));
        worldSion.onClick(TextActions.runCommand("/world Sion"));
        worldWilderness.onClick(TextActions.runCommand("/world Wilderness"));

        src.sendMessage(Texts.of(worldMain));
        src.sendMessage(Texts.of(worldSion));
        src.sendMessage(Texts.of(worldWilderness));

        return CommandResult.success();
    }

    public static CommandSpec ListWorlds = CommandSpec.builder()
            .description(Texts.of("Teleport to a different world"))
            .permission("skree.world")
            .executor(new WorldCommandList()).build();
}
