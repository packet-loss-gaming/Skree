package com.skelril.skree.service.internal.world;

import com.google.common.base.Optional;
import com.skelril.skree.service.WorldService;
import org.spongepowered.api.Game;
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

    private Game game;

    public WorldCommandList(Game game){
        this.game = game;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player){
            src.sendMessage(Texts.of("Please Select A World To Teleport To:"));
        }
        else
        {src.sendMessage(Texts.of("List Of All Worlds That Can Be Teleported To"));}

        Optional<WorldService> service = game.getServiceManager().provide(WorldService.class);
        TextBuilder builder = Texts.builder();
        String worldName;


        for(WorldEffectWrapper wrapper: service.get().getEffectWrappers()){
            worldName = wrapper.getName();
            if (worldName.contentEquals("Build")) {
                worldName = "Sion";
            }

            builder.color(TextColors.GREEN);
            builder.append(Texts.of(worldName));
            builder.onClick(TextActions.runCommand("/world " + worldName));

            src.sendMessage(Texts.of(builder.build()));

            builder.removeAll();
        }

        return CommandResult.success();
    }

    public static CommandSpec ListWorlds(Game game){
        return CommandSpec.builder()
            .description(Texts.of("Teleport to a different world"))
            .permission("skree.world")
            .executor(new WorldCommandList(game)).build();}

}
