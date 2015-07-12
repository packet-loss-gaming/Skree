/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.currency;

import com.skelril.nitro.registry.item.CraftableItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CondensedCofferItem extends CofferItem implements CraftableItem {

    private CofferItem parent;

    public CondensedCofferItem(String ID, CofferItem parent) {
        super(ID, parent.getCofferValue() * 9);
        this.parent = parent;
    }

    public CofferItem getParent() {
        return parent;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "AAA",
                "AAA",
                "AAA",
                'A', new ItemStack(parent)
        );
    }
}
