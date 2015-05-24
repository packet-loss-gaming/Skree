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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Game;

public class CustomItemSystem {

    private final SkreePlugin plugin;
    private final Game game;

    private CrystalSword crystalSword;
    private HackBook hackBook;
    private Luminositor luminositor;

    public CustomItemSystem(SkreePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    public void preInit() {
        crystalSword = register(new CrystalSword());
        hackBook = register(new HackBook());
        luminositor = register(new Luminositor());
    }

    public void init() {
        render(crystalSword);
        render(hackBook);
        render(luminositor);
    }

    private <T extends Item & CustomItem> T register(T item) {
        try {
            item.setUnlocalizedName("skree_" + item.getID());

            GameRegistry.registerItem(item, item.getID(), "skree");

            game.getEventManager().register(plugin, item);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return item;
    }

    private <T extends Item & CustomItem> T render(T item) {
        try {
            if (game.getPlatform().getType().isClient()) {
                RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
                ItemModelMesher mesher = renderItem.getItemModelMesher();
                mesher.register(
                        item,
                        0,
                        new ModelResourceLocation(
                                "skree:" + item.getID(),
                                "inventory"
                        )
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return item;
    }
}
