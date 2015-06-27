/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry.item;

import com.skelril.nitro.registry.item.CookedItem;
import com.skelril.nitro.registry.item.CraftableItem;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.nitro.selector.GameAwareContent;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Game;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CustomItemSystem {

    private final SkreePlugin plugin;
    private final Game game;

    public CustomItemSystem(SkreePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    public void preInit() {
        try {
            iterate(CustomItemSystem.class.getDeclaredMethod("register", Object.class));
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public void init() {
        try {
            iterate(CustomItemSystem.class.getDeclaredMethod("render", Object.class));
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
    private void register(Object item) {
        if (item instanceof Item && item instanceof CustomItem) {
            ((Item) item).setUnlocalizedName("skree_" + ((CustomItem) item).getID());

            GameRegistry.registerItem((Item) item, ((CustomItem) item).getID(), "skree");

            // Add selective hooks
            if (item instanceof EventAwareContent) {
                game.getEventManager().register(plugin, item);
            }

            if (item instanceof GameAwareContent) {
                ((GameAwareContent) item).supplyGame(game);
            }

            if (item instanceof CraftableItem) {
                ((CraftableItem) item).registerRecipes();
            }

            if (item instanceof CookedItem) {
                ((CookedItem) item).registerIngredients();
            }
        } else {
            throw new IllegalArgumentException("Invalid custom item!");
        }
    }

    // Invoked via reflection
    @SuppressWarnings("unused")
    private void render(Object item) {
        if (item instanceof Item && item instanceof CustomItem) {
            if (game.getPlatform().getExecutionType().isClient()) {
                RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
                ItemModelMesher mesher = renderItem.getItemModelMesher();
                mesher.register(
                        (Item) item,
                        0,
                        new ModelResourceLocation(
                                "skree:" + ((CustomItem) item).getID(),
                                "inventory"
                        )
                );
            }
        } else {
            throw new IllegalArgumentException("Invalid custom item!");
        }
    }
}
