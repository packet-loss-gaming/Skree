/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.zone;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.position.PositionRandomizer;
import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.world.instance.InstanceWorldWrapper;
import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.ZoneService;
import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static com.skelril.skree.content.registry.item.zone.ZoneItemUtil.*;

public class ZoneMasterOrb extends CustomItem implements EventAwareContent, Craftable {

    @Override
    public String __getID() {
        return "zone_master_orb";
    }

    @Override
    public List<String> __getMeshDefinitions() {
        List<String> baseList = super.__getMeshDefinitions();
        baseList.add("zone_master_orb_active");
        return baseList;
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabMaterials;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "BBB",
                "BAB",
                "BBB",
                'A', new ItemStack(CustomItemTypes.FAIRY_DUST),
                'B', new ItemStack(Blocks.glass)
        );
        GameRegistry.addRecipe(new ZoneMasterOrbRecipie(
                "Shnuggles Prime",
                new ItemStack(this),
                new ItemStack(Items.rotten_flesh)
        ));
        GameRegistry.addRecipe(new ZoneMasterOrbRecipie(
                "Patient X",
                new ItemStack(this),
                new ItemStack(CustomItemTypes.PHANTOM_HYMN)
        ));
        GameRegistry.addRecipe(new ZoneMasterOrbRecipie(
                "Gold Rush",
                new ItemStack(this),
                new ItemStack(Items.gold_ingot)
        ));
        GameRegistry.addRecipe(new ZoneMasterOrbRecipie(
                "Cursed Mine",
                new ItemStack(this),
                new ItemStack(Items.iron_pickaxe)
        ));
        GameRegistry.addRecipe(new ZoneMasterOrbRecipie(
                "Temple of Fate",
                new ItemStack(this),
                new ItemStack(Items.feather)
        ));
        GameRegistry.addRecipe(new ZoneMasterOrbRecipie(
                "Catacombs",
                new ItemStack(this),
                new ItemStack(Items.bone)
        ));
        GameRegistry.addRecipe(new ZoneMasterOrbRecipie(
                "Jungle Raid",
                new ItemStack(this),
                new ItemStack(Items.dye, 1, 3)
        ));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(net.minecraft.item.Item itemIn, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public String getHighlightTip(ItemStack item, String displayName) {
        Optional<String> optContained = getZone(item);

        return optContained.isPresent() ? optContained.get() + " " + displayName: displayName;
    }


    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event) {
        purgeZoneItems(event.getTargetEntity(), null);
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            Optional<org.spongepowered.api.item.inventory.ItemStack> optItemStack = player.getItemInHand();
            if (optItemStack.isPresent()) {
                ItemStack itemStack = tf(optItemStack.get());
                if (isZoneMasterItem(itemStack)) {
                    if (isAttuned(itemStack)) {
                        if (isInInstanceWorld(player)) {
                            player.sendMessage(Text.of(TextColors.RED, "You cannot start an instance from within an instance."));
                            event.setCancelled(true);
                            return;
                        }

                        Optional<ZoneService> optService = Sponge.getServiceManager().provide(ZoneService.class);
                        if (optService.isPresent()) {
                            ZoneService service = optService.get();
                            List<Player> group = new ArrayList<>();
                            group.add(player);
                            for (Player aPlayer : Sponge.getServer().getOnlinePlayers()) {
                                ItemStack[] itemStacks = tf(aPlayer).inventory.mainInventory;
                                for (ItemStack aStack : itemStacks) {
                                    if (!hasSameZoneID(itemStack, aStack)) {
                                        continue;
                                    }

                                    if (isAttuned(aStack) && isZoneSlaveItem(aStack)) {
                                        Optional<Player> optZoneOwner = getGroupOwner(aStack);
                                        if (optZoneOwner.isPresent()) {
                                            group.add(aPlayer);
                                            break;
                                        }
                                    }
                                }
                            }

                            for (int i = group.size() - 1; i >= 0; --i) {
                                purgeZoneItems(group.get(i), itemStack);
                                // createLightningStrike(group.get(i)); SpongeCommon/420
                                moveToInstanceIsle(group.get(i));
                            }

                            service.requestZone(getZone(itemStack).get(), group, result -> {
                                if (result.isPresent()) {
                                    result.get().stream().filter(entry -> entry.getValue() != ZoneStatus.ADDED).forEach(entry -> {
                                        player.setLocation(player.getWorld().getSpawnLocation());
                                        player.sendMessage(Text.of(TextColors.RED, "You could not be added to the zone."));
                                    });
                                }
                            });
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    private void createLightningStrike(Player player) {
        Location<World> loc = player.getLocation();
        Optional<Entity> optLightning = loc.getExtent().createEntity(EntityTypes.LIGHTNING, loc.getPosition());
        if (optLightning.isPresent()) {
            Lightning lightning = (Lightning) optLightning.get();
            lightning.setEffect(true);
            loc.getExtent().spawnEntity(lightning, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
        }
    }

    private void moveToInstanceIsle(Player player) {
        WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);
        Vector3i randomizedPos = new PositionRandomizer(5, 0, 5).createPosition3i(new Vector3i(122, 94, 103));
        World targetWorld = service.getEffectWrapper(MainWorldWrapper.class).get().getWorlds().iterator().next();
        Location<World> targetPos = targetWorld.getLocation(randomizedPos);
        player.setLocation(targetPos);
        player.sendMessage(Text.of(TextColors.YELLOW, "Your instance is building..."));
        player.sendMessage(Text.of(TextColors.YELLOW, "You will automatically be teleported when it's finished."));
    }

    private boolean isInInstanceWorld(Player player) {
        Optional<WorldService> optWorldService = Sponge.getServiceManager().provide(WorldService.class);
        if (optWorldService.isPresent()) {
            WorldService worldService = optWorldService.get();
            WorldEffectWrapper wrapper = worldService.getEffectWrapper(InstanceWorldWrapper.class).get();

            if (wrapper.getWorlds().contains(player.getLocation().getExtent())) {
                return true;
            }
        }
        return false;
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent.Primary event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        Entity targetEntity = event.getTargetEntity();
        if (optPlayer.isPresent() && targetEntity instanceof Player) {
            Player player = optPlayer.get();
            Optional<org.spongepowered.api.item.inventory.ItemStack> optItemStack = player.getItemInHand();
            if (optItemStack.isPresent()) {
                org.spongepowered.api.item.inventory.ItemStack itemStack = optItemStack.get();
                if (this.equals(itemStack.getItem()) && isAttuned(itemStack)) {
                    Player targetPlayer = (Player) targetEntity;
                    if (playerAlreadyHasInvite(itemStack, targetPlayer)) {
                        player.sendMessage(
                                Text.of(TextColors.RED, targetPlayer.getName() + " already has an invite.")
                        );
                    } else {
                        org.spongepowered.api.item.inventory.ItemStack newStack = createForMaster(itemStack, player);
                        tf(targetPlayer).inventory.addItemStackToInventory(tf(newStack));
                        player.sendMessage(
                                Text.of(TextColors.GOLD, targetPlayer.getName() + " has been given invite.")
                        );
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onDropItem(DropItemEvent.Dispense event) {
        event.getEntities().stream().filter(entity -> entity instanceof Item).forEach(entity -> {
            ItemStack stack = ((EntityItem) entity).getEntityItem();
            if (isZoneMasterItem(stack) && isAttuned(stack)) {
                rescindGroupInvite(stack);
                ItemStack reset = new ItemStack(CustomItemTypes.ZONE_MASTER_ORB);
                setMasterToZone(reset, getZone(stack).get());
                entity.offer(Keys.REPRESENTED_ITEM, tf(reset).createSnapshot());
            }
        });
    }

    // Modified Native Item methods

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        Optional<String> optZoneName = getZone(tf(stack));
        if (optZoneName.isPresent()) {
            tooltip.add("Zone: " + optZoneName.get());
            Optional<Integer> playerCount = getGroupSize(stack);
            if (playerCount.isPresent()) {
                Optional<Integer> maxPlayerCount = getMaxGroupSize(stack);
                tooltip.add("Players: " + playerCount.get() + " / " + (!maxPlayerCount.isPresent() ? "Unlimited" : maxPlayerCount.get()));
            }
        }
    }
}
