/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

import com.skelril.nitro.registry.CustomItemTier;
import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.VanillaItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;

public final class ItemTiers {
    public static final ItemTier CRYSTAL = new CustomItemTier("Crystal", 4, 3122, 10, 7, 10);
    public static final ItemTier DIAMOND = new VanillaItemTier("Diamond", ItemTool.ToolMaterial.EMERALD);
    public static final ItemTier GOLD = new VanillaItemTier("Gold", ItemTool.ToolMaterial.GOLD);
    public static final ItemTier IRON = new VanillaItemTier("Iron", ItemTool.ToolMaterial.IRON);
    public static final ItemTier STONE = new VanillaItemTier("Stone", ItemTool.ToolMaterial.STONE);
    public static final ItemTier WOOD = new VanillaItemTier("Wood", Item.ToolMaterial.WOOD);

    private ItemTiers() { }
}
