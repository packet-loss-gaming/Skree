/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.region;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.RegionService;
import com.skelril.skree.service.internal.region.Region;
import com.skelril.skree.service.internal.region.RegionPoint;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class RegionMaster extends Block implements ICustomBlock, EventAwareContent {

    private Map<Player, Vector3d> recentlyClicked = new WeakHashMap<>();

    public RegionMaster() {
        super(new Material(MapColor.stoneColor));
        this.setCreativeTab(CreativeTabs.tabDecorations);

        // Data applied for Vanilla blocks in net.minecraft.block.Block
        this.setHardness(1.5F);
        this.setResistance(6000000.0F);
        this.setStepSound(soundTypePiston);
    }

    @Override
    public String __getID() {
        return "region_master";
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            for (Transaction<BlockSnapshot> block : event.getTransactions()) {
                if (!block.isValid()) {
                    continue;
                }

                if (block.getFinal().getState().getType() == this) {
                    Optional<RegionService> optService = Sponge.getServiceManager().provide(RegionService.class);
                    if (optService.isPresent()) {
                        RegionService service = optService.get();
                        Optional<Location<World>> optLoc = block.getFinal().getLocation();
                        if (optLoc.isPresent()) {
                            Optional<Region> optOriginRef = service.getSelectedRegion(player);
                            Optional<Region> optRef = service.getOrCreate(optLoc.get(), player);
                            if (optRef.isPresent()) {
                                Region ref = optRef.get();

                                Vector3d masterBlock = ref.getMasterBlock();
                                Vector3d blockPos = optLoc.get().getPosition();
                                // Determine if this is a new region or not
                                if (!masterBlock.equals(blockPos)) {
                                    if (blockPos.equals(recentlyClicked.get(player))) {
                                        // Update the master block
                                        ref.setMasterBlock(new RegionPoint(blockPos));

                                        // Delete the existing master block
                                        optLoc.get().getExtent().setBlockType(
                                                masterBlock.toInt(),
                                                BlockTypes.AIR,
                                                false,
                                                Cause.source(SkreePlugin.container()).build()
                                        );

                                        player.sendMessage(
                                                Text.of(
                                                        TextColors.YELLOW,
                                                        "Master block moved."
                                                )
                                        );
                                    } else {
                                        recentlyClicked.put(player, blockPos);
                                        block.setValid(false);
                                        player.sendMessage(
                                                Text.of(
                                                        TextColors.YELLOW,
                                                        "Place the master block again to move this regions master block."
                                                )
                                        );
                                    }
                                }

                                service.setSelectedRegion(player, ref);

                                if (!ref.equals(optOriginRef.orElse(null))) {
                                    player.sendMessage(Text.of(TextColors.YELLOW, "Active region set!"));
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            for (Transaction<BlockSnapshot> block : event.getTransactions()) {
                if (!block.isValid()) {
                    continue;
                }

                if (block.getOriginal().getState().getType() == this) {
                    Optional<RegionService> optService = Sponge.getServiceManager().provide(RegionService.class);
                    if (optService.isPresent()) {
                        RegionService service = optService.get();
                        Optional<Location<World>> optLoc = block.getOriginal().getLocation();
                        if (optLoc.isPresent()) {
                            Optional<Region> optRef = service.getMarkedRegion(optLoc.get());
                            if (optRef.isPresent()) {
                                Region ref = optRef.get();
                                if (!ref.getFullPoints().isEmpty()) {
                                    block.setValid(false);
                                    player.sendMessage(Text.of(TextColors.RED, "You must first delete all markers!"));
                                } else {
                                    service.rem(optLoc.get());
                                    player.sendMessage(Text.of(TextColors.YELLOW, "Region deleted!"));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}