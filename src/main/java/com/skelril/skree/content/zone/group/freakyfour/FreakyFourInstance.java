/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.Clause;
import com.skelril.nitro.entity.VelocityEntitySpawner;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossManager;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.content.zone.group.freakyfour.boss.CharlotteBossManager;
import com.skelril.skree.content.zone.group.freakyfour.boss.DaBombBossManager;
import com.skelril.skree.content.zone.group.freakyfour.boss.FrimusBossManager;
import com.skelril.skree.content.zone.group.freakyfour.boss.SnipeeBossManager;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import com.skelril.skree.service.internal.zone.ZoneBoundingBox;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.CaveSpider;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class FreakyFourInstance extends LegacyZoneBase implements Runnable {

    private final FreakyFourConfig config;

    private final CharlotteBossManager charlotteManager;
    private final FrimusBossManager frimusManager;
    private final DaBombBossManager daBombManager;
    private final SnipeeBossManager snipeeManager;

    private boolean loadingBoss = false;
    private FreakyFourBoss currentboss = null;

    private EnumMap<FreakyFourBoss, BossManager<Living, ZoneBossDetail<FreakyFourInstance>>> bossManagers = new EnumMap<>(FreakyFourBoss.class);
    private EnumMap<FreakyFourBoss, Boss<Living, ZoneBossDetail<FreakyFourInstance>>> bosses = new EnumMap<>(FreakyFourBoss.class);
    private EnumMap<FreakyFourBoss, ZoneBoundingBox> regions = new EnumMap<>(FreakyFourBoss.class);

    private List<Boss<CaveSpider, ZoneBossDetail<FreakyFourInstance>>> charlotteMinions = new ArrayList<>();
    private int tick = 0;

    public FreakyFourInstance(ZoneRegion region, FreakyFourConfig config,
                              CharlotteBossManager charlotteManager,
                              FrimusBossManager frimusManager,
                              DaBombBossManager daBombManager,
                              SnipeeBossManager snipeeManager) {
        super(region);
        this.config = config;

        bossManagers.put(FreakyFourBoss.CHARLOTTE, this.charlotteManager = charlotteManager);
        bossManagers.put(FreakyFourBoss.FRIMUS, this.frimusManager = frimusManager);
        bossManagers.put(FreakyFourBoss.DA_BOMB, this.daBombManager = daBombManager);
        bossManagers.put(FreakyFourBoss.SNIPEE, this.snipeeManager = snipeeManager);
    }

    @Override
    public boolean init() {
        setUp();
        remove();
        prepare();
        return true;
    }

    private void setUp() {
        Vector3i offset = region.getMinimumPoint();
        regions.put(FreakyFourBoss.CHARLOTTE, new ZoneBoundingBox(offset.add(72, 7, 1), new Vector3i(22, 5, 41)));
        regions.put(FreakyFourBoss.FRIMUS, new ZoneBoundingBox(offset.add(48, 7, 1), new Vector3i(22, 5, 41)));
        regions.put(FreakyFourBoss.DA_BOMB, new ZoneBoundingBox(offset.add(24, 7, 1), new Vector3i(22, 5, 41)));
        regions.put(FreakyFourBoss.SNIPEE, new ZoneBoundingBox(offset.add(1, 7, 1), new Vector3i(21, 5, 41)));
    }

    @Override
    public void run() {
        if (isEmpty()) {
            expire();
            return;
        }
        ++tick;
        if (tick % 6 == 0) {
            for (FreakyFourBoss boss : FreakyFourBoss.values()) {
                if (isSpawned(boss)) {
                    run(boss);
                }
            }
        }
    }

    private void prepare() {
        for (FreakyFourBoss boss : FreakyFourBoss.values()) {
            prepare(boss);
        }
    }

    private void prepare(FreakyFourBoss boss) {
        switch (boss) {
            case CHARLOTTE:
                prepareCharlotte();
                break;
            case FRIMUS:
                prepareFrimus();
                break;
        }
    }

    private void prepareCharlotte() {
        ZoneBoundingBox charlotte_RG = regions.get(FreakyFourBoss.CHARLOTTE);
        charlotte_RG.forAll(pt -> {
            if (getRegion().getExtent().getBlockType(pt) == BlockTypes.WEB) {
                getRegion().getExtent().setBlockType(pt, BlockTypes.AIR, Cause.source(SkreePlugin.container()).build());
            }
        });
    }

    private void prepareFrimus() {
        ZoneBoundingBox frimus_RG = regions.get(FreakyFourBoss.FRIMUS);
        frimus_RG.forAll(pt -> {
            BlockType originalType = getRegion().getExtent().getBlockType(pt);
            if (originalType == BlockTypes.FIRE || originalType == BlockTypes.FLOWING_LAVA || originalType == BlockTypes.LAVA) {
                getRegion().getExtent().setBlockType(pt, BlockTypes.AIR, Cause.source(SkreePlugin.container()).build());
            }
        });
    }

    @Override
    public void forceEnd() {
        for (FreakyFourBoss boss : FreakyFourBoss.values()) {
            cleanUp(boss);
        }
        cleanUpCharlotteMinions();
        remove(getPlayers(PARTICIPANT));
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        player.setLocation(new Location<>(getRegion().getExtent(), getCenter(FreakyFourBoss.CHARLOTTE)));
        return new Clause<>(player, ZoneStatus.ADDED);
    }

    public void cleanUp(FreakyFourBoss boss) {
        Boss<Living, ZoneBossDetail<FreakyFourInstance>> aBoss = bosses.get(boss);
        if (aBoss != null) {
            bossManagers.get(boss).silentUnbind(aBoss);
        }
    }

    public void cleanUpCharlotteMinions() {
        charlotteMinions.forEach(e -> charlotteManager.getMinionManager().silentUnbind(e));
    }

    public boolean isSpawned(FreakyFourBoss boss) {
        if (loadingBoss) {
            return true;
        }

        getContained(getRegion(boss), boss.getEntityType()).stream()
                .filter(e -> !e.isRemoved())
                .filter(e -> e instanceof Living)
                .map(e -> (Living) e)
                .forEach(e -> {

            Optional<Boss<Living, ZoneBossDetail<FreakyFourInstance>>> b = bossManagers.get(boss).updateLookup(e);
            if (!b.isPresent() && !(e instanceof CaveSpider)) {
                e.remove();
            }
        });

        Boss<Living, ZoneBossDetail<FreakyFourInstance>> bossDef = bosses.get(boss);
        return bossDef != null && bossDef.getTargetEntity().isPresent();
    }

    public Optional<Living> getBoss(FreakyFourBoss boss) {
        Boss<Living, ZoneBossDetail<FreakyFourInstance>> aBoss = bosses.get(boss);
        return aBoss != null ? aBoss.getTargetEntity() : Optional.empty();
    }

    public Optional<FreakyFourBoss> getCurrentboss() {
        return Optional.ofNullable(currentboss);
    }

    public void setCurrentboss(FreakyFourBoss currentBoss) {
        this.currentboss = currentBoss;
    }

    public Vector3d getCenter(FreakyFourBoss boss) {
        return regions.get(boss).getCenter();
    }

    public ZoneBoundingBox getRegion(FreakyFourBoss boss) {
        return regions.get(boss);
    }

    public void spawnBoss(FreakyFourBoss boss) {
        spawnBoss(boss, 5 * 20);
    }

    public void spawnBoss(FreakyFourBoss boss, long delay) {
        loadingBoss = true;
        Task.builder().execute(() -> {
            Entity entity = getRegion().getExtent().createEntity(boss.getEntityType(), getCenter(boss));
            getRegion().getExtent().spawnEntity(entity, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());

            Boss<Living, ZoneBossDetail<FreakyFourInstance>> aBoss = new Boss<>(
                    (Living) entity,
                    new ZoneBossDetail<>(this)
            );

            bossManagers.get(boss).bind(aBoss);
            bosses.put(boss, aBoss);
            loadingBoss = false;
        }).delayTicks(delay).submit(SkreePlugin.inst());
    }

    public void bossDied(FreakyFourBoss boss) {
        bosses.put(boss, null);
    }

    public void run(FreakyFourBoss boss) {
        if (loadingBoss) {
            return;
        }

        switch (boss) {
            case CHARLOTTE:
                runCharlotte();
                break;
            case FRIMUS:
                runFrimus();
                break;
            case SNIPEE:
                runSnipee();
                break;
        }
    }

    private void createWall(ZoneBoundingBox region,
                            Predicate<BlockType> oldExpr,
                            Predicate<BlockType> newExpr,
                            BlockType oldType, BlockType newType,
                            int density, int floodFloor) {

        final Vector3i min = region.getMinimumPoint();
        final Vector3i max = region.getMaximumPoint();
        int minX = min.getX();
        int minY = min.getY();
        int minZ = min.getZ();
        int maxX = max.getX();
        int maxY = max.getY();
        int maxZ = max.getZ();

        int initialTimes = maxZ - minZ + 1;
        IntegratedRunnable integratedRunnable = new IntegratedRunnable() {
            @Override
            public boolean run(int times) {
                int startZ = minZ + (initialTimes - times) - 1;

                for (int x = minX; x <= maxX; ++x) {
                    for (int z = startZ; z < Math.min(maxZ, startZ + 4); ++z) {
                        boolean flood = Probability.getChance(density);
                        for (int y = minY; y <= maxY; ++y) {
                            BlockType block = getRegion().getExtent().getBlockType(x, y, z);
                            if (z == startZ && newExpr.test(block)) {
                                getRegion().getExtent().setBlockType(x, y, z, oldType, Cause.source(SkreePlugin.container()).build());
                            } else if (flood && oldExpr.test(block)) {
                                getRegion().getExtent().setBlockType(x, y, z, newType, Cause.source(SkreePlugin.container()).build());
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public void end() {
                if (floodFloor != -1) {
                    for (int x = minX; x <= maxX; ++x) {
                        for (int z = minZ; z <= maxZ; ++z) {
                            if (!Probability.getChance(floodFloor)) continue;
                            BlockType block = getRegion().getExtent().getBlockType(x, minY, z);
                            if (oldExpr.test(block)) {
                                getRegion().getExtent().setBlockType(x, minY, z, newType, Cause.source(SkreePlugin.container()).build());
                            }
                        }
                    }
                }
            }
        };
        TimedRunnable<IntegratedRunnable> timedRunnable = new TimedRunnable<>(integratedRunnable, initialTimes);
        Task task = Task.builder().execute(timedRunnable).interval(
                500,
                MILLISECONDS
        ).submit(SkreePlugin.inst());
        timedRunnable.setTask(task);
    }

    private void runCharlotte() {
        Living boss = getBoss(FreakyFourBoss.CHARLOTTE).get();
        for (int i = Probability.getRandom(10); i > 0; --i) {
            spawnCharlotteMinion(boss.getLocation().getPosition());
        }

        ZoneBoundingBox charlotte_RG = regions.get(FreakyFourBoss.CHARLOTTE);
        switch (Probability.getRandom(3)) {
            case 1:
                createWall(
                        charlotte_RG,
                        type -> type == BlockTypes.AIR,
                        type -> type == BlockTypes.WEB,
                        BlockTypes.AIR,
                        BlockTypes.WEB,
                        1,
                        config.charlotteFloorWeb
                );
                break;
            case 2:
                if (boss instanceof Monster) {
                    Optional<Entity> optTarget = ((Monster) boss).getTarget();
                    if (optTarget.isPresent() && contains(optTarget.get())) {
                        Entity target = optTarget.get();
                        ZoneBoundingBox targetArea = new ZoneBoundingBox(
                                target.getLocation().getPosition().sub(1, 1, 1).toInt(),
                                new Vector3i(3, 3, 3)
                        );

                        targetArea.forAll(pt -> {
                            if (getRegion().getExtent().getBlockType(pt) == BlockTypes.AIR) {
                                getRegion().getExtent().setBlockType(
                                        pt,
                                        BlockTypes.WEB,
                                        Cause.source(SkreePlugin.container()).build()
                                );
                            }
                        });
                    }
                    break;
                }
            case 3:
                charlotte_RG.forAll(pt -> {
                    if (!Probability.getChance(config.charlotteWebSpider)) {
                        return;
                    }
                    if (getRegion().getExtent().getBlockType(pt) == BlockTypes.WEB) {
                        getRegion().getExtent().setBlockType(
                                pt,
                                BlockTypes.AIR,
                                Cause.source(SkreePlugin.container()).build()
                        );
                        spawnCharlotteMinion(pt.toDouble().add(.5, 0, .5));
                    }
                });
                break;
        }
    }

    private void spawnCharlotteMinion(Vector3d position) {
        Entity entity = getRegion().getExtent().createEntity(EntityTypes.CAVE_SPIDER, position);
        getRegion().getExtent().spawnEntity(entity, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());

        Boss<CaveSpider, ZoneBossDetail<FreakyFourInstance>> boss = new Boss<>(
                (CaveSpider) entity,
                new ZoneBossDetail<>(this)
        );

        charlotteMinions.add(boss);
        charlotteManager.getMinionManager().bind(boss);
    }

    private void runFrimus() {
        createWall(
                getRegion(FreakyFourBoss.FRIMUS),
                type -> type == BlockTypes.AIR,
                type -> type == BlockTypes.LAVA || type == BlockTypes.FLOWING_LAVA,
                BlockTypes.AIR,
                BlockTypes.LAVA,
                config.frimusWallDensity,
                -1
        );

        for (Player player : getPlayers(PlayerClassifier.PARTICIPANT)) {
            List<PotionEffect> oldPotions = player.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
            List<PotionEffect> newPotions = oldPotions.stream().filter(
                    effect -> effect.getType() != PotionEffectTypes.FIRE_RESISTANCE
            ).collect(Collectors.toList());

            if (oldPotions.size() != newPotions.size()) {
                player.offer(Keys.POTION_EFFECTS, newPotions);
            }
        }
    }

    private void runSnipee() {
        Living snipee = getBoss(FreakyFourBoss.SNIPEE).get();

        VelocityEntitySpawner.sendRadial(
                EntityTypes.TIPPED_ARROW,
                snipee,
                20,
                1.6F,
                Cause.source(SpawnCause.builder().type(SpawnTypes.PROJECTILE).build()).build()
        );
    }

    public void dabombDetonate(double percentEffectiveness) {
        ZoneBoundingBox dabomb_RG = getRegion(FreakyFourBoss.DA_BOMB);

        Vector3i min = dabomb_RG.getMinimumPoint();
        Vector3i max = dabomb_RG.getMaximumPoint();

        int minX = min.getX();
        int minY = min.getY();
        int minZ = min.getZ();
        int maxX = max.getX();
        int maxZ = max.getZ();

        int dmgFact = (int) Math.max(3, percentEffectiveness * config.daBombTNTStrength);

        for (int x = minX; x < maxX; ++x) {
            for (int z = minZ; z < maxZ; ++z) {
                if (Probability.getChance(config.daBombTNT)) {
                    getRegion().getExtent().triggerExplosion(
                            Explosion.builder()
                                    .location(new Location<>(getRegion().getExtent(), new Vector3d(x, minY, z)))
                                    .radius(dmgFact)
                                    .canCauseFire(false)
                                    .shouldDamageEntities(true)
                                    .build()
                    );
                }
            }
        }
    }
}