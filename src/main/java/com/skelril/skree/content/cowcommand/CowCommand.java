package com.skelril.skree.content.cowcommand;

import net.minecraft.command.CommandException;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.source.ConsoleSource;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

/**
 * Created by cow_fu on 6/15/15 at 8:18 PM
 */
public class CowCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        if(src instanceof Player){
            Player player = (Player) src;
            player.sendMessage(Texts.of("Hai "+player.getName()+", this is my first command :D"));
        }

        else
            src.sendMessage(Texts.of("Hai not player, its my first command :D!"));

        return CommandResult.success();
    }
    public static CommandSpec aquireSpec(){
        return CommandSpec.builder()
                .description(Texts.of("Cow's First Command :D"))
                .permission("skree.cowcommand")
                .executor(new CowCommand()).build();
    }



}