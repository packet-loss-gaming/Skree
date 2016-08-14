/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.mobdensity;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.mobdensity.MobDensityListener;
import org.spongepowered.api.Sponge;

@NModule(name = "Mob Density System")
public class MobDensitySystem {
    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new MobDensityListener());
    }
}
