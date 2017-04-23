/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.playerstate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.PlayerStateService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static org.spongepowered.api.data.persistence.DataTranslators.CONFIGURATION_NODE;

public class PlayerStateServiceImpl implements PlayerStateService {

    private static final String GENERAL_STORE_NAME = "general_store";

    private Path getFile(Player player) throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        path = Files.createDirectories(path.resolve("profiles"));
        return path.resolve(player.getUniqueId() + ".json");
    }

    private static Optional<JsonElement> serializeItemStack(ItemStack item) {
        try (StringWriter sink = new StringWriter()) {
            try (BufferedWriter writer = new BufferedWriter(sink)) {
                GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSink(() -> writer).build();
                ConfigurationNode node = CONFIGURATION_NODE.translate(item.toContainer());
                loader.save(node);
                return Optional.of(new JsonParser().parse(sink.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static Optional<ItemStack> deserializeItemStack(JsonElement element) {
        try (StringReader source = new StringReader(element.toString())) {
            try (BufferedReader reader = new BufferedReader(source)) {
                GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSource(() -> reader).build();
                ConfigurationNode node = loader.load();
                return Optional.of(ItemStack.builder().fromContainer(CONFIGURATION_NODE.translate(node)).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<SavedPlayerStateContainer> getContainer(Player player) throws IOException {
        if (!getFile(player).toFile().exists()) {
            return Optional.empty();
        }

        try (BufferedReader reader = Files.newBufferedReader(getFile(player))) {
            Gson gson = new GsonBuilder().create();
            return Optional.of(gson.fromJson(reader, SavedPlayerStateContainer.class));
        }
    }

    private void writeContainer(Player player, SavedPlayerStateContainer container) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(getFile(player))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(container));
        }
    }

    @Override
    public boolean hasInventoryStored(Player player) {
        try {
            SavedPlayerStateContainer stateContainer = getContainer(player).orElse(new SavedPlayerStateContainer());
            Map<String, SavedPlayerState> states = stateContainer.getSavedPlayerStates();

            return states.get(GENERAL_STORE_NAME) != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasReleasedInventoryStored(Player player) {
        try {
            SavedPlayerStateContainer container = getContainer(player).orElse(new SavedPlayerStateContainer());
            SavedPlayerState playerState = container.getSavedPlayerStates().get(container.getReleasedState());
            return playerState != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void storeInventory(Player player) throws InventoryStorageStateException {
        if (hasInventoryStored(player)) {
            throw new InventoryStorageStateException();
        }

        save(player, GENERAL_STORE_NAME);
    }

    @Override
    public void loadInventory(Player player) throws InventoryStorageStateException {
        if (!hasInventoryStored(player)) {
            throw new InventoryStorageStateException();
        }

        load(player, GENERAL_STORE_NAME);
        destroySave(player, GENERAL_STORE_NAME);
    }

    @Override
    public void releaseInventory(Player player) throws InventoryStorageStateException {
        if (!hasInventoryStored(player)) {
            throw new InventoryStorageStateException();
        }

        try {
            SavedPlayerStateContainer container = getContainer(player).orElse(new SavedPlayerStateContainer());
            container.setReleasedState(GENERAL_STORE_NAME);

            writeContainer(player, container);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private SavedPlayerState createNewPlayerState(Player player) {
        SavedPlayerState playerState = new SavedPlayerState();

        player.getInventory().slots().forEach(slot -> {
            ItemStack stackInSlot = slot.peek().orElse(newItemStack(ItemTypes.NONE));
            JsonElement serializedStack = serializeItemStack(stackInSlot).get();
            playerState.getInventoryContents().add(serializedStack);
        });

        return playerState;
    }

    @Override
    public void save(Player player, String saveName) {
        try {
            SavedPlayerStateContainer stateContainer = getContainer(player).orElse(new SavedPlayerStateContainer());
            Map<String, SavedPlayerState> states = stateContainer.getSavedPlayerStates();
            states.put(saveName, createNewPlayerState(player));

            writeContainer(player, stateContainer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<JsonElement> getInventoryContents(Player player, String saveName) {
        try {
            SavedPlayerStateContainer stateContainer = getContainer(player).orElse(new SavedPlayerStateContainer());
            SavedPlayerState state = stateContainer.getSavedPlayerStates().getOrDefault(saveName, new SavedPlayerState());
            return state.getInventoryContents();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void load(Player player, String saveName) {
        Iterator<Inventory> slots = player.getInventory().slots().iterator();
        List<JsonElement> persistedInventoryContents = getInventoryContents(player, saveName);
        for (int i = 0; slots.hasNext(); ++i) {
            if (i < persistedInventoryContents.size()) {
                ItemStack stack = deserializeItemStack(persistedInventoryContents.get(i)).orElse(newItemStack(ItemTypes.NONE));
                slots.next().set(stack);
            } else {
                slots.next().set(newItemStack(ItemTypes.NONE));
            }
        }
    }

    private void destroySave(Player player, String saveName) {
        try {
            SavedPlayerStateContainer container = getContainer(player).orElse(new SavedPlayerStateContainer());
            container.getSavedPlayerStates().remove(saveName);
            if (Objects.equals(saveName, GENERAL_STORE_NAME)) {
                container.setReleasedState(null);
            }

            writeContainer(player, container);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener(order = Order.LAST)
    public void onPlayerRespawn(RespawnPlayerEvent event) {
        Player player = event.getTargetEntity();
        if (hasReleasedInventoryStored(player)) {
            try {
                loadInventory(player);
            } catch (InventoryStorageStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Listener(order = Order.LAST)
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (hasReleasedInventoryStored(player)) {
            try {
                loadInventory(player);
            } catch (InventoryStorageStateException e) {
                e.printStackTrace();
            }
        }
    }
}
