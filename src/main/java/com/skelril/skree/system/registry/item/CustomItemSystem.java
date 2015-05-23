/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry.item;

import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.admin.HackBook;
import com.skelril.skree.content.registry.item.generic.Luminositor;
import com.skelril.skree.content.registry.item.weapon.sword.CrystalSword;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Game;

public class CustomItemSystem {

    private final SkreePlugin plugin;
    private final Game game;

    public CustomItemSystem(SkreePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    public void init() {
        register(new CrystalSword());
        register(new HackBook());
        register(new Luminositor());
    }

    private <T extends Item & CustomItem> T register(T item) {
        GameRegistry.registerItem(item, item.getID());
        game.getEventManager().register(plugin, item);
        return item;
    }
}
