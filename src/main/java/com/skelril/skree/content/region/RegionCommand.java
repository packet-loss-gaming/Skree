/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.region;

import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class RegionCommand {
    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Manipulate regions"))
                .child(RegionSetNameCommand.aquireSpec(), "setname")
                .child(RegionSelectCommand.aquireSpec(), "select")
                .child(RegionAddMemberCommand.aquireSpec(), "addmember")
                .child(RegionRemMemberCommand.aquireSpec(), "removemember", "remmember")
                .child(RegionInfoCommand.aquireSpec(), "info", "i")
                .child(RegionListMembersCommand.aquireSpec(), "listmembers")
                .child(RegionListMarkersCommand.aquireSpec(), "listmarkers")
                .child(RegionCleanupCommand.aquireSpec(), "cleanup")
                .build();
    }
}
