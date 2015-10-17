/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import com.skelril.skree.content.market.admin.MarketQuickAddCommand;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.spec.CommandSpec;

public class MarketAdminCommand {
    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Administrative commands for the market"))
                .child(MarketQuickAddCommand.aquireSpec(game), "quickadd")
                .build();
    }
}
