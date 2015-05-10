/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.modifier;

import com.google.inject.Inject;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.internal.modifier.mysql.lazy.LazyMySQLModifierService;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.ProviderExistsException;

public class ModifierSystem {

    private ModifierService service;

    @Inject
    public ModifierSystem(SkreePlugin plugin, Game game) {
        // TODO add database
        service = new LazyMySQLModifierService(game, "", "modifiers");
        try {
            game.getServiceManager().setProvider(plugin, ModifierService.class, service);
        } catch (ProviderExistsException e) {
            e.printStackTrace();
        }
    }

    public ModifierService getService() {
        return service;
    }
}
