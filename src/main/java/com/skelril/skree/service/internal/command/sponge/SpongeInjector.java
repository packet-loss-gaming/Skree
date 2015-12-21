/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.command.sponge;

import com.sk89q.intake.parametric.AbstractModule;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

public class SpongeInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(CommandSource.class).annotatedWith(Sender.class).toProvider(new SenderProvider());
        bind(Player.class).annotatedWith(Sender.class).toProvider(new PlayerSenderProvider());
        bind(Player.class).toProvider(new PlayerProvider());
    }
}