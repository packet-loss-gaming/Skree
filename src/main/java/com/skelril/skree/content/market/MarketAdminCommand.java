/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import com.skelril.skree.content.market.admin.*;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class MarketAdminCommand {
    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Administrative commands for the market"))
                .permission("skree.market.admin")
                .child(MarketQuickAddCommand.aquireSpec(), "quickadd", "add")
                .child(MarketAddAliasCommand.aquireSpec(), "addalias")
                .child(MarketRemoveAliasCommand.aquireSpec(), "remalias")
                .child(MarketSetPriceCommand.aquireSpec(), "setprice")
                .child(MarketSetPrimaryAliasCommand.aquireSpec(), "setprimaryalias", "setpalias")
                .child(MarketTrackItemCommand.aquireSpec(), "track")
                .child(MarketUntrackItemComand.aquireSpec(), "untrack", "remove")
                .build();
    }
}
