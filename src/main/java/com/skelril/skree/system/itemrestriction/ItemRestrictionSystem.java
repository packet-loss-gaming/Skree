/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.itemrestriction;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.itemrestriction.ItemCraftBlockingListener;
import com.skelril.skree.content.itemrestriction.ItemInteractBlockingListener;
import com.skelril.skree.system.ConfigLoader;
import org.spongepowered.api.Sponge;

import java.io.IOException;
import java.util.Set;

@NModule(name = "Item Restriction System")
public class ItemRestrictionSystem {
    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        try {
            ItemRestrictionConfig config = ConfigLoader.loadConfig("item_restriction.json", ItemRestrictionConfig.class);
            Set<String> blockedItems = config.getBlockedItems();
            Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new ItemCraftBlockingListener(blockedItems));
            Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new ItemInteractBlockingListener(blockedItems));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}