/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.sword;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.registry.item.DegradableItem;
import com.skelril.nitro.selector.EventAwareContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.gamemode.GameModes;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.EntityBreakBlockEvent;

public abstract class CustomSword extends DegradableItem implements CustomItem, EventAwareContent {

    // Skelril Methods

    // General

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    public abstract int __getMaxUses();

    public abstract String __getType();

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabCombat;
    }

    // Repair
    public abstract ItemStack __getRepairItemStack();

    // Combat Data
    public int __getDamageForUseOnEntity() {
        return 1;
    }

    public abstract double __getHitPower();

    // Enchantability
    public abstract int __getEnchantability();

    // Block Modification Data
    public int __getDamageForUseOnBlock() {
        return 2;
    }

    @Override
    public String __getID() {
        String typeStr = __getType();
        return "sword" + Character.toUpperCase(typeStr.charAt(0)) + typeStr.substring(1);
    }

    // Workaround since the sword code is hard coded to be ItemSword
    @Subscribe
    public void onBlockBreak(EntityBreakBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Optional<org.spongepowered.api.item.inventory.ItemStack> optHeldItem = ((Player) entity).getItemInHand();

            if (optHeldItem.isPresent() && ((Player) entity).getGameModeData().getGameMode() == GameModes.CREATIVE) {
                if (this.equals(optHeldItem.get().getItem())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // Modified Native ItemTool methods

    public float getStrVsBlock(ItemStack stack, Block p_150893_2_) {
        if (p_150893_2_ == Blocks.web) {
            return 15.0F;
        } else {
            Material material = p_150893_2_.getMaterial();
            return material != Material.plants && material != Material.vine && material != Material.coral && material != Material.leaves && material != Material.gourd ? 1.0F : 1.5F;
        }
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     *
     * @param target The Entity being hit
     * @param attacker the attacking entity
     */
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(__getDamageForUseOnEntity(), attacker);
        return true;
    }

    /**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn) {
        if ((double) blockIn.getBlockHardness(worldIn, pos) != 0.0D) {
            stack.damageItem(__getDamageForUseOnBlock(), playerIn);
        }

        return true;
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    @Override
    public boolean canHarvestBlock(Block blockIn) {
        return blockIn == Blocks.web;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    @Override
    public int getItemEnchantability() {
        return __getEnchantability();
    }

    /**
     * Return whether this item is repairable in an anvil.
     *
     * @param toRepair The ItemStack to be repaired
     * @param repair The ItemStack that should repair this Item (leather for leather armor, etc.)
     */
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = __getRepairItemStack();
        if (mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return super.getIsRepairable(toRepair, repair);
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", __getHitPower(), 0));
        return multimap;
    }

}
