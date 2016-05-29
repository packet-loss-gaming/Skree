/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.admin;

import com.skelril.nitro.inventory.PlayerInventoryReader;
import com.skelril.skree.SkreePlugin;
import net.minecraft.init.Blocks;
import net.minecraft.world.ILockableContainer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static org.spongepowered.api.command.args.GenericArguments.choices;
import static org.spongepowered.api.command.args.GenericArguments.user;

/**
 * Dear reader, this file may make you cry.
 */
public class InventoryEditCommand implements CommandExecutor {

    private Map<UUID, DataDump> dataDumpMap = new HashMap<>();
    private List<UUID> activelyEdited = new ArrayList<>();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "You must be a player to use this command!"));
            return CommandResult.empty();
        }

        User user = args.<User>getOne("user").get();

        if (Sponge.getServer().getPlayer(user.getUniqueId()).isPresent()) {
            src.sendMessage(Text.of(TextColors.RED, "The player MUST be offline."));
            return CommandResult.empty();
        }

        if (activelyEdited.contains(user.getUniqueId())) {
            src.sendMessage(Text.of(TextColors.RED, "Player already being edited."));
            return CommandResult.empty();
        }

        activelyEdited.add(user.getUniqueId());

        DataDump.Type type = args.<DataDump.Type>getOne("type").get();
        PlayerInventoryReader.Data data = PlayerInventoryReader.getPlayerData(user.getUniqueId());
        dataDumpMap.put(((Player) src).getUniqueId(), new DataDump(user.getUniqueId(), data, type));

        src.sendMessage(Text.of(TextColors.YELLOW, user.getName(), " has been prevented from logging in while this operation is in progress."));
        src.sendMessage(Text.of(TextColors.GREEN, "Punch a large chest to dump the inventory contents."));

        return CommandResult.success();
    }

    @Listener
    public void onConnect(ClientConnectionEvent.Auth event) {
        if (activelyEdited.contains(event.getProfile().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    private void testOrFail(Player admin, UUID targetUser, boolean condition) throws IllegalStateException {
        if (!condition) {
            dataDumpMap.remove(admin.getUniqueId());
            activelyEdited.remove(targetUser);

            admin.sendMessage(Text.of(TextColors.RED, "Invalid storage dump option, aborting!"));
            throw new IllegalStateException();
        }
    }

    @Listener
    public void onPlayerInteract(InteractBlockEvent.Primary event, @Root Player player) {
        DataDump dataDump = dataDumpMap.get(player.getUniqueId());
        if (dataDump == null) {
            return;
        }

        BlockSnapshot blockSnapshot = event.getTargetBlock();
        Optional<Location<World>> optLoc = blockSnapshot.getLocation();
        if (!optLoc.isPresent()) {
            return;
        }

        Location<World> blockLoc = optLoc.get();
        ILockableContainer container = Blocks.chest.getLockableContainer(
                tf(blockLoc.getExtent()), tf(blockLoc.getBlockPosition())
        );

        if (container == null) {
            return;
        }

        boolean complete = false;

        try {
            switch (dataDump.type) {
                case PLAYER:
                    if (dataDump.dumped) {
                        testOrFail(player, dataDump.targetUser, dataDump.readPInvFromContainer(container));
                        complete = true;
                    } else {
                        testOrFail(player, dataDump.targetUser, dataDump.writePInvToContainer(container));
                        dataDump.dumped = true;
                    }
                    break;
                case ENDER:
                    if (dataDump.dumped) {
                        testOrFail(player, dataDump.targetUser, dataDump.readEInvFromContainer(container));
                        complete = true;
                    } else {
                        testOrFail(player, dataDump.targetUser, dataDump.writeEInvToContainer(container));
                        dataDump.dumped = true;
                    }
                    break;
            }
        } catch (IllegalStateException ex) {
            return;
        }

        if (complete) {
            PlayerInventoryReader.writePlayerData(dataDump.targetUser, dataDump.inventoryData);
            dataDumpMap.remove(player.getUniqueId());
            activelyEdited.remove(dataDump.targetUser);

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "Inventory changes written."));
        } else if (dataDump.dumped) {
            player.sendMessage(Text.of(TextColors.GREEN, "Punch the chest when you've completed your changes."));
        }
    }

    private static class DataDump {
        final UUID targetUser;
        final PlayerInventoryReader.Data inventoryData;
        final Type type;

        boolean dumped = false;

        public DataDump(UUID targetUser, PlayerInventoryReader.Data inventoryData, Type type) {
            this.targetUser = targetUser;
            this.inventoryData = inventoryData;
            this.type = type;
        }

        public boolean writePInvToContainer(ILockableContainer container) {
            int totalSize = inventoryData.mainInventory.length + inventoryData.armorInventory.length;
            if (totalSize > container.getSizeInventory()) {
                return false;
            }

            for (int i = 0; i < container.getSizeInventory(); ++i) {
                container.setInventorySlotContents(i, null);
            }

            for (int i = 0; i < inventoryData.mainInventory.length; ++i) {
                container.setInventorySlotContents(i, tf(inventoryData.mainInventory[i]));
            }

            for (int i = 0; i < inventoryData.armorInventory.length; ++i) {
                container.setInventorySlotContents(i + inventoryData.mainInventory.length, tf(inventoryData.armorInventory[i]));
            }

            return true;
        }

        public boolean readPInvFromContainer(ILockableContainer container) {
            int totalSize = inventoryData.mainInventory.length + inventoryData.armorInventory.length;
            if (totalSize > container.getSizeInventory()) {
                return false;
            }

            for (int i = 0; i < inventoryData.mainInventory.length; ++i) {
                inventoryData.mainInventory[i] = tf(container.getStackInSlot(i));
            }

            for (int i = 0; i < inventoryData.armorInventory.length; ++i) {
                inventoryData.armorInventory[i] = tf(container.getStackInSlot(i + inventoryData.mainInventory.length));
            }

            for (int i = 0; i < container.getSizeInventory(); ++i) {
                container.setInventorySlotContents(i, null);
            }

            return true;
        }

        public boolean writeEInvToContainer(ILockableContainer container) {
            int totalSize = inventoryData.enderInventory.length;
            if (totalSize > container.getSizeInventory()) {
                return false;
            }

            for (int i = 0; i < container.getSizeInventory(); ++i) {
                container.setInventorySlotContents(i, null);
            }

            for (int i = 0; i < inventoryData.enderInventory.length; ++i) {
                container.setInventorySlotContents(i, tf(inventoryData.enderInventory[i]));
            }

            return true;
        }

        public boolean readEInvFromContainer(ILockableContainer container) {
            int totalSize = inventoryData.enderInventory.length;
            if (totalSize > container.getSizeInventory()) {
                return false;
            }

            for (int i = 0; i < inventoryData.enderInventory.length; ++i) {
                inventoryData.enderInventory[i] = tf(container.getStackInSlot(i));
            }

            for (int i = 0; i < container.getSizeInventory(); ++i) {
                container.setInventorySlotContents(i, null);
            }

            return true;
        }


        private enum Type {
            PLAYER,
            ENDER
        }
    }

    public static CommandSpec aquireSpec() {
        Map<String, DataDump.Type> inventoryType = new HashMap<>();
        inventoryType.put("player", DataDump.Type.PLAYER);
        inventoryType.put("ender", DataDump.Type.ENDER);

        InventoryEditCommand commandObject = new InventoryEditCommand();
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), commandObject);

        return CommandSpec.builder()
                .description(Text.of("Edit a players inventory"))
                .permission("skree.inventoryedit")
                .arguments(user(Text.of("user")), choices(Text.of("type"), inventoryType))
                .executor(commandObject).build();
    }
}
