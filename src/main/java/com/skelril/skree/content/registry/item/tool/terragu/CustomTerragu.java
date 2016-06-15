/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.terragu;

import com.skelril.nitro.registry.item.pickaxe.CustomPickaxe;
import com.skelril.nitro.registry.item.pickaxe.ICustomPickaxe;
import com.skelril.nitro.selector.EventAwareContent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public abstract class CustomTerragu extends CustomPickaxe implements ICustomPickaxe, EventAwareContent {
    @Override
    public String __getToolClass() {
        return "terragu";
    }

    private Map<Player, Direction> clickMap = new WeakHashMap<>();

    public void process(InteractBlockEvent.Primary.MainHand event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            clickMap.put(optPlayer.get(), event.getTargetSide().getOpposite());
        }
    }

    public void process(InteractBlockEvent.Secondary.MainHand event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (!optPlayer.isPresent()) return;

        Player player = optPlayer.get();

        Optional<org.spongepowered.api.item.inventory.ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (optHeldItem.isPresent()) {
            org.spongepowered.api.item.inventory.ItemStack held = optHeldItem.get();
            if (held.getItem() == this) {
                int newDist = getMaxEditDist(held) % 9 + 1;
                setMaxEditDist(held, newDist);
                player.setItemInHand(HandTypes.MAIN_HAND, held);
                player.sendMessage(Text.of(TextColors.YELLOW, "Distance set to: " + newDist));
            }
        }
    }

    public void process(ChangeBlockEvent.Break event) {
        if (event.getTransactions().size() > 1) {
            return;
        }

        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            Optional<Direction> optClickedDir = Optional.ofNullable(clickMap.get(player));
            Optional<org.spongepowered.api.item.inventory.ItemStack> optStack = player.getItemInHand(HandTypes.MAIN_HAND);
            if (optStack.isPresent() && optClickedDir.isPresent()) {
                if (optStack.get().getItem() == this) {
                    ItemStack stack = tf(optStack.get());
                    for (Transaction<BlockSnapshot> snapshot : event.getTransactions()) {
                        if (!snapshot.getOriginal().getLocation().isPresent()) {
                            return;
                        }

                        int maxDist = getMaxEditDist(stack);
                        int dmg = destroyLine(player, optClickedDir.get(), maxDist - 1, snapshot.getOriginal());
                        stack.damageItem(dmg, tf(player));
                        player.setItemInHand(HandTypes.MAIN_HAND, tf(stack));
                    }
                }
            }
        }
    }

    protected int getMaxEditDist(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getMaxEditDist(tf(stack));
    }

    protected int getMaxEditDist(ItemStack stack) {
        int dmgLeft = stack.getMaxDamage() - stack.getItemDamage();

        return Math.min(dmgLeft, getSetEditDist(stack));
    }

    protected int getSetEditDist(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getSetEditDist(tf(stack));
    }

    protected int getSetEditDist(ItemStack stack) {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("skree_terragu_data")) {
            return 1;
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_terragu_data");
        return tag.getInteger("edit_dist");
    }

    protected void setMaxEditDist(org.spongepowered.api.item.inventory.ItemStack stack, int dist) {
        setMaxEditDist(tf(stack), dist);
    }

    protected void setMaxEditDist(ItemStack stack, int dist) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (!stack.getTagCompound().hasKey("skree_terragu_data")) {
            stack.getTagCompound().setTag("skree_terragu_data", new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_terragu_data");
        tag.setInteger("edit_dist", dist);
    }

    protected int destroyLine(Player player, Direction dir, int maxDist, BlockSnapshot state) {
        Location<World> starting = state.getLocation().get();
        int i;
        for (i = 0; i < maxDist; ++i) {
            starting = starting.add(dir.toVector3d());
            if (starting.getBlockType() != state.getState().getType()) {
                break;
            }

            /*if (!starting.getExtent().digBlock(starting.getBlockPosition(), Cause.of(NamedCause.simulated(player)))) {
                break;
            }*/

            ((net.minecraft.world.World) starting.getExtent()).destroyBlock(
                    new BlockPos(starting.getX(), starting.getY(), starting.getZ()),
                    true
            );
        }
        return i;
    }

    // Modified Native Item methods

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        int editDist = getSetEditDist(stack);
        tooltip.add("Edit distance: " + editDist);
    }
}
