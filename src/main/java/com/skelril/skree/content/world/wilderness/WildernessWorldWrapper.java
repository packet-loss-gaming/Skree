/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.world.World;

import java.util.Collection;

public class WildernessWorldWrapper extends WorldEffectWrapperImpl {

    public WildernessWorldWrapper() {
        super("Wilderness");
    }

    public WildernessWorldWrapper(Collection<World> worlds) {
        super("Wilderness", worlds);
    }
}
