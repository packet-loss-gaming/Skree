/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.container;

import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.service.RespawnQueueService;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;

import java.util.*;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class GraveStone extends BlockContainer implements ICustomBlock {
  public static final PropertyDirection FACING_PROP = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

  public GraveStone() {
    super(new Material(MapColor.STONE)); // Create a new non-burnable stone like block
    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING_PROP, EnumFacing.NORTH));

    // Data applied for Vanilla blocks in net.minecraft.block.Block
    this.setLightLevel(0.3F);
    this.setResistance(6000000.0F);
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return new AxisAlignedBB(0.125F, 0.125F, 0.125F, 0.875F, 0.875F, 0.875F);
  }

  @Override
  public String __getID() {
    return "grave_stone";
  }

  @Override
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new GraveStoneTileEntity();
  }

  @Override
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    return this.getDefaultState().withProperty(FACING_PROP, placer.getHorizontalFacing());
  }

  @Override
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
    TileEntity tileEntity = worldIn.getTileEntity(pos);

    if (tileEntity instanceof IInventory) {
      InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileEntity);
    }

    super.breakBlock(worldIn, pos, state);
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return null;
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing enumfacing = EnumFacing.getFront(meta);

    if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
      enumfacing = EnumFacing.NORTH;
    }

    return this.getDefaultState().withProperty(FACING_PROP, enumfacing);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(FACING_PROP).getIndex();
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING_PROP);
  }

  public List<ItemStack> createGrave(List<ItemStack> items, Location<org.spongepowered.api.world.World> pos) {
    World world = (World) pos.getExtent();
    BlockPos bPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
    world.setBlockState(bPos, getDefaultState());
    TileEntity tileEntity = world.getTileEntity(bPos);

    int remnants = 0;
    if (tileEntity instanceof IInventory) {
      remnants = Math.min(items.size(), ((IInventory) tileEntity).getSizeInventory());
      for (int i = 0; i < remnants; ++i) {
        ((IInventory) tileEntity).setInventorySlotContents(i, tf(items.get(i)));
      }
    }

    return items.subList(remnants, items.size());
  }

  public void createGraveDropExcess(List<ItemStack> items, Location<org.spongepowered.api.world.World> pos) {
    List<ItemStack> excess = createGrave(items, pos);
    World world = (World) pos.getExtent();
    for (ItemStack stack : excess) {
      world.spawnEntityInWorld(
          new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), tf(stack))
      );
    }
  }

  public void createGraveFromDeath(DestructEntityEvent.Death event) {
    Entity target = event.getTargetEntity();
    if (target instanceof Player) {
      Player player = (Player) target;

      List<ItemStack> items = new ArrayList<>();

      Optional<ItemStack> optStack;
      do {
        optStack = player.getInventory().poll();
      } while (optStack.isPresent() && items.add(optStack.get()));

      Iterator<ItemStack> it = items.iterator();
      Optional<RespawnQueueService> optService = Sponge.getServiceManager().provide(RespawnQueueService.class);
      if (optService.isPresent()) {
        RespawnQueueService service = optService.get();
        while (it.hasNext()) {
          if (it.next().getItem() == CustomItemTypes.NETHER_BOWL) {
            it.remove();
            ItemStack stack = tf(new net.minecraft.item.ItemStack(CustomItemTypes.NETHER_BOWL));
            CustomItemTypes.NETHER_BOWL.setDestination(stack, player.getLocation());
            service.enque(player, stack);
            break;
          }
        }
      }

      Task.builder().execute(() -> {
        createGraveDropExcess(items, target.getLocation());
      }).delayTicks(1).submit(SkreePlugin.inst());
    }
  }
}
