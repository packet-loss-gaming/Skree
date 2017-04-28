/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

import com.skelril.nitro.registry.dynamic.item.ability.AbilityCooldownHandler;
import com.skelril.nitro.registry.dynamic.item.ability.AbilityCooldownManager;
import com.skelril.nitro.registry.dynamic.item.ability.AbilityGroup;
import com.skelril.nitro.registry.dynamic.item.ability.grouptype.ClusterListener;
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
    private AbilityCooldownManager cooldownManager = new AbilityCooldownManager();

    public GameIntegrator(String modID) {
        this.modID = modID;
    }

    public void registerForProcessing(Item item, ItemConfig config) {
        constructedItems.add(new ItemDescriptor(item, config));
    }

    private void processAbilityGroup(Object mod, String id, AbilityGroup abilityGroup) {
        AbilityCooldownHandler cooldownHandler = new AbilityCooldownHandler(abilityGroup.getCoolDown(), cooldownManager);
        abilityGroup.getClusters().forEach((cluster) -> {
            ClusterListener listener = cluster.getListenerFor(id, cooldownHandler);
            Sponge.getEventManager().registerListeners(mod, listener);
        });
    }
    private void registerItem(ItemDescriptor descriptor) {
        Item item = descriptor.item;
        ItemConfig config = descriptor.config;

        String unlocalizedName = modID + "_" + config.getID();
        String id = modID + ":" + config.getID();

        item.setUnlocalizedName(unlocalizedName);
        item.setRegistryName(id);

        GameRegistry.register(item);

        Object mod = Sponge.getPluginManager().getPlugin(modID).get().getInstance().get();
        config.getAbilityGroups().forEach(abilityGroup -> processAbilityGroup(mod, id, abilityGroup));
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
