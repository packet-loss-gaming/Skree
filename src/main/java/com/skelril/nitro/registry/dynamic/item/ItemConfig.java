/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

import com.google.common.collect.Lists;
import com.skelril.nitro.registry.dynamic.ItemStackConfig;

import java.util.List;

public abstract class ItemConfig {
    private String id;

    private int maxUses;

    private ItemStackConfig repairItemStack;

    private int enchantability;
    private double hitPower;
    private double attackSpeed;

    public String getID() {
        return id;
    }

    public List<String> getMeshDefinitions() {
        return Lists.newArrayList(getID());
    }

    public int getMaxUses() {
        return maxUses;
    }

    public ItemStackConfig getRepairItemStack() {
        return repairItemStack;
    }

    public int getEnchantability() {
        return enchantability;
    }

    public double getHitPower() {
        return hitPower;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }
}
