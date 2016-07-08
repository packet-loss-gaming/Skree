/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.world;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.content.world.BarrierWorldGeneratorModifier;
import com.skelril.skree.content.world.NoOreWorldGeneratorModifier;
import com.skelril.skree.content.world.SolidWorldGeneratorModifier;
import com.skelril.skree.content.world.VoidWorldGeneratorModifier;
import com.skelril.skree.content.world.wilderness.WildernessWorldGeneratorModifier;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

@NModule(name = "World Generator System")
public class WorldGeneratorSystem {
    @NModuleTrigger(trigger = "PRE_INITIALIZATION")
    public void init() {
        Sponge.getRegistry().register(WorldGeneratorModifier.class, new VoidWorldGeneratorModifier());
        Sponge.getRegistry().register(WorldGeneratorModifier.class, new SolidWorldGeneratorModifier());
        Sponge.getRegistry().register(WorldGeneratorModifier.class, new BarrierWorldGeneratorModifier());
        Sponge.getRegistry().register(WorldGeneratorModifier.class, new NoOreWorldGeneratorModifier());
        Sponge.getRegistry().register(WorldGeneratorModifier.class, new WildernessWorldGeneratorModifier());
    }
}
