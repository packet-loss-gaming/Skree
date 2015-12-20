/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree;

import com.skelril.skree.system.registry.block.CustomBlockSystem;
import com.skelril.skree.system.registry.item.CustomItemSystem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * This "mod" exists solely due to the fact that the FMLInitializationEvent
 * differs from Sponge's InitializationEVent. Once this discrepancy is
 * resolved, the "Dirty Skree" mod can be removed from the project.
 */
@Mod(modid = "skree", version = "1.0", name = "FML Skree")
public class FMLSkree {

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        SkreePlugin.customItemSystem = new CustomItemSystem(SkreePlugin.inst(), SkreePlugin.game());
        SkreePlugin.customItemSystem.preInit();

        SkreePlugin.customBlockSystem = new CustomBlockSystem(SkreePlugin.inst(), SkreePlugin.game());
        SkreePlugin.customBlockSystem.preInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        SkreePlugin.customItemSystem.init();
        SkreePlugin.customBlockSystem.init();
    }
}
