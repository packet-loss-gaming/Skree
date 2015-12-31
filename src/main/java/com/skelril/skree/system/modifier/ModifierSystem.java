/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.modifier;

import com.google.inject.Inject;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.modifier.ModExtendCommand;
import com.skelril.skree.content.modifier.ModifierNotifier;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.internal.modifier.LazyMySQLModifierService;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ProviderExistsException;

public class ModifierSystem implements ServiceProvider<ModifierService> {

    private ModifierService service;

    @Inject
    public ModifierSystem() {
        service = new LazyMySQLModifierService();

        // Register the service
        try {
            Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new ModifierNotifier());
            Sponge.getServiceManager().setProvider(SkreePlugin.inst(), ModifierService.class, service);
            Sponge.getCommandManager().register(SkreePlugin.inst(), ModExtendCommand.aquireSpec(), "modextend");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public ModifierService getService() {
        return service;
    }
}
