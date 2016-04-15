/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.Clause;
import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.EntityDetail;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.content.zone.group.catacombs.instruction.CatacombsHealthInstruction;
import com.skelril.skree.content.zone.group.catacombs.instruction.bossmove.*;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.SPECTATOR;

public class CatacombsInstance extends LegacyZoneBase implements Runnable {

    private final BossManager<Zombie, CatacombsBossDetail> bossManager;
    private final BossManager<Zombie, CatacombsBossDetail> waveMobManager;

    private int ticks = 0;
    private int wave = 0;

    private Location<World> entryPoint;

    public CatacombsInstance(ZoneRegion region, BossManager<Zombie, CatacombsBossDetail> bossManager, BossManager<Zombie, CatacombsBossDetail> waveMobManager) {
        super(region);
        this.bossManager = bossManager;
        this.waveMobManager = waveMobManager;
    }

    @Override
    public boolean init() {
        setUp();
        remove();
        return true;
    }

    @Override
    public void forceEnd() {
        remove(getPlayers(PARTICIPANT));
        remove();
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        player.setLocation(entryPoint);
        return new Clause<>(player, ZoneStatus.ADDED);
    }

    private void setUp() {
        Vector3i min = getRegion().getMinimumPoint();
        this.entryPoint = new Location<>(getRegion().getExtent(), min.getX() + 17.5, min.getY() + 1, min.getZ() + 58.5);

        Task.builder().execute(this::checkedSpawnWave).delay(5, TimeUnit.SECONDS).submit(SkreePlugin.inst());
    }

    @Override
    public void run() {
        if (isEmpty()) {
            expire();
            return;
        }

        // This shouldn't be necessary, however, there seems to be an
        // edge case where it is, so it's preferable to cover it rather
        // than ignore the issue
        if (++ticks % 60 == 0) {
            checkedSpawnWave();
        }
    }

    public void checkedSpawnWave() {
        if (!hasActiveMobs()) {
            remove();
            spawnWave();
        }
    }

    public boolean hasActiveMobs() {
        for (Zombie zombie : getContained(Zombie.class)) {
            if (waveMobManager.lookup(zombie.getUniqueId()).isPresent()) {
                return true;
            }
            if (bossManager.lookup(zombie.getUniqueId()).isPresent()) {
                return true;
            }
        }
        return false;
    }

    public int getSpawnCount(int wave) {
        return (int) (Math.pow(wave, 2) + (wave * 3)) / 2;
    }

    public int getSpeed() {

        if ((wave + 1) % 5 == 0) {
            return 1;
        }

        for (Player player : getPlayers(PlayerClassifier.PARTICIPANT)) {
            if (player.getInventory().query((ItemType) CustomItemTypes.PHANTOM_CLOCK).size() > 0) {
                return 2;
            }
        }
        return 1;
    }

    public void spawnWave() {
        wave += getSpeed();
        if (wave % 5 == 0) {
            spawnBossWave();
        } else {
            spawnNormalWave();
        }

        for (Player player : getPlayers(SPECTATOR)) {
            player.sendTitle(
                    Title.builder()
                            .title(Text.of(TextColors.RED, "Wave"))
                            .subtitle(Text.of(TextColors.RED, wave))
                            .fadeIn(20)
                            .fadeOut(20)
                            .build()
            );
        }
    }

    private void spawnBossWave() {
        Zombie zombie = checkedZombieSpawn(entryPoint);
        Boss<Zombie, CatacombsBossDetail> boss = new Boss<>(zombie, new CatacombsBossDetail(this, wave));

        List<Instruction<DamageCondition, Boss<Zombie, CatacombsBossDetail>>> damageProcessor = boss.getDamageProcessor();

        if (Probability.getChance(2)) {
            damageProcessor.add(new ThorAttack());
        }
        if (Probability.getChance(2)) {
            damageProcessor.add(new SoulReaper());
        }

        List<Instruction<DamagedCondition, Boss<Zombie, CatacombsBossDetail>>> damagedProcessor = boss.getDamagedProcessor();
        if (Probability.getChance(4)) {
            damagedProcessor.add(new BlipDefense());
        }
        if (Probability.getChance(3)) {
            damagedProcessor.add(new ExplosiveArrowBarrage() {
                @Override
                public boolean activate(EntityDetail detail) {
                    return Probability.getChance(12);
                }
            });
        }
        if (Probability.getChance(2)) {
            damagedProcessor.add(new DeathMark());
        }
        if (Probability.getChance(2)) {
            damagedProcessor.add(new CatacombsDamageNearby());
        }
        if (Probability.getChance(2)) {
            damagedProcessor.add(new UndeadMinionRetaliation(Probability.getRangedRandom(12, 25)));
        }

        bossManager.bind(boss);
    }

    private void spawnNormalWave() {
        Vector3i min = getRegion().getMinimumPoint();
        Vector3i max = getRegion().getMaximumPoint();

        int minX = min.getX();
        int minZ = min.getZ();
        int maxX = max.getX();
        int maxZ = max.getZ();

        final int y = min.getY() + 2;
        int needed = getSpawnCount(wave);

        for (int i = needed; i > 0; --i) {
            int x, z;
            BlockType type, aboveType;
            do {
                x = Probability.getRangedRandom(minX, maxX);
                z = Probability.getRangedRandom(minZ, maxZ);

                type = getRegion().getExtent().getBlockType(x, y, z);
                aboveType = getRegion().getExtent().getBlockType(x, y + 1, z);
            } while (type != BlockTypes.AIR || aboveType != BlockTypes.AIR);
            spawnWaveMob(new Location<>(getRegion().getExtent(), x + .5, y, z + .5));
        }
    }

    public void spawnWaveMob(Location<World> loc) {
        Boss<Zombie, CatacombsBossDetail> waveMob;
        if (Probability.getChance(25)) {
            waveMob = spawnStrong(loc);
        } else {
            waveMob = spawnNormal(loc);
        }

        waveMobManager.bind(waveMob);
    }

    private Zombie checkedZombieSpawn(Location<World> loc) {
        Optional<Entity> optEnt = loc.getExtent().createEntity(EntityTypes.ZOMBIE, loc.getPosition());
        if (optEnt.isPresent()) {
            loc.getExtent().spawnEntity(optEnt.get(), Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
            return (Zombie) optEnt.get();
        }
        throw new IllegalStateException("Zombie could not be spawned");
    }

    private Boss<Zombie, CatacombsBossDetail> spawnStrong(Location<World> loc) {
        Zombie zombie = checkedZombieSpawn(loc);
        Boss<Zombie, CatacombsBossDetail> boss = new Boss<>(zombie, new CatacombsBossDetail(this, wave * 2));

        List<Instruction<BindCondition, Boss<Zombie, CatacombsBossDetail>>> bindProcessor = boss.getBindProcessor();
        bindProcessor.add(new CatacombsHealthInstruction(25));
        bindProcessor.add(new NamedBindInstruction<>("Wrathful Zombie"));

        return boss;
    }

    private Boss<Zombie, CatacombsBossDetail> spawnNormal(Location<World> loc) {
        Zombie zombie = checkedZombieSpawn(loc);
        Boss<Zombie, CatacombsBossDetail> boss = new Boss<>(zombie, new CatacombsBossDetail(this, wave));

        List<Instruction<BindCondition, Boss<Zombie, CatacombsBossDetail>>> bindProcessor = boss.getBindProcessor();
        bindProcessor.add(new CatacombsHealthInstruction(20));
        bindProcessor.add(new NamedBindInstruction<>("Guardian Zombie"));

        return boss;
    }
}
