package com.skelril.skree.content.registry.block.container;

import com.skelril.nitro.registry.block.ICustomBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GraveStone extends BlockContainer implements ICustomBlock {
    public static final PropertyDirection FACING_PROP = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public GraveStone() {
        super(new Material(MapColor.stoneColor)); // Create a new non-burnable stone like block
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING_PROP, EnumFacing.NORTH));
    }

    @Override
    public String __getID() {
        return "grave_stone";
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new GraveStoneTileEntity();
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING_PROP, placer.func_174811_aO());
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

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING_PROP, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(FACING_PROP)).getIndex();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING_PROP);
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
                ((IInventory) tileEntity).setInventorySlotContents(
                        i,
                        (net.minecraft.item.ItemStack) (Object) items.get(i)
                );
            }
        }

        return items.subList(remnants, items.size());
    }

    public List<ItemStack> createGraveFromDeath(DestructEntityEvent.Death event) {
        Entity target = event.getTargetEntity();
        if (target instanceof EntityPlayer) {
            net.minecraft.item.ItemStack[] mainInv = ((EntityPlayer) target).inventory.mainInventory;
            net.minecraft.item.ItemStack[] armInv = ((EntityPlayer) target).inventory.armorInventory;

            List<ItemStack> items = new ArrayList<>();

            Collections.addAll(items, (ItemStack[]) (Object[]) mainInv);
            Collections.addAll(items, (ItemStack[]) (Object[]) armInv);

            items.removeAll(Collections.singleton(null));

            ((EntityPlayer) target).inventory.clear();

            return createGrave(items, target.getLocation());
        }
        return null;
    }
}
