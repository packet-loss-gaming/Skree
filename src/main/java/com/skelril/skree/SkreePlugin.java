/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.skelril.skree.guice.SkreeGuiceModule;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

@Singleton
@Plugin(id = "Skree", name = "Skree", version = "1.0")
public class SkreePlugin {

    @Inject
    @Named(value = "Sponge")
    private PluginContainer mod;

    @Inject
    private Logger logger;

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        logger.info("Skree Started! Kaw!");

        // Use reflection to obtain the injector, so that we can have
        // access to things which are normally injected in a sponge system
        try {
            Method m = mod.getClass().getMethod("getInjector");
            Object res = m.invoke(mod);
            if (res instanceof Injector) {
                Injector injector = (Injector) res;
                injector.createChildInjector(new SkreeGuiceModule());
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
