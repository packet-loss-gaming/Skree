/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.shutdown;

import com.google.inject.Inject;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ShutdownService;
import com.skelril.skree.service.internal.shutdown.ShutdownCommand;
import com.skelril.skree.service.internal.shutdown.ShutdownServiceImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.spec.CommandSpec;

import static org.spongepowered.api.util.command.args.GenericArguments.*;

public class ShutdownSystem {

    private ShutdownService service;

    @Inject
    public ShutdownSystem(SkreePlugin plugin, Game game) {

        service = new ShutdownServiceImpl(plugin, game, game.getServer());

        try {
            game.getServiceManager().setProvider(plugin, ShutdownService.class, service);
            game.getCommandDispatcher().register(plugin, getCommandSpec(), "shutdown");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
        }
    }

    public ShutdownService getService() {
        return service;
    }

    private CommandSpec getCommandSpec() {
       return CommandSpec.builder()
               .setDescription(Texts.of("Shut the server off"))
               .setPermission("skree.shutdown")
               .setArguments(
                       flags().flag("f").buildWith(
                               seq(
                                       onlyOne(optionalWeak(integer(Texts.of("seconds")), 60)),
                                       optional(remainingJoinedStrings(Texts.of("message")))
                               )
                       )
               ).setExecutor(new ShutdownCommand(service)).build();
    }
}
