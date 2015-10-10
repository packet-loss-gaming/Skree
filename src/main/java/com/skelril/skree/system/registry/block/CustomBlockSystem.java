/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry.block;

import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.nitro.selector.GameAwareContent;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Game;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CustomBlockSystem {

    private final SkreePlugin plugin;
    private final Game game;

    public CustomBlockSystem(SkreePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    public void preInit() {
        try {
            iterate(CustomBlockSystem.class.getDeclaredMethod("register", Object.class));
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public void init() {
        try {
            iterate(CustomBlockSystem.class.getDeclaredMethod("render", Object.class));
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }


    private void iterate(Method method) {
        method.setAccessible(true);
        for (Field field : CustomItemTypes.class.getFields()) {
            try {
                Object result = field.get(null);
                method.invoke(this, result);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Invoked via reflection
    @SuppressWarnings("unused")
    private void register(Object block) {
        if (block instanceof Block && block instanceof ICustomBlock) {
            ((Block) block).setUnlocalizedName("skree_" + ((ICustomBlock) block).__getID());

            GameRegistry.registerBlock((Block) block, ((ICustomBlock) block).__getID());

            // Add selective hooks
            if (block instanceof EventAwareContent) {
                game.getEventManager().registerListeners(plugin, block);
            }

            if (block instanceof GameAwareContent) {
                ((GameAwareContent) block).supplyGame(game);
            }
        } else {
            throw new IllegalArgumentException("Invalid custom item!");
        }
    }
}
