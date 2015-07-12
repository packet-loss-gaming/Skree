package com.skelril.skree.content.world;

import com.google.common.base.Optional;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import org.spongepowered.api.Game;
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
import org.spongepowered.api.world.World;

public class WorldListCommand implements CommandExecutor {

    private Game game;

    public WorldListCommand(Game game) {
        this.game = game;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        TextBuilder header = Texts.builder();
        header.color(TextColors.GOLD);
        header.append(Texts.of("Available worlds (click to teleport):"));

        src.sendMessage(header.build());

        Optional<WorldService> service = game.getServiceManager().provide(WorldService.class);

        for (WorldEffectWrapper wrapper : service.get().getEffectWrappers()) {
            String worldType = wrapper.getName();
            for (World world : wrapper.getWorlds()) {
                TextBuilder builder = Texts.builder();
                String worldName = world.getName();
                builder.append(Texts.of(worldName + " [" + worldType + "]"));
                builder.color(TextColors.GREEN);
                builder.onClick(TextActions.runCommand("/world " + worldName));
                builder.onHover(TextActions.showText(Texts.of("Teleport to " + worldName)));
                src.sendMessage(builder.build());
            }
        }
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
            .description(Texts.of("List available worlds"))
            .permission("skree.world")
            .executor(new WorldListCommand(game)).build();
    }
}
