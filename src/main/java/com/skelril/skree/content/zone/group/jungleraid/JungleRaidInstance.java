/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.jungleraid;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.entity.SafeTeleportHelper;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.service.internal.zone.Zone;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.trait.EnumTraits;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.SPECTATOR;

public class JungleRaidInstance extends LegacyZoneBase implements Zone, Runnable {

    private Map<Player, Set<Player>> teamMapping = new HashMap<>();
    private Set<Player> freeForAllPlayers = new HashSet<>();
    private Set<Player> blueTeamPlayers = new HashSet<>();
    private Set<Player> redTeamPlayers = new HashSet<>();
    private Map<Player, JungleRaidClass> classMap = new HashMap<>();

    private JungleRaidState state = JungleRaidState.LOBBY;
    private long startTime;

    private Location<World> lobbySpawnLocation;
    private Location<World> leftFlagActivationSign;
    private Location<World> rightFlagActivationSign;
    private List<Location<World>> scrollingFlagSigns = new ArrayList<>();

    private Location<World> leftClassActivationSign;
    private Location<World> rightClassActivationSign;
    private List<Location<World>> scrollingClassSigns = new ArrayList<>();

    private int signScrollFlagStart;
    private int signScrollClassStart;

    private FlagEffectData flagData = new FlagEffectData();
    private boolean[] flagState = new boolean[JungleRaidFlag.values().length];

    public JungleRaidInstance(ZoneRegion region) {
        super(region);
    }

    @Override
    public boolean init() {
        setUp();
        remove();
        return true;
    }

    private void setUp() {
        Vector3i offset = getRegion().getMinimumPoint();

        lobbySpawnLocation = new Location<>(getRegion().getExtent(), offset.add(216, 2, 29));
        leftFlagActivationSign = new Location<>(getRegion().getExtent(), offset.add(209, 3, 29));
        rightFlagActivationSign = new Location<>(getRegion().getExtent(), offset.add(209, 3, 23));

        for (int z = 28; z > 23; --z) { // Do this in rerverse so left/right buttons are correct
            scrollingFlagSigns.add(new Location<>(getRegion().getExtent(), offset.add(209, 3, z)));
        }

        for (JungleRaidFlag flag : JungleRaidFlag.values()) {
            flagState[flag.index] = flag.enabledByDefault;
        }

        flagSignPopulate();

        leftClassActivationSign = new Location<>(getRegion().getExtent(), offset.add(209, 3, 22));
        rightClassActivationSign = new Location<>(getRegion().getExtent(), offset.add(209, 3, 18));

        for (int z = 21; z > 18; --z) { // Do this in rerverse so left/right buttons are correct
            scrollingClassSigns.add(new Location<>(getRegion().getExtent(), offset.add(209, 3, z)));
        }

        classSignPopulate();
    }

    private void updateFlagSign(int index) {
        String title = JungleRaidFlag.values()[signScrollFlagStart + index].toString();
        if (title.length() > 15) {
            title = title.substring(0, 15);
        }
        title = WordUtils.capitalizeFully(title.replace("_", " "));

        scrollingFlagSigns.get(index).getTileEntity().get().offer(Keys.SIGN_LINES, Lists.newArrayList(
                Text.EMPTY,
                Text.of(title),
                Text.of(flagState[signScrollFlagStart + index] ? Text.of(TextColors.DARK_GREEN, "Enabled") : Text.of(TextColors.RED, "Disabled")),
                Text.EMPTY
        ));
    }

    private void flagSignPopulate() {
        for (int i = 0; i < scrollingFlagSigns.size(); ++i) {
            updateFlagSign(i);
        }

        boolean isLeftScrollable = signScrollFlagStart == 0;
        leftFlagActivationSign.getTileEntity().get().offer(Keys.SIGN_LINES, Lists.newArrayList(
                Text.EMPTY,
                Text.of(isLeftScrollable ? "" : TextColors.BLUE, "<<"),
                Text.EMPTY,
                Text.EMPTY
        ));
        boolean isRightScrollable = signScrollFlagStart + scrollingFlagSigns.size() == JungleRaidFlag.values().length;
        rightFlagActivationSign.getTileEntity().get().offer(Keys.SIGN_LINES, Lists.newArrayList(
                Text.EMPTY,
                Text.of(isRightScrollable ? "" : TextColors.BLUE, ">>"),
                Text.EMPTY,
                Text.EMPTY
        ));
    }

    public Location<World> getLeftFlagActivationSign() {
        return leftFlagActivationSign;
    }

    public Location<World> getRightFlagActivationSign() {
        return rightFlagActivationSign;
    }

    public void leftFlagListSign() {
        signScrollFlagStart = Math.max(0, signScrollFlagStart - scrollingFlagSigns.size());
        flagSignPopulate();
    }

    public void rightFlagListSign() {
        signScrollFlagStart = Math.min(JungleRaidFlag.values().length - scrollingFlagSigns.size(), signScrollFlagStart + scrollingFlagSigns.size());
        flagSignPopulate();
    }

    public void tryToggleFlagSignAt(Location<World> loc) {
        for (int i = 0; i < scrollingFlagSigns.size(); ++i) {
            if (loc.equals(scrollingFlagSigns.get(i))) {
                flagState[signScrollFlagStart + i] = !flagState[signScrollFlagStart + i];
                updateFlagSign(i);
                break;
            }
        }
    }

    private void updateClassSign(int index) {
        String title = JungleRaidClass.values()[signScrollClassStart + index].toString();
        if (title.length() > 15) {
            title = title.substring(0, 15);
        }
        title = WordUtils.capitalizeFully(title.replace("_", " "));

        scrollingClassSigns.get(index).getTileEntity().get().offer(Keys.SIGN_LINES, Lists.newArrayList(
                Text.EMPTY,
                Text.of(title),
                Text.EMPTY,
                Text.EMPTY
        ));
    }

    private void classSignPopulate() {
        for (int i = 0; i < scrollingClassSigns.size(); ++i) {
            updateClassSign(i);
        }

        boolean isLeftScrollable = signScrollClassStart == 0;
        leftClassActivationSign.getTileEntity().get().offer(Keys.SIGN_LINES, Lists.newArrayList(
                Text.EMPTY,
                Text.of(isLeftScrollable ? "" : TextColors.BLUE, "<<"),
                Text.EMPTY,
                Text.EMPTY
        ));
        boolean isRightScrollable = signScrollClassStart + scrollingClassSigns.size() == JungleRaidClass.values().length;
        rightClassActivationSign.getTileEntity().get().offer(Keys.SIGN_LINES, Lists.newArrayList(
                Text.EMPTY,
                Text.of(isRightScrollable ? "" : TextColors.BLUE, ">>"),
                Text.EMPTY,
                Text.EMPTY
        ));
    }


    public Location<World> getLeftClassActivationSign() {
        return leftClassActivationSign;
    }

    public Location<World> getRightClassActivationSign() {
        return rightClassActivationSign;
    }

    public void leftClassListSign() {
        signScrollClassStart = Math.max(0, signScrollClassStart - scrollingClassSigns.size());
        classSignPopulate();
    }

    public void rightClassListSign() {
        signScrollClassStart = Math.min(JungleRaidClass.values().length - scrollingClassSigns.size(), signScrollClassStart + scrollingClassSigns.size());
        classSignPopulate();
    }

    public void tryUseClassSignAt(Location<World> loc, Player player) {
        for (int i = 0; i < scrollingClassSigns.size(); ++i) {
            if (loc.equals(scrollingClassSigns.get(i))) {
                JungleRaidClass targetClass = JungleRaidClass.values()[signScrollClassStart + i];
                giveBaseEquipment(player, targetClass);
                classMap.put(player, targetClass);
                break;
            }
        }
    }

    public void setFlag(JungleRaidFlag flag, boolean enabled) {
        flagState[flag.index] = enabled;
    }

    public boolean isFlagEnabled(JungleRaidFlag flag) {
        return flagState[flag.index];
    }

    @Override
    public void forceEnd() {
        remove(getPlayers(PARTICIPANT));
        remove();
    }

    @Override
    public void run() {
        if (isEmpty()) {
            expire();
            return;
        }

        if (state == JungleRaidState.LOBBY) {
            smartStart();
            return;
        }

        if (state == JungleRaidState.INITIALIZE) {
            tryBeginCombat();
            return;
        }

        Optional<Clause<String, WinType>> optWinner = getWinner();
        if (optWinner.isPresent()) {
            processWin(optWinner.get());
            expire();
            return;
        }
        JungleRaidEffectProcessor.run(this);
    }

    public JungleRaidState getState() {
        return state;
    }

    public long getStartTime() {
        return startTime;
    }

    public FlagEffectData getFlagData() {
        return flagData;
    }

    private void tryBeginCombat() {
        if (System.currentTimeMillis() - startTime >= TimeUnit.MINUTES.toMillis(1)) {
            state = JungleRaidState.IN_PROGRESS;
            getPlayerMessageChannel(SPECTATOR).send(Text.of(TextColors.DARK_RED, "LET THE SLAUGHTER BEGIN!"));
        }
    }

    public Optional<Clause<String, WinType>> getWinner() {
        if (freeForAllPlayers.size() == 1 && blueTeamPlayers.isEmpty() && redTeamPlayers.isEmpty()) {
            return Optional.of(new Clause<>(freeForAllPlayers.iterator().next().getName(), WinType.SOLO));
        } else if (freeForAllPlayers.isEmpty() && !blueTeamPlayers.isEmpty() && redTeamPlayers.isEmpty()) {
            return Optional.of(new Clause<>("Blue", WinType.TEAM));
        } else if (freeForAllPlayers.isEmpty() && blueTeamPlayers.isEmpty() && !redTeamPlayers.isEmpty()) {
            return Optional.of(new Clause<>("Red", WinType.TEAM));
        } else if (freeForAllPlayers.isEmpty() && blueTeamPlayers.isEmpty() && redTeamPlayers.isEmpty()) {
            return Optional.of(new Clause<>(null, WinType.DRAW));
        }

        return Optional.empty();
    }

    private void processWin(Clause<String, WinType> winClause) {
        state = JungleRaidState.DONE;

        switch (winClause.getValue()) {
            case SOLO:
                MessageChannel.TO_ALL.send(Text.of(TextColors.GOLD, winClause.getKey(), " has won the jungle raid!"));
                break;
            case TEAM:
                MessageChannel.TO_ALL.send(Text.of(TextColors.GOLD, winClause.getKey(), " team has won the jungle raid!"));
                break;
            case DRAW:
                MessageChannel.TO_ALL.send(Text.of(TextColors.GOLD, "The jungle raid was a draw!"));
                break;
        }
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        if (state == JungleRaidState.LOBBY) {
            player.setLocation(lobbySpawnLocation);
            return new Clause<>(player, ZoneStatus.ADDED);
        }
        return new Clause<>(player, ZoneStatus.NO_REJOIN);
    }

    private void giveBaseEquipment(Player player, JungleRaidClass jrClass) {
        player.getInventory().clear();

        List<ItemStack> gear = new ArrayList<>();
        switch (jrClass) {
            case MELEE:
                ItemStack enchantedSword = newItemStack(ItemTypes.DIAMOND_SWORD);
                enchantedSword.offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(
                        new ItemEnchantment(Enchantments.FIRE_ASPECT, 2),
                        new ItemEnchantment(Enchantments.KNOCKBACK, 2)
                ));

                gear.add(enchantedSword);
                break;
            case ARCHER:
                ItemStack dmgBow = newItemStack(ItemTypes.BOW);
                dmgBow.offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(
                        new ItemEnchantment(Enchantments.FIRE_ASPECT, 2),
                        new ItemEnchantment(Enchantments.KNOCKBACK, 2),
                        new ItemEnchantment(Enchantments.SHARPNESS, 3)
                ));

                gear.add(dmgBow);

                ItemStack fireBow = newItemStack(ItemTypes.BOW);
                fireBow.offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(
                        new ItemEnchantment(Enchantments.FLAME, 1)
                ));

                gear.add(fireBow);
                break;
            case BALANCED:
                ItemStack standardSword = newItemStack(ItemTypes.IRON_SWORD);
                gear.add(standardSword);

                ItemStack standardBow = newItemStack(ItemTypes.BOW);
                gear.add(standardBow);
                break;
        }

        for (int i = 0; i < 3; i++) {
            gear.add(newItemStack(BlockTypes.TNT, 32));
        }
        gear.add(newItemStack(ItemTypes.FLINT_AND_STEEL));
        gear.add(newItemStack(ItemTypes.SHEARS));
        gear.add(newItemStack(ItemTypes.IRON_AXE));
        gear.add(newItemStack(ItemTypes.COOKED_BEEF, 64));
        gear.add(newItemStack(ItemTypes.COMPASS));
        for (int i = 0; i < 2; i++) {
            gear.add(newItemStack(ItemTypes.ARROW, 64));
        }

        for (ItemStack stack : gear) {
            player.getInventory().offer(stack);
        }
    }

    private void giveTeamEquipment(Player player, Color teamColor) {
        // EquipmentInventory playerEquipment = player.getInventory().query(EquipmentInventory.class);

        ItemStack teamHood = newItemStack(ItemTypes.LEATHER_HELMET);
        teamHood.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Team Hood"));
        teamHood.offer(Keys.COLOR, teamColor);
        // playerEquipment.set(EquipmentTypes.HEADWEAR, teamHood);
        tf(player).inventory.armorInventory[3] = tf(teamHood);

        ItemStack teamChestplate = newItemStack(ItemTypes.LEATHER_CHESTPLATE);
        teamChestplate.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Team Chestplate"));
        teamChestplate.offer(Keys.COLOR, teamColor);
        // playerEquipment.set(EquipmentTypes.CHESTPLATE, teamChestplate);
        tf(player).inventory.armorInventory[2] = tf(teamChestplate);

        ItemStack teamLeggings = newItemStack(ItemTypes.LEATHER_LEGGINGS);
        teamLeggings.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Team Leggings"));
        teamLeggings.offer(Keys.COLOR, teamColor);
        // playerEquipment.set(EquipmentTypes.LEGGINGS, teamLeggings);
        tf(player).inventory.armorInventory[1] = tf(teamLeggings);

        ItemStack teamBoots = newItemStack(ItemTypes.LEATHER_BOOTS);
        teamBoots.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Team Boots"));
        teamBoots.offer(Keys.COLOR, teamColor);
        // playerEquipment.set(EquipmentTypes.BOOTS, teamBoots);
        tf(player).inventory.armorInventory[0] = tf(teamBoots);
    }

    private void addPlayer(Player player, Supplier<Location<World>> startingPos, Color teamColor, JungleRaidClass jrClass) {
        giveBaseEquipment(player, jrClass);
        giveTeamEquipment(player, teamColor);

        player.setLocation(startingPos.get());
    }

    public void addFFAPlayer(Player player, JungleRaidClass jrClass) {
        addPlayer(player, this::getRandomLocation, Color.WHITE, jrClass);
        freeForAllPlayers.add(player);
        teamMapping.put(player, freeForAllPlayers);
    }

    public void addBluePlayer(Player player, JungleRaidClass jrClass) {
        Location<World> spawnPoint = getRandomLocation();
        addPlayer(player, () -> spawnPoint, Color.BLUE, jrClass);
        blueTeamPlayers.add(player);
        teamMapping.put(player, blueTeamPlayers);
    }

    public void addRedPlayer(Player player, JungleRaidClass jrClass) {
        Location<World> spawnPoint = getRandomLocation();
        addPlayer(player, () -> spawnPoint, Color.RED, jrClass);
        redTeamPlayers.add(player);
        teamMapping.put(player, redTeamPlayers);
    }

    public void smartStart() {
        List<Player> ffaList = new ArrayList<>();
        List<Player> redList = new ArrayList<>();
        List<Player> blueList = new ArrayList<>();

        Collection<Player> containedPlayers = getPlayers(PARTICIPANT);
        if (containedPlayers.size() <= 1) {
            return;
        }

        for (Player player : containedPlayers) {
            BlockState state = player.getLocation().add(0, -1, 0).getBlock();
            if (state.getType() != BlockTypes.WOOL) {
                return;
            }

            Optional<?> optColor = state.getTraitValue(EnumTraits.WOOL_COLOR);
            if (optColor.isPresent()) {
                DyeColor color = (DyeColor) optColor.get();
                if (color == DyeColors.RED) {
                    redList.add(player);
                } else if (color == DyeColors.BLUE) {
                    blueList.add(player);
                } else if (color == DyeColors.WHITE) {
                    ffaList.add(player);
                } else {
                    return;
                }
            }
        }

        ffaList.stream().forEach(p -> addFFAPlayer(p, classMap.getOrDefault(p, JungleRaidClass.BALANCED)));
        redList.stream().forEach(p -> addRedPlayer(p, classMap.getOrDefault(p, JungleRaidClass.BALANCED)));
        blueList.stream().forEach(p -> addBluePlayer(p, classMap.getOrDefault(p, JungleRaidClass.BALANCED)));

        state = JungleRaidState.INITIALIZE;
        startTime = System.currentTimeMillis();
    }

    public Location<World> getRandomLocation() {
        Vector3i offset = getRegion().getMinimumPoint();
        Vector3i boundingBox = getRegion().getBoundingBox();
        Vector3i randomDest;
        while (true) {
            randomDest = new Vector3i(
                    Probability.getRandom(boundingBox.getX()),
                    Probability.getRangedRandom(16, 80),
                    Probability.getRandom(boundingBox.getZ())
            ).add(offset);

            Optional<Location<World>> optSafeDest = SafeTeleportHelper.getSafeDest(
                    new Location<>(getRegion().getExtent(), randomDest)
            );

            if (optSafeDest.isPresent()) {
                Location<World> safeDest = optSafeDest.get();
                if (safeDest.getY() > 16 && safeDest.getY() < 80) {
                    return safeDest;
                }
            }
        }
    }

    @Override
    public Clause<Player, ZoneStatus> remove(Player player) {
        playerLost(player);
        return super.remove(player);
    }

    public void playerLost(Player player) {
        Set<Player> teamPlayers = teamMapping.remove(player);
        if (teamPlayers != null) {
            teamPlayers.remove(player);

            player.getInventory().clear();
            payPlayer(player);
        }
    }

    public Color getTeamColor(Player player) {
        Set<Player> playerTeam = teamMapping.get(player);
        if (playerTeam == redTeamPlayers) {
            return Color.RED;
        } else if (playerTeam == blueTeamPlayers) {
            return Color.BLUE;
        }
        return Color.WHITE;
    }

    private void payPlayer(Player player) {

    }

    public boolean isFriendlyFire(Player attacker, Player defender) {
        Set<Player> attackerTeam = teamMapping.get(attacker);
        Set<Player> defenderTeam = teamMapping.get(defender);

        /* We want identity comparison to prevent expensive list comparisons */
        return attackerTeam == defenderTeam && attackerTeam != freeForAllPlayers && attackerTeam != null;
    }
}
