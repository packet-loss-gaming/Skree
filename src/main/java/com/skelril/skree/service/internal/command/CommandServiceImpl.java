/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.command;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sk89q.intake.*;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.dispatcher.SimpleDispatcher;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;
import com.sk89q.intake.util.auth.AuthorizationException;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.CommandService;
import com.skelril.skree.service.internal.command.sponge.CommandAdapter;
import com.skelril.skree.service.internal.command.sponge.SpongeInjector;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class CommandServiceImpl implements CommandService {

    private SimpleDispatcher dispatcher = new SimpleDispatcher();
    private ParametricBuilder builder;

    public CommandServiceImpl() {
        Injector injector = Intake.createInjector();
        injector.install(new PrimitivesModule());
        injector.install(new SpongeInjector());

        builder = new ParametricBuilder(injector);
        dispatcher = new SimpleDispatcher();
    }

    @Override
    public void registerCommands(Object object) {
        builder.registerMethodsAsCommands(dispatcher, object);
    }

    @Override
    public void registerCommands() {
        for (CommandMapping command : dispatcher.getCommands()) {
            CommandAdapter adapter = new CommandAdapter(command) {
                @Override
                public CommandResult process(CommandSource source, String arguments) throws org.spongepowered.api.command.CommandException {
                    Namespace namespace = new Namespace();
                    namespace.put(CommandSource.class, source);
                    try {
                        if (command.getCallable().call(arguments, namespace, Lists.newArrayList(command.getPrimaryAlias()))) {
                            return CommandResult.success();
                        }
                    } catch (InvalidUsageException e) {
                        sendCommandUsageHelp(e, source);
                    } catch (CommandException e) {
                        source.sendMessage(Texts.of(TextColors.RED, e.getMessage()));
                    } catch (InvocationCommandException e) {
                        source.sendMessage(Texts.of(TextColors.RED, "The command could not complete successfully do to an internal error."));
                        e.printStackTrace();
                    } catch (AuthorizationException e) {
                        source.sendMessage(Texts.of(TextColors.RED, "You do not have permission."));
                    }
                    return CommandResult.empty();
                }

                @Override
                public List<String> getSuggestions(CommandSource source, String arguments) throws org.spongepowered.api.command.CommandException {
                    Namespace namespace = new Namespace();
                    namespace.put(CommandSource.class, source);
                    try {
                        return dispatcher.getSuggestions(arguments, namespace);
                    } catch (CommandException e) {
                        return Lists.newArrayList(e.getMessage());
                    }
                }
            };
            SkreePlugin.inst().getGame().getCommandManager().register(SkreePlugin.inst(), adapter, command.getAllAliases());
        }
    }

    private void sendCommandUsageHelp(InvalidUsageException e, CommandSource sender) {
        String commandString = Joiner.on(' ').join(e.getAliasStack());
        Description description = e.getCommand().getDescription();

        if (e.isFullHelpSuggested()) {
            if (e.getCommand() instanceof Dispatcher) {
                sender.sendMessage(Texts.of("Subcommands:"));

                Dispatcher dispatcher = (Dispatcher) e.getCommand();
                List<CommandMapping> list = new ArrayList<>(dispatcher.getCommands());

                for (CommandMapping mapping : list) {
                    sender.sendMessage(Texts.of(
                            "/" + (commandString.isEmpty() ? "" : commandString + " ") + mapping.getPrimaryAlias() +
                                    ": " + mapping.getDescription().getShortDescription()));
                }
            } else {
                sender.sendMessage(Texts.of("Help for ", commandString));

                if (description.getUsage() != null) {
                    sender.sendMessage(Texts.of("Usage /", commandString + " " + description.getUsage()));
                } else {
                    sender.sendMessage(Texts.of("No usage information available."));
                }

                if (description.getHelp() != null) {
                    sender.sendMessage(Texts.of(description.getHelp()));
                } else if (description.getShortDescription() != null) {
                    sender.sendMessage(Texts.of(description.getShortDescription()));
                } else {
                    sender.sendMessage(Texts.of("No additional help available."));
                }
            }

            String message = e.getMessage();
            if (message != null) {
                sender.sendMessage(Texts.of(message));
            }
        } else {
            String message = e.getMessage();
            sender.sendMessage(Texts.of(
                    TextColors.RED,
                    message != null ? message : "Invalid command usage, no further information available."
            ));
            sender.sendMessage(Texts.of(
                    TextColors.RED,
                    "Usage ",
                    TextColors.BLUE,
                    "/",
                    commandString + " " + e.getCommand().getDescription().getUsage()
            ));
        }
    }
}
