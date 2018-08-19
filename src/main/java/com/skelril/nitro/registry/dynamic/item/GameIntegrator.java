/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

import com.skelril.nitro.registry.dynamic.ability.AbilityApplicabilityTest;
import com.skelril.nitro.registry.dynamic.ability.AbilityGroup;
import com.skelril.nitro.registry.dynamic.ability.AbilityIntegrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;

import java.util.ArrayList;
import java.util.List;

public class GameIntegrator {
  private String modID;
  private List<ItemDescriptor> constructedItems = new ArrayList<>();
  private AbilityIntegrator abilityIntegrator = new AbilityIntegrator();

  public GameIntegrator(String modID) {
    this.modID = modID;
  }

  public void registerAbilities(AbilityApplicabilityTest applicabilityTest, List<AbilityGroup> abilityGroups) {
    Object mod = Sponge.getPluginManager().getPlugin(modID).get().getInstance().get();
    abilityIntegrator.processAbilityGroups(mod, applicabilityTest, abilityGroups);
  }

  public void registerForProcessing(Item item, ItemConfig config) {
    constructedItems.add(new ItemDescriptor(item, config));
  }

  private void registerItem(ItemDescriptor descriptor) {
    Item item = descriptor.item;
    ItemConfig config = descriptor.config;

    String unlocalizedName = modID + "_" + config.getID();
    String id = modID + ":" + config.getID();

    item.setTranslationKey(unlocalizedName);
    item.setRegistryName(id);

    GameRegistry.findRegistry(Item.class).register(item);

    AbilityApplicabilityTest applicabilityTest = config.getApplicabilityTest(id);
    registerAbilities(applicabilityTest, config.getAbilityGroups());
  }

  public void registerItems() {
    constructedItems.forEach(this::registerItem);
  }

  private void registerItemRendering(ItemDescriptor descriptor) {
    Item item = descriptor.item;
    ItemConfig config = descriptor.config;

    RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    ItemModelMesher mesher = renderItem.getItemModelMesher();

    // TODO Custom mesher currently not supported
    // Optional<ItemMeshDefinition> optMeshDefinition = (item).__getCustomMeshDefinition();
    // if (optMeshDefinition.isPresent()) {
    //     mesher.register((Item) item, optMeshDefinition.get());
    // }

    List<String> variants = config.getMeshDefinitions();
    List<ResourceLocation> modelResources = new ArrayList<>();

    for (int i = 0; i < variants.size(); ++i) {
      ModelResourceLocation resourceLocation = new ModelResourceLocation(
          modID + ":" + variants.get(i),
          "inventory"
      );

      // if (!optMeshDefinition.isPresent()) {
      mesher.register(item, i, resourceLocation);
      // }
      modelResources.add(resourceLocation);
    }

    ModelBakery.registerItemVariants(item, modelResources.toArray(new ResourceLocation[modelResources.size()]));
  }

  public void registerItemRenderings() {
    if (!Sponge.getPlatform().getExecutionType().isClient()) {
      return;
    }

    constructedItems.forEach(this::registerItemRendering);
  }

  private class ItemDescriptor {
    public Item item;
    public ItemConfig config;

    public ItemDescriptor(Item item, ItemConfig config) {
      this.item = item;
      this.config = config;
    }
  }
}
