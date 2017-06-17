/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.hoe;

import com.google.common.collect.Multimap;
import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.item.DegradableItem;
import com.skelril.nitro.registry.item.ICustomItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.skelril.nitro.registry.item.ICustomTool.ATTACK_DAMAGE_MODIFIER;
import static com.skelril.nitro.registry.item.ICustomTool.ATTACK_SPEED_MODIFIER;

public interface ICustomHoe extends ICustomItem, DegradableItem {

  @Override
  default String __getId() {
    return __getType() + "_hoe";
  }

  @Override
  default int __getMaxStackSize() {
    return 1;
  }

  String __getType();

  int __getMaxUses();

  default double __getHitPower() {
    return 0;
  }

  default double __getAttackSpeed() {
    return __getHarvestTier().getDamage() - 3.0F;
  }

  ItemTier __getHarvestTier();

  @Override
  default CreativeTabs __getCreativeTab() {
    return CreativeTabs.TOOLS;
  }

  Multimap<String, AttributeModifier> __superGetItemAttributeModifiers(EntityEquipmentSlot equipmentSlot);

  // Modified Native ItemTool methods

  default EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (!playerIn.canPlayerEdit(pos.offset(facing), facing, stack)) {
      return EnumActionResult.FAIL;
    } else {
      int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(stack, playerIn, worldIn, pos);
      if (hook != 0) {
        return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      }

      IBlockState iblockstate = worldIn.getBlockState(pos);
      Block block = iblockstate.getBlock();

      if (facing != EnumFacing.DOWN && worldIn.isAirBlock(pos.up())) {
        if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
          this.setBlock(stack, playerIn, worldIn, pos, Blocks.FARMLAND.getDefaultState());
          return EnumActionResult.SUCCESS;
        }

        if (block == Blocks.DIRT) {
          switch (iblockstate.getValue(BlockDirt.VARIANT)) {
            case DIRT:
              this.setBlock(stack, playerIn, worldIn, pos, Blocks.FARMLAND.getDefaultState());
              return EnumActionResult.SUCCESS;
            case COARSE_DIRT:
              this.setBlock(stack, playerIn, worldIn, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
              return EnumActionResult.SUCCESS;
          }
        }
      }

      return EnumActionResult.PASS;
    }
  }

  default void setBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state) {
    worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

    if (!worldIn.isRemote) {
      worldIn.setBlockState(pos, state, 11);
      stack.damageItem(1, player);
    }
  }

  /**
   * Returns True is the item is renderer in full 3D when hold.
   */
  @SideOnly(Side.CLIENT)
  default boolean isFull3D() {
    return true;
  }

  default Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
    Multimap<String, AttributeModifier> multimap = __superGetItemAttributeModifiers(equipmentSlot);

    if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
      multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", __getHitPower(), 0));
      multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", __getAttackSpeed(), 0));
    }

    return multimap;
  }
}
