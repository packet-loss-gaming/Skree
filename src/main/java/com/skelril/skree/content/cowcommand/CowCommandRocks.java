package com.skelril.skree.content.cowcommand;

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

/**
 * Created by cow_fu on 7/1/15 at 2:53 PM
 */
public class CowCommandRocks implements CommandExecutor{
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Texts.of("Thank you, I've worked hard on it! :D"));
        return CommandResult.success();
    }

    public static CommandSpec cowrocks = CommandSpec.builder()
            .description(Texts.of("Cow's First Child Command :D"))
            .permission("skree.cowcommand")
            .executor(new CowCommandRocks()).build();
}
