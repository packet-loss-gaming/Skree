/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.skywars;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.nearce.gamechatter.sponge.GameChatterPlugin;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.skelril.nitro.Clause;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.registry.item.minigame.SkyFeather;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.service.PlayerStateService;
import com.skelril.skree.service.internal.playerstate.InventoryStorageStateException;
import com.skelril.skree.service.internal.zone.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.animal.Chicken;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;

public class SkyWarsInstance extends LegacyZoneBase implements Zone, Runnable {

    private static Map<Color, String> colorNameMapping = new LinkedHashMap<>();

    static {
        colorNameMapping.put(Color.WHITE, "white");
        colorNameMapping.put(Color.BLUE, "blue");
        colorNameMapping.put(Color.RED, "red");
        colorNameMapping.put(Color.GREEN, "green");
        colorNameMapping.put(Color.GRAY, "gray");
        colorNameMapping.put(Color.YELLOW, "yellow");
        colorNameMapping.put(Color.BLACK, "black");
    }

    private Location<World> startingLocation;

    private SkyWarsState state = SkyWarsState.LOBBY;

    private Map<Player, SkyWarsPlayerData> playerDataMapping = new HashMap<>();
    private Map<Color, Set<Player>> teams = new HashMap<>();

    public SkyWarsInstance(ZoneRegion region) {
        super(region);
    }

    @Override
    public boolean init() {
        setup();
        return true;
    }

    private Map<Color, Set<Player>> createTeamsMapping() {
        Map<Color, Set<Player>> teams = new HashMap<>();
        for (Color color : colorNameMapping.keySet()) {
            teams.put(color, new HashSet<>());
        }
        return teams;
    }

    private void setup() {
        teams = createTeamsMapping();

        Vector3d centerPoint = getRegion().getCenter();
        startingLocation = new Location<>(getRegion().getExtent(), centerPoint.getX(), 14, centerPoint.getZ());

        showStartingPlatform(true);
    }

    @Override
    public void forceEnd() {

    }

    private void giveTeamHoods(Player player) {
        for (Color teamColor : colorNameMapping.keySet()) {
            ItemStack teamHood = newItemStack(ItemTypes.LEATHER_HELMET);
            teamHood.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Sky Hood"));
            teamHood.offer(Keys.COLOR, teamColor);
            player.getInventory().offer(teamHood);
        }
    }

    private void giveTeamEquipment(Player player, Color teamColor) {
        ItemStack teamHood = newItemStack(ItemTypes.LEATHER_HELMET);
        teamHood.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Sky Hood"));
        teamHood.offer(Keys.COLOR, teamColor);
        player.setHelmet(teamHood);

        ItemStack teamChestplate = newItemStack(ItemTypes.LEATHER_CHESTPLATE);
        teamChestplate.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Sky Chestplate"));
        teamChestplate.offer(Keys.COLOR, teamColor);
        player.setChestplate(teamChestplate);

        ItemStack teamLeggings = newItemStack(ItemTypes.LEATHER_LEGGINGS);
        teamLeggings.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Sky Leggings"));
        teamLeggings.offer(Keys.COLOR, teamColor);
        player.setLeggings(teamLeggings);

        ItemStack teamBoots = newItemStack(ItemTypes.LEATHER_BOOTS);
        teamBoots.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Sky Boots"));
        teamBoots.offer(Keys.COLOR, teamColor);
        player.setBoots(teamBoots);
    }

    private void showStartingPlatform(boolean present) {
        Location<World> platformLocation = startingLocation.add(0, -1, 0);

        EditSession editor = WorldEdit.getInstance().getEditSessionFactory().getEditSession(
                new WorldResolver(getRegion().getExtent()).getWorldEditWorld(),
                -1
        );
        com.sk89q.worldedit.Vector origin = new com.sk89q.worldedit.Vector(
                platformLocation.getX(), platformLocation.getY(), platformLocation.getZ()
        );

        BaseBlock targetBlock;

        if (present) {
            targetBlock = WorldEdit.getInstance().getBaseBlockFactory().getBaseBlock(BlockID.STAINED_GLASS, 15);
        } else {
            targetBlock = WorldEdit.getInstance().getBaseBlockFactory().getBaseBlock(BlockID.AIR);
        }

        try {
            editor.makeCylinder(origin, new SingleBlockPattern(targetBlock), 12, 1, true);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        if (state != SkyWarsState.LOBBY) {
            return new Clause<>(player, ZoneStatus.NO_REJOIN);
        }

        player.setLocation(startingLocation);
        Optional<PlayerStateService> optService = Sponge.getServiceManager().provide(PlayerStateService.class);
        if (optService.isPresent()) {
            PlayerStateService service = optService.get();
            try {
                service.storeInventory(player);
                service.releaseInventory(player);

                giveTeamHoods(player);
            } catch (InventoryStorageStateException e) {
                e.printStackTrace();
                return new Clause<>(player, ZoneStatus.ERROR);
            }
        }

        playerDataMapping.put(player, new SkyWarsPlayerData());

        return new Clause<>(player, ZoneStatus.ADDED);
    }

    @Override
    public Clause<Player, ZoneStatus> remove(Player player) {
        playerLost(player);

        Optional<PlayerStateService> optService = Sponge.getServiceManager().provide(PlayerStateService.class);
        if (optService.isPresent()) {
            PlayerStateService service = optService.get();
            if (service.hasInventoryStored(player)) {
                try {
                    service.loadInventory(player);
                } catch (InventoryStorageStateException e) {
                    e.printStackTrace();
                }
            }
        }

        return super.remove(player);
    }

    public void playerLost(Player player) {
        SkyWarsPlayerData playerData = playerDataMapping.remove(player);
        if (playerData != null) {
            Set<Player> team = playerData.getTeam();
            if (team != null) {
                team.remove(player);
            }

            player.getInventory().clear();
        }
    }

    @Override
    public Collection<Player> getPlayers(PlayerClassifier classifier) {
        if (classifier == PARTICIPANT) {
            return playerDataMapping.keySet();
        }
        return super.getPlayers(classifier);
    }

    @Override
    public void run() {
        if (isEmpty() && state != SkyWarsState.IN_PROGRESS) {
            expire();
            return;
        }

        if (state == SkyWarsState.LOBBY) {
            smartStart();
            return;
        }

        outOfBoundsCheck();

        Optional<Clause<String, WinType>> optWinner = getWinner();
        if (optWinner.isPresent()) {
            processWin(optWinner.get());
            expire();
            return;
        }

        feedPlayers();
        damagePlayers();
        spawnChickens();
    }

    public SkyWarsState getState() {
        return state;
    }

    public void awardPowerup(Player player, ItemStack held) {

        ItemStack powerup;

        Optional<String> optSuffix = SkyFeather.getSuffix(held);
        if (optSuffix.isPresent() && optSuffix.get().equals("Doom")) return;

        int uses = 5;
        double radius = 3;
        double flight = 2;
        double pushBack = 4;

        if (Probability.getChance(2)) {
            radius = 5;
            pushBack = 6;
        } else {
            flight = 6;
        }

        if (Probability.getChance(50)) {
            uses = -1;
            radius = 7;
            flight = 6;
            pushBack = 6;
            MutableMessageChannel targets = getPlayerMessageChannel(PlayerClassifier.SPECTATOR).asMutable();
            targets.removeMember(player);
            targets.send(Text.of(TextColors.RED, player.getName() + " has been given a Doom feather!"));

            player.getInventory().clear();
        }

        powerup = newItemStack(CustomItemTypes.SKY_FEATHER);
        SkyFeather.setFeatherProperties(powerup, uses, radius, flight, pushBack);

        player.getInventory().offer(powerup);

        // Display name doesn't need checked as all power ups have one assigned
        player.sendMessage(Text.of(TextColors.YELLOW, "You obtain a power-up!"));
    }

    public boolean isFriendlyFire(Player attacker, Player defender) {
        SkyWarsPlayerData attackerData = playerDataMapping.get(attacker);
        SkyWarsPlayerData defenderData = playerDataMapping.get(defender);

        if (attackerData == null || defenderData == null) {
            return false;
        }

        Set<Player> attackerTeam = attackerData.getTeam();
        Set<Player> defenderTeam = defenderData.getTeam();

        /* We want identity comparison to prevent expensive list comparisons */
        return attackerTeam == defenderTeam && attackerTeam != teams.get(Color.WHITE) && attackerTeam != null;
    }

    public Optional<SkyWarsPlayerData> getPlayerData(Player player) {
        return Optional.ofNullable(playerDataMapping.get(player));
    }

    private void launchPlayer(Player player, double mod) {
        player.setVelocity(new Vector3d(0, 3.5, 0).mul(mod));
    }

    private void smartStart() {
        HashMap<Player, Color> colorMapping = new HashMap<>();
        for (Player player : getPlayers(PlayerClassifier.PARTICIPANT)) {
            Optional<ItemStack> optHelmet = player.getHelmet();
            if (!optHelmet.isPresent()) {
                return;
            }

            ItemStack helmet = optHelmet.get();
            Optional<Color> optColor = helmet.get(Keys.COLOR);
            if (!optColor.isPresent()) {
                return;
            }

            colorMapping.put(player, optColor.get());
        }

        teams = createTeamsMapping(); // Reset the team mapping as it may have been corrupted by a bad run

        for (Map.Entry<Player, Color> entry : colorMapping.entrySet()) {
            Color color = entry.getValue();
            Set<Player> team = teams.get(color);
            if (team == null) {
                return;
            }

            Player player = entry.getKey();

            // Update team mapping
            team.add(player);
            SkyWarsPlayerData playerData = playerDataMapping.get(player);
            playerData.setTeam(team);

            // Give player team equipment
            giveTeamEquipment(player, color);

            // Throw player
            launchPlayer(player, 1);
            playerData.stopPushBack();
        }

        if (getWinner().isPresent()) {
            return;
        }

        getPlayerMessageChannel(PlayerClassifier.SPECTATOR).send(Text.of(TextColors.RED, "Fight!"));
        state = SkyWarsState.IN_PROGRESS;

        showStartingPlatform(false);
    }

    private void outOfBoundsCheck() {
        for (Player player : getPlayers(PARTICIPANT)) {
            if (contains(player)) {
                continue;
            }

            remove(player);
        }
    }

    public Optional<Clause<String, WinType>> getWinner() {
        return getWinner(teams);
    }

    private Optional<Clause<String, WinType>> getWinner(Map<Color, Set<Player>> teams) {
        List<Clause<String, WinType>> winners = new ArrayList<>();
        for (Map.Entry<Color, Set<Player>> entry : teams.entrySet()) {
            String colorName = colorNameMapping.get(entry.getKey());
            if (colorName == null) {
                entry.getValue().stream().forEach(p -> winners.add(new Clause<>(p.getName(), WinType.SOLO)));
            } else {
                winners.add(new Clause<>(colorName, WinType.TEAM));
            }
        }

        if (winners.isEmpty()) {
            return Optional.of(new Clause<>(null, WinType.DRAW));
        }

        return winners.size() == 1 ? Optional.of(winners.get(0)) : Optional.empty();
    }

    private void processWin(Clause<String, WinType> winClause) {
        state = SkyWarsState.DONE;

        String rawWinMessage;
        switch (winClause.getValue()) {
            case SOLO:
                rawWinMessage = winClause.getKey() + " is the sky wars victor!";
                break;
            case TEAM:
                rawWinMessage = winClause.getKey() + " team is the sky wars victor!";
                break;
            case DRAW:
                rawWinMessage = "Sky wars ended with a draw!";
                break;
            default:
                return;
        }

        MessageChannel.TO_ALL.send(Text.of(TextColors.GOLD, rawWinMessage));
        GameChatterPlugin.inst().sendSystemMessage(rawWinMessage);
    }

    private void feedPlayers() {
        for (Player player : getPlayers(PARTICIPANT)) {
            player.offer(Keys.FOOD_LEVEL, 20);
            player.offer(Keys.SATURATION, 5D);
        }
    }

    private void damagePlayers() {
        for (Player player : getPlayers(PARTICIPANT)) {
            BlockType blockType = player.getLocation().getBlockType();
            if (blockType == BlockTypes.WATER || blockType == BlockTypes.FLOWING_WATER) {
                // If the game is in progress, damage, otherwise return to the spawn point
                if (state == SkyWarsState.IN_PROGRESS) {
                    player.damage(Probability.getRandom(3), DamageSource.builder().type(DamageTypes.DROWN).build());
                } else {
                    player.setLocation(startingLocation);
                }
            }
        }
    }

    private void spawnChickens() {
        Vector3i bvMax = getRegion().getMaximumPoint();
        Vector3i bvMin = getRegion().getMinimumPoint();

        for (int i = 0; i < getPlayers(PARTICIPANT).size(); ++i) {
            Location<World> testLoc = new Location<>(
                    getRegion().getExtent(),
                    Probability.getRangedRandom(bvMin.getX(), bvMax.getX()),
                    bvMax.getY() - 10,
                    Probability.getRangedRandom(bvMin.getZ(), bvMax.getZ())
            );

            Vector2d testPos = new Vector2d(testLoc.getX(), testLoc.getZ());
            Vector2d originPos = new Vector2d(startingLocation.getX(), startingLocation.getZ());

            if (testPos.distanceSquared(originPos) >= 70 * 70) {
                --i;
                continue;
            }

            Optional<Entity> optEntity = testLoc.getExtent().createEntity(EntityTypes.CHICKEN, testLoc.getPosition());
            if (optEntity.isPresent()) {
                Chicken chicken = (Chicken) optEntity.get();
                chicken.offer(Keys.PERSISTS, false);
                testLoc.getExtent().spawnEntity(
                        chicken,
                        Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build()
                );
            }
        }
    }
}
