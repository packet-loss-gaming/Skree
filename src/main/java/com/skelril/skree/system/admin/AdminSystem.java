/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.admin;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.admin.InventoryEditCommand;
import org.spongepowered.api.Sponge;

@NModule(name = "Admin System")
public class AdminSystem {
    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        Sponge.getCommandManager().register(SkreePlugin.inst(), InventoryEditCommand.aquireSpec(), "invedit");
    }
}
