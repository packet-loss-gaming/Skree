/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.guice;

import com.google.inject.AbstractModule;
import com.skelril.skree.service.system.shutdown.ShutdownSystem;
import com.skelril.skree.service.system.world.WorldSystem;
import com.skelril.skree.service.system.zone.ZoneSystem;

public class SkreeGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ShutdownSystem.class).asEagerSingleton();
        bind(WorldSystem.class).asEagerSingleton();
        bind(ZoneSystem.class).asEagerSingleton();
    }
}
