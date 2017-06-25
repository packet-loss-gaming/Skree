/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry.item;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.dynamic.LoaderRegistry;
import com.skelril.nitro.registry.dynamic.ability.AbilityRegistry;
import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import com.skelril.nitro.registry.dynamic.item.armor.BootsLoader;
import com.skelril.nitro.registry.dynamic.item.armor.ChestplateLoader;
import com.skelril.nitro.registry.dynamic.item.armor.HelmetLoader;
import com.skelril.nitro.registry.dynamic.item.armor.LeggingsLoader;
import com.skelril.nitro.registry.dynamic.item.bow.BowLoader;
import com.skelril.nitro.registry.dynamic.item.food.FoodLoader;
import com.skelril.nitro.registry.dynamic.item.simple.SimpleLoader;
import com.skelril.nitro.registry.dynamic.item.sword.SwordLoader;
import com.skelril.nitro.registry.item.CookedItem;
import com.skelril.nitro.registry.item.ICustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.system.registry.AbstractCustomRegistrySystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomItemSystem extends AbstractCustomRegistrySystem {
  private final GameIntegrator gameIntegrator;

  public CustomItemSystem(GameIntegrator gameIntegrator, AbilityRegistry abilityRegistry) {
    super("/registry/items/");
    this.gameIntegrator = gameIntegrator;

    LoaderRegistry dynamicItemRegistry = new LoaderRegistry();
    dynamicItemRegistry.registerConstant("SWORD_SPEED", "-2.4");

    loadFromResources(getResource -> {
      dynamicItemRegistry.registerLoader(new SimpleLoader(gameIntegrator, abilityRegistry), getResource.apply("simple"));
      dynamicItemRegistry.registerLoader(new SwordLoader(gameIntegrator, abilityRegistry), getResource.apply("swords"));
      dynamicItemRegistry.registerLoader(new BowLoader(gameIntegrator, abilityRegistry), getResource.apply("bows"));
      dynamicItemRegistry.registerLoader(new FoodLoader(gameIntegrator, abilityRegistry), getResource.apply("food"));
      dynamicItemRegistry.registerLoader(new HelmetLoader(gameIntegrator, abilityRegistry), getResource.apply("armor/helmets"));
      dynamicItemRegistry.registerLoader(new ChestplateLoader(gameIntegrator, abilityRegistry), getResource.apply("armor/chestplates"));
      dynamicItemRegistry.registerLoader(new LeggingsLoader(gameIntegrator, abilityRegistry), getResource.apply("armor/leggings"));
      dynamicItemRegistry.registerLoader(new BootsLoader(gameIntegrator, abilityRegistry), getResource.apply("armor/boots"));
      dynamicItemRegistry.loadAll();
    });
  }

  @Override
  public void preInit() {
    try {
      gameIntegrator.registerItems();
      iterate(CustomItemSystem.class.getDeclaredMethod("register", Object.class));
    } catch (NoSuchMethodException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void associate() {
    try {
      iterate(CustomItemSystem.class.getDeclaredMethod("registerAssociates", Object.class));
    } catch (NoSuchMethodException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void init() {
    try {
      gameIntegrator.registerItemRenderings();
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
    if (item instanceof Item && item instanceof ICustomItem) {
      ((Item) item).setUnlocalizedName("skree_" + ((ICustomItem) item).__getId());
      ((Item) item).setRegistryName("skree:" + ((ICustomItem) item).__getId());

      GameRegistry.register((Item) item);

      // Add selective hooks
      if (item instanceof EventAwareContent) {
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), item);
      }
    } else {
      throw new IllegalArgumentException("Invalid custom item!");
    }
  }

  @SuppressWarnings("unused")
  private void registerAssociates(Object item) {
    if (item instanceof Item && item instanceof ICustomItem) {
      // Add selective hooks
      if (item instanceof Craftable) {
        ((Craftable) item).registerRecipes();
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
    if (item instanceof Item && item instanceof ICustomItem) {
      if (Sponge.getPlatform().getExecutionType().isClient()) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        ItemModelMesher mesher = renderItem.getItemModelMesher();

        Optional<ItemMeshDefinition> optMeshDefinition = ((ICustomItem) item).__getCustomMeshDefinition();
        if (optMeshDefinition.isPresent()) {
          mesher.register((Item) item, optMeshDefinition.get());
        }

        List<String> variants = ((ICustomItem) item).__getMeshDefinitions();
        List<ResourceLocation> modelResources = new ArrayList<>();

        for (int i = 0; i < variants.size(); ++i) {
          ModelResourceLocation resourceLocation = new ModelResourceLocation(
              "skree:" + variants.get(i),
              "inventory"
          );

          if (!optMeshDefinition.isPresent()) {
            mesher.register((Item) item, i, resourceLocation);
          }
          modelResources.add(resourceLocation);
        }

        ModelBakery.registerItemVariants(
            (Item) item,
            modelResources.toArray(new ResourceLocation[modelResources.size()])
        );
      }
    } else {
      throw new IllegalArgumentException("Invalid custom item!");
    }
  }
}
