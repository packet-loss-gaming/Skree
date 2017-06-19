/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry.block;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.nitro.registry.item.CookedItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import com.skelril.skree.content.registry.block.container.GraveStone;
import com.skelril.skree.content.registry.block.container.GraveStoneTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CustomBlockSystem {

  public void preInit() {
    try {
      iterate(CustomBlockSystem.class.getDeclaredMethod("register", Object.class));
    } catch (NoSuchMethodException ex) {
      ex.printStackTrace();
    }
  }

  public void associate() {
    try {
      iterate(CustomBlockSystem.class.getDeclaredMethod("registerAssociates", Object.class));
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
    for (Field field : CustomBlockTypes.class.getFields()) {
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
      ((Block) block).setRegistryName("skree:" + ((ICustomBlock) block).__getID());

      GameRegistry.register((Block) block);

      ItemBlock itemBlock = new ItemBlock((Block) block);
      itemBlock.setUnlocalizedName("skree_" + ((ICustomBlock) block).__getID());
      itemBlock.setRegistryName("skree:" + ((ICustomBlock) block).__getID());
      GameRegistry.register(itemBlock);

      // Add selective hooks
      if (block instanceof EventAwareContent) {
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), block);
      }

      if (block instanceof GraveStone) {
        GameRegistry.registerTileEntity(GraveStoneTileEntity.class, "skree:grave_stone_ent");
      }
    } else {
      throw new IllegalArgumentException("Invalid custom item!");
    }
  }

  @SuppressWarnings("unused")
  private void registerAssociates(Object block) {
    if (block instanceof Block && block instanceof ICustomBlock) {
      if (block instanceof Craftable) {
        ((Craftable) block).registerRecipes();
      }

      if (block instanceof CookedItem) {
        ((CookedItem) block).registerIngredients();
      }
    } else {
      throw new IllegalArgumentException("Invalid custom item!");
    }
  }

  // Invoked via reflection
  @SuppressWarnings("unused")
  private void render(Object block) {
    if (block instanceof Block && block instanceof ICustomBlock) {
      if (Sponge.getPlatform().getExecutionType().isClient()) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        renderItem.getItemModelMesher().register(
            Item.getItemFromBlock((Block) block),
            0,
            new ModelResourceLocation(
                "skree:" + ((ICustomBlock) block).__getID(),
                "inventory"
            )
        );

      }
    } else {
      throw new IllegalArgumentException("Invalid custom item!");
    }
  }
}
