/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.goldrush;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.market.MarketImplUtil;
import com.skelril.skree.content.modifier.Modifiers;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.registry.item.generic.PrizeBox;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.zone.Zone;
import com.skelril.skree.service.internal.zone.ZoneBoundingBox;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.trait.BooleanTraits;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static com.skelril.skree.content.registry.item.generic.PrizeBox.makePrizeBox;

public class GoldRushInstance extends LegacyZoneBase implements Zone, Runnable {
    // Constants
    private static final BigDecimal MIN_START_RISK = new BigDecimal(100);

    private ZoneBoundingBox startingRoom, keyRoom, flashMemoryRoom;

    private ZoneBoundingBox flashMemoryDoor, rewardRoomDoor;

    // Block - Should be flipped
    private ConcurrentHashMap<Location<World>, Boolean> leverBlocks = new ConcurrentHashMap<>();
    private List<Location<World>> floodBlocks = new ArrayList<>();
    private List<Location<World>> chestBlocks = new ArrayList<>();

    // Block - Is unlocked
    private List<Location<World>> locks = new ArrayList<>();
    private Location rewardChest;

    // Session
    private long startTime = -1;
    private BigDecimal lootSplit = BigDecimal.ZERO;
    private int playerMod = 0;
    private BlockType floodBlockType = BlockTypes.WATER;
    protected Map<UUID, BigDecimal> cofferRisk = new HashMap<>();
    private boolean keysTriggered = false;
    private boolean checkingKeys = true;
    private boolean leversTriggered = false;
    private boolean checkingLevers = true;
    private long lastLeverSwitch = System.currentTimeMillis();
    private long lastFlood = System.currentTimeMillis();

    public GoldRushInstance(ZoneRegion region) {
        super(region);
        setup();
        remove();
    }

    private void setup() {
        Vector3i offset = getRegion().getMinimumPoint();

        rewardChest = new Location<>(
                getRegion().getExtent(),
                offset.getX() + 15,
                offset.getY() + 2,
                offset.getZ() + 6
        );

        startingRoom = new ZoneBoundingBox(offset.add(1, 1, 76), offset.add(27, 6, 13));
        keyRoom = new ZoneBoundingBox(offset.add(1, 1, 36), offset.add(25, 6, 38));
        flashMemoryRoom = new ZoneBoundingBox(offset.add(11, 1, 17), offset.add(8, 6, 18));

        flashMemoryDoor = new ZoneBoundingBox(offset.add(14, 1, 36), offset.add(3, 3, 1));
        rewardRoomDoor = new ZoneBoundingBox(offset.add(14, 1, 16), offset.add(3, 3, 1));

        findChestAndKeys();         // Setup room one
        findLeversAndFloodBlocks(); // Setup room two
    }

    @Override
    public boolean init() {
        resetChestAndKeys();
        resetLevers();
        resetFloodType();
        drainAll();
        setDoor(flashMemoryDoor, BlockTypes.IRON_BLOCK);
        setDoor(rewardRoomDoor, BlockTypes.IRON_BLOCK);
        return true;
    }

    public void tryToStart() {
        BigDecimal coffersNeeded = getCoffersNeeded();
        if (coffersNeeded.compareTo(BigDecimal.ZERO) >= 0) {
            MessageChannel channel = MessageChannel.fixed(getContained(Player.class));
            channel.send(Text.of(TextColors.RED, "Your party doesn't have a high enough coffer risk!"));
            channel.send(Text.of(TextColors.RED, "At least ", coffersNeeded, " more coffers must be risked."));
            return;
        }
        readyPlayers();
        calculateLootSplit();
        startTime = System.currentTimeMillis(); // Reset tryToStart clock
        populateChest();                        // Add content
    }

    public boolean isLocked() {
        return startTime != -1;
    }

    public boolean isComplete() {
        return checkKeys() && checkLevers();
    }

    private void findChestAndKeys() {
        keyRoom.forAll((pt) -> {
            BlockState block = getRegion().getExtent().getBlock(pt);
            if (block.getType() == BlockTypes.CHEST) {
                // TODO Sponge port
                TileEntity tileEntity = tf(getRegion().getExtent()).getTileEntity(tf(pt));
                if (tileEntity instanceof IInventory) {
                    ((IInventory) tileEntity).clear();
                }

                chestBlocks.add(new Location<>(getRegion().getExtent(), pt));
            } else if (block.getType() == BlockTypes.WALL_SIGN) {
                BlockState state = getRegion().getExtent().getBlock(pt);
                getRegion().getExtent().setBlock(pt, state.with(Keys.SIGN_LINES, Lists.newArrayList(
                        Text.EMPTY, Text.EMPTY, Text.of("- Locked -"), Text.of("Unlocked")
                )).orElse(state));
                locks.add(new Location<>(getRegion().getExtent(), pt));
            }
        });
    }

    private void populateChest() {
        for (Location<World> chest : chestBlocks) {
            // TODO Sponge port
            TileEntity tileEntity = tf(getRegion().getExtent()).getTileEntity(tf(chest.getBlockPosition()));
            if (tileEntity instanceof IInventory) {
                IInventory inventory = ((IInventory) tileEntity);
                int iterationTimes = Probability.getRandom(27);
                for (int i = iterationTimes; i > 0; --i) {
                    ItemStack targetStack;
                    if (Probability.getChance(1000)) {
                        targetStack = makePrizeBox(newItemStack(BlockTypes.GOLD_BLOCK, Probability.getRandom(6)));
                    } else {
                        targetStack = makePrizeBox(newItemStack(ItemTypes.GOLD_INGOT, Probability.getRandom(3)));
                    }
                    inventory.setInventorySlotContents(
                            Probability.getRandom(inventory.getSizeInventory()) - 1,
                            tf(targetStack)
                    );
                }

                if (Probability.getChance(10000 / iterationTimes)) {
                    inventory.setInventorySlotContents(
                            Probability.getRandom(inventory.getSizeInventory()) - 1,
                            tf(makePrizeBox(newItemStack(CustomItemTypes.PHANTOM_HYMN)))
                    );
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            Vector3i targetPos = Probability.pickOneOf(chestBlocks).getBlockPosition();
            TileEntity tileEntity = tf(getRegion().getExtent()).getTileEntity(tf(targetPos));
            if (tileEntity instanceof IInventory) {
                IInventory inventory = ((IInventory) tileEntity);
                inventory.setInventorySlotContents(
                        Probability.getRandom(inventory.getSizeInventory()) - 1,
                        new net.minecraft.item.ItemStack(CustomItemTypes.GOLD_RUSH_KEY, i)
                );
            }
        }
    }

    private void resetChestAndKeys() {
        for (Location<World> chest : chestBlocks) {
            // TODO Sponge port
            TileEntity tileEntity = tf(getRegion().getExtent()).getTileEntity(tf(chest.getBlockPosition()));
            if (tileEntity instanceof IInventory) {
                ((IInventory) tileEntity).clear();
            }
        }

        for (Location lock : locks) {
            BlockState state = lock.getBlock();
            lock.setBlock(state.with(Keys.SIGN_LINES, Lists.newArrayList(
                    Text.EMPTY, Text.EMPTY, Text.of("- Locked -"), Text.of("Unlocked")
            )).orElse(state));
        }

        keysTriggered = false;
        checkingKeys = true;
    }

    private void findLeversAndFloodBlocks() {

        Vector3i min = flashMemoryRoom.getMinimumPoint();
        Vector3i max = flashMemoryRoom.getMaximumPoint();

        int minX = min.getX();
        int minZ = min.getZ();
        int minY = min.getY();
        int maxX = max.getX();
        int maxZ = max.getZ();
        int maxY = max.getY();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = maxY; y >= minY; --y) {
                    BlockState state = getRegion().getExtent().getBlock(x, y, z);
                    if (state.getType() == BlockTypes.LEVER) {
                        Location<World> loc = new Location<>(getRegion().getExtent(), x, y, z);
                        loc.setBlock(state.withTrait(BooleanTraits.LEVER_POWERED, false).orElse(state));

                        leverBlocks.put(loc, !Probability.getChance(3));
                        for (int i = y; i < maxY; i++) {
                            BlockType type = getRegion().getExtent().getBlockType(x, i, z);
                            if (type == BlockTypes.AIR) {
                                floodBlocks.add(new Location<>(getRegion().getExtent(), x, i, z));
                                break;
                            }
                        }
                        break; // One lever a column only
                    }
                }
            }
        }
    }

    public boolean checkLevers() {
        if (!checkingLevers) {
            return leversTriggered;
        }

        for (Map.Entry<Location<World>, Boolean> lever : leverBlocks.entrySet()) {
            boolean leverState = lever.getKey().getBlock().getTraitValue(BooleanTraits.LEVER_POWERED).orElse(false);
            if (leverState != lever.getValue()) return false;
        }
        return true;
    }

    public void unlockLevers() {
        drainAll();
        setDoor(rewardRoomDoor, BlockTypes.AIR);
        leversTriggered = true;
        checkingLevers = false;
    }

    private void resetLevers() {
        leversTriggered = false;
        checkingLevers = true;

        for (Location<World> entry : leverBlocks.keySet()) {
            BlockState state = entry.getBlock();

            entry.setBlock(state.withTrait(BooleanTraits.LEVER_POWERED, false).orElse(state));

            leverBlocks.put(entry, !Probability.getChance(3));
        }
    }

    public Location getRewardChestLoc() {
        return rewardChest;
    }

    @Override
    public void forceEnd() {
        resetChestAndKeys();
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        if (isLocked()) {
            return new Clause<>(player, ZoneStatus.NO_REJOIN);
        }

        player.setLocation(new Location<>(getRegion().getExtent(), startingRoom.getCenter()));
        return new Clause<>(player, ZoneStatus.ADDED);
    }

    @Override
    public Clause<Player, ZoneStatus> remove(Player player) {
        // TODO remove any items given
        WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);
        player.setLocation(service.getEffectWrapper("Main").getWorlds().iterator().next().getSpawnLocation());
        return new Clause<>(player, ZoneStatus.REMOVED);
    }

    private BigDecimal getCoffersNeeded() {
        Collection<Player> cPlayers = getContained(Player.class);
        Iterator<Player> it = cPlayers.iterator();

        BigDecimal totalRisk = BigDecimal.ZERO;

        while (it.hasNext()) {
            Player next = it.next();
            BigDecimal origCharge = cofferRisk.get(next.getUniqueId());
            if (origCharge == null) {
                continue;
            }
            totalRisk = totalRisk.add(origCharge);
        }

        return totalRisk.subtract(MIN_START_RISK);
    }

    private void calculateLootSplit() {
        Collection<Player> cPlayers = getContained(Player.class);
        Iterator<Player> it = cPlayers.iterator();
        while (it.hasNext()) {
            Player next = it.next();
            BigDecimal origCharge = cofferRisk.get(next.getUniqueId());
            if (origCharge == null) {
                remove(next);
                it.remove();
                continue;
            }
            lootSplit = lootSplit.add(origCharge.multiply(new BigDecimal(.3)));
        }
        lootSplit = lootSplit.divide(new BigDecimal(cPlayers.size()), RoundingMode.HALF_DOWN);

        playerMod = Math.max(1, cPlayers.size() / 2);
        if (Probability.getChance(35)) lootSplit = lootSplit.multiply(BigDecimal.TEN);
        if (Probability.getChance(15)) lootSplit = lootSplit.multiply(new BigDecimal(2));

        Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
        if (optService.isPresent()) {
            ModifierService service = optService.get();
            if (service.isActive(Modifiers.QUAD_GOLD_RUSH)) lootSplit = lootSplit.multiply(new BigDecimal(4));
        }
    }

    private void readyPlayers() {
        Collection<Player> players = getContained(Player.class);
        for (Player player : players) {
            // Reset vitals
            player.offer(Keys.HEALTH, player.get(Keys.MAX_HEALTH).orElse(20D));
            player.offer(Keys.FOOD_LEVEL, 20);
            player.offer(Keys.SATURATION, 20D);
            player.offer(Keys.EXHAUSTION, 0D);
        }

        // Partner talk
        Task.builder().execute(() -> {
            MessageChannel.fixed(players).send(Text.of(
                    TextColors.YELLOW,
                    "[Partner] I've disabled the security systems for now."
            ));
            Task.builder().execute(() -> {
                MessageChannel.fixed(players).send(Text.of(
                        TextColors.YELLOW,
                        "[Partner] For your sake kid I hope you can move quickly."
                ));
            }).delay(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
        }).delay(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
    }

    @Override
    public void run() {
        if (!isLocked()) return; // If it's not locked things haven't been started yet
        if (System.currentTimeMillis() - startTime > TimeUnit.MINUTES.toMillis(7) || isEmpty()) {
            expire();
            return;
        }
        if (checkKeys()) {
            unlockKeys();
            if (getContained(Player.class).stream().filter(p -> keyRoom.contains(p.getLocation().getPosition())).count() > 0) {
                setDoor(flashMemoryDoor, BlockTypes.AIR);
            } else {
                setDoor(flashMemoryDoor, BlockTypes.IRON_BLOCK);
                if (checkLevers()) {
                    unlockLevers();
                } else {
                    randomizeLevers();
                    checkFloodType();
                    flood();
                }
            }
        }
    }

    public void refundPlayer(Player player) {
        BigDecimal fee = cofferRisk.get(player.getUniqueId());

        // They didn't pay, CHEATER!!!
        if (fee == null) return;
        MarketImplUtil.setBalanceTo(player, fee.add(MarketImplUtil.getMoney(player)), Cause.of(this));
        remove(player);
        player.sendMessage(Text.of(TextColors.YELLOW, "[Partner] These @$#&!@# restarts... Here, have your bail money..."));
    }

    public boolean payPlayer(Player player) {
        BigDecimal fee = cofferRisk.get(player.getUniqueId());
        // They didn't pay, CHEATER!!!
        if (fee == null) return false;

        net.minecraft.item.ItemStack[] itemStacks = tf(player).inventory.mainInventory;
        BigDecimal goldValue = BigDecimal.ZERO;
        BigDecimal itemValue = BigDecimal.ZERO;

        Optional<MarketService> optService = Sponge.getServiceManager().provide(MarketService.class);

        for (int i = 0; i < itemStacks.length; ++i) {
            net.minecraft.item.ItemStack is = itemStacks[i];

            if (is == null || is.getItem() != CustomItemTypes.PRIZE_BOX) continue;

            Optional<ItemStack> optOpened = PrizeBox.getPrizeStack(is);
            if (optService.isPresent() && optOpened.isPresent()) {
                MarketService service = optService.get();
                Optional<BigDecimal> value = service.getPrice(optOpened.get());
                if (value.isPresent()) {
                    if (is.getItem() == ItemTypes.GOLD_NUGGET || is.getItem() == ItemTypes.GOLD_INGOT || is.getItem() == BlockTypes.GOLD_BLOCK) {
                        goldValue = goldValue.add(value.get());
                        itemStacks[i] = null;
                    } else {
                        itemValue = itemValue.add(value.get());
                    }
                }
            }
        }

        // TODO formatting
        player.sendMessage(Text.of(TextColors.YELLOW, "You obtain: "));
        player.sendMessage(Text.of(TextColors.YELLOW, " - Bail: ", fee, "."));
        player.sendMessage(Text.of(TextColors.YELLOW, " - Split: ", lootSplit, "."));
        if (goldValue.compareTo(BigDecimal.ZERO) > 0) {
            player.sendMessage(Text.of(TextColors.YELLOW, " - Gold: ", goldValue.intValue(), "."));
        }
        if (itemValue.compareTo(BigDecimal.ZERO) > 0) {
            player.sendMessage(Text.of(TextColors.YELLOW, " - Items: ", itemValue.intValue(), "."));
        }

        MarketImplUtil.setBalanceTo(player, fee.add(lootSplit).add(goldValue).add(MarketImplUtil.getMoney(player)), Cause.of(this));
        remove(player);
        return true;
    }

    public List<Location<World>> getLockLocations() {
        return locks;
    }

    public boolean checkKeys() {
        if (!checkingKeys) {
            return keysTriggered;
        }

        for (Location<World> lock : locks) {
            List<Text> signText = lock.getBlock().get(Keys.SIGN_LINES).orElse(
                    Lists.newArrayList(Text.EMPTY, Text.EMPTY, Text.EMPTY, Text.EMPTY)
            );

            if (signText.get(2).toPlain().startsWith("-")) return false;
        }
        return true;
    }

    public void unlockKeys() {
        keysTriggered = true;
        checkingKeys = false;
    }

    private void setDoor(ZoneBoundingBox door, BlockType type) {
        door.forAll((pt) -> getRegion().getExtent().setBlockType(pt, type));
    }

    private void drainAll() {
        flashMemoryRoom.forAll((pt) -> {
            BlockType type = getRegion().getExtent().getBlockType(pt);
            if (type == BlockTypes.WATER || type == BlockTypes.FLOWING_WATER || type == BlockTypes.LAVA || type == BlockTypes.FLOWING_LAVA) {
                getRegion().getExtent().setBlockType(pt, BlockTypes.AIR);
            }
        });
    }

    private void randomizeLevers() {

        if (System.currentTimeMillis() - lastLeverSwitch >= TimeUnit.SECONDS.toMillis(14)) {
            for (Location<World> entry : leverBlocks.keySet()) {
                BlockState state = entry.getBlock();
                entry.setBlock(state.withTrait(BooleanTraits.LEVER_POWERED, false).orElse(state));
                leverBlocks.put(entry, !Probability.getChance(3));
            }
            lastLeverSwitch = System.currentTimeMillis();
            randomizeLevers();
        } else if (System.currentTimeMillis() - lastLeverSwitch == 0) {
            for (Location<World> entry : leverBlocks.keySet()) {
                entry.add(0, -1, 0).setBlockType(BlockTypes.REDSTONE_LAMP);
            }

            Task.builder().execute(() -> {
                for (Map.Entry<Location<World>, Boolean> entry : leverBlocks.entrySet()) {
                    entry.getKey().add(0, -1, 0).setBlockType(
                            entry.getValue() ? BlockTypes.LIT_REDSTONE_LAMP : BlockTypes.REDSTONE_LAMP
                    );
                }

                Task.builder().execute(this::randomizeLevers).delayTicks(15).submit(SkreePlugin.inst());
            }).delayTicks(15).submit(SkreePlugin.inst());
        } else {
            for (Location entry : leverBlocks.keySet()) {
                entry.add(0, -1, 0).setBlockType(BlockTypes.REDSTONE_LAMP);
            }
        }
    }

    private void checkFloodType() {
        for (Player player : getContained(Player.class)) {
            if (player.getInventory().query(CustomItemTypes.PHANTOM_HYMN).size() != 0) {
                if (floodBlockType != BlockTypes.LAVA) {
                    drainAll(); // Force away all water
                    floodBlockType = BlockTypes.LAVA;
                }
                break;
            }
        }
    }

    private void flood() {
        if (System.currentTimeMillis() - startTime >= TimeUnit.SECONDS.toMillis((3 * 60) / playerMod)) {

            for (Location<World> floodBlock : floodBlocks) {
                floodBlock.setBlockType(floodBlockType);
            }

            if (System.currentTimeMillis() - lastFlood >= TimeUnit.SECONDS.toMillis(30 / Math.max(1, playerMod))) {

                Vector3i min = flashMemoryRoom.getMinimumPoint();
                Vector3i max = flashMemoryRoom.getMaximumPoint();

                int minX = min.getX();
                int minZ = min.getZ();
                int minY = min.getY();
                int maxX = max.getX();
                int maxZ = max.getZ();
                int maxY = max.getY();

                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        for (int y = minY; y <= maxY; y++) {
                            BlockType type = getRegion().getExtent().getBlockType(x, y, z);
                            if (type == BlockTypes.AIR) {
                                getRegion().getExtent().setBlockType(x, y, z, floodBlockType);
                                break;
                            }
                        }
                    }
                }
                lastFlood = System.currentTimeMillis();
            }
        }
    }

    private void resetFloodType() {
        floodBlockType = BlockTypes.WATER;
    }
}