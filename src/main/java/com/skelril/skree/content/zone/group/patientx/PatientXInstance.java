/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.patientx;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.data.util.LightLevelUtil;
import com.skelril.nitro.entity.EntityHealthPrinter;
import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.text.CombinedText;
import com.skelril.nitro.text.GeneratedText;
import com.skelril.nitro.text.PlaceHolderText;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossManager;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.service.internal.zone.*;
import net.minecraft.entity.monster.EntityZombie;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.skelril.nitro.entity.EntityHealthUtil.getMaxHealth;
import static com.skelril.nitro.entity.EntityHealthUtil.heal;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.SPECTATOR;

public class PatientXInstance extends LegacyZoneBase implements Zone, Runnable {

    private final PatientXConfig config;
    private final BossManager<Zombie, ZoneBossDetail<PatientXInstance>> bossManager;

    private ZoneBoundingBox ice, drops;

    private Boss<Zombie, ZoneBossDetail<PatientXInstance>> boss = null;
    private long attackDur = 0;
    private PatientXAttack lastAttack = null;
    private long lastUltimateAttack = 0;
    private long lastDeath = 0;
    private long lastTelep = 0;
    private int emptyTicks = 0;
    private int activeTicks = 0;
    private double difficulty;
    private Random random = new Random();

    private List<Location<World>> destinations = new ArrayList<>();

    public PatientXInstance(ZoneRegion region, PatientXConfig config, BossManager<Zombie, ZoneBossDetail<PatientXInstance>> bossManager) {
        super(region);
        this.config = config;
        this.bossManager = bossManager;
    }

    public void setUp() {
        Vector3i min = getRegion().getMinimumPoint();
        World world = getRegion().getExtent();

        destinations.add(new Location<>(world, min.getX() + 11.5, min.getY() + 25, min.getZ() + 34.5));
        destinations.add(new Location<>(world, min.getX() + 10.5, min.getY() + 20, min.getZ() + 34.5));
        destinations.add(new Location<>(world, min.getX() + 57.5, min.getY() + 25, min.getZ() + 34.5));
        destinations.add(new Location<>(world, min.getX() + 63.5, min.getY() + 25, min.getZ() + 45.5));
        destinations.add(new Location<>(world, min.getX() + 62.5, min.getY() + 18, min.getZ() + 34.5));
        destinations.add(new Location<>(world, min.getX() + 34.5, min.getY() + 29, min.getZ() + 60.5));
        destinations.add(new Location<>(world, min.getX() + 34.5, min.getY() + 18, min.getZ() + 34.5));
        destinations.add(new Location<>(world, min.getX() + 24.5, min.getY() + 29, min.getZ() + 41.5));
        destinations.add(getCenter());

        ice = new ZoneBoundingBox(min.add(14, 17, 14), new Vector3i(49, 1, 45));
        drops = new ZoneBoundingBox(min.add(14, 24, 14), new Vector3i(41, 1, 41));
    }

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = Math.max(config.minDifficulty, Math.min(config.maxDifficulty, difficulty));
    }

    public void resetDifficulty() {
        setDifficulty(config.defaultDifficulty);
    }

    public void modifyDifficulty(double amt) {
        setDifficulty(this.difficulty + amt);
    }

    protected Location<World> getRandomDest() {
        return Probability.pickOneOf(destinations);
    }

    public ZoneBoundingBox getDropRegion() {
        return drops;
    }

    @Override
    public void run() {
        if ((!isBossSpawned() && hasBossBeenKilled()) || emptyTicks > 60) {
            if (System.currentTimeMillis() - lastDeath >= 1000 * 60 * 5) {
                expire();
            }
        } else {
            if (isEmpty()) {
                ++emptyTicks;
            } else {
                emptyTicks = 0;
                ++activeTicks;

                if (activeTicks % 8 == 0) {
                    teleportRandom();
                    freezeEntities();
                    freezeBlocks(Probability.getChance((int) Math.ceil(config.iceChangeChance - difficulty)));
                    spawnCreatures();
                    printBossHealth();
                }

                if (activeTicks % 20 == 0) {
                    runAttack();
                }
            }
        }
    }

    public boolean isBossSpawned() {
        getContained(Zombie.class).stream().filter(Entity::isLoaded).filter(z -> !((EntityZombie) z).isChild()).forEach(e -> {
            Optional<Boss<Zombie, ZoneBossDetail<PatientXInstance>>> b = bossManager.updateLookup(e);
            if (b == null) {
                e.remove();
            }
        });
        return boss != null && boss.getTargetEntity().isPresent();
    }

    public boolean hasBossBeenKilled() {
        return lastDeath != 0;
    }

    public void spawnBoss() {
        resetDifficulty();
        freezeBlocks(false);

        Optional<Entity> spawned = getRegion().getExtent().createEntity(EntityTypes.ZOMBIE, getRegion().getCenter());
        if (spawned.isPresent()) {
            getRegion().getExtent().spawnEntity(spawned.get(), Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());

            Boss<Zombie, ZoneBossDetail<PatientXInstance>> boss = new Boss<>((Zombie) spawned.get(), new ZoneBossDetail<>(this));
            bossManager.bind(boss);
            this.boss = boss;
        }
    }

    public Location<World> getCenter() {
        Vector3i min = getRegion().getMinimumPoint();
        return new Location<>(getRegion().getExtent(), min.getX() + 34.5, min.getY() + 25, min.getZ() + 34.5);
    }

    public Optional<Zombie> getBoss() {
        return isBossSpawned() ? boss.getTargetEntity() : Optional.empty();
    }

    public void healBoss(float percentHealth) {
        Optional<Zombie> optBoss = getBoss();
        if (optBoss.isPresent()) {
            Zombie boss = optBoss.get();
            heal(boss, getMaxHealth(boss) * percentHealth);
        }
    }

    public void bossDied() {
        lastDeath = System.currentTimeMillis();
        boss = null;
    }

    protected void teleportRandom() {
        teleportRandom(false);
    }

    protected void teleportRandom(boolean force) {
        long diff = System.currentTimeMillis() - lastTelep;
        if (!force) {
            if (!Probability.getChance(4) || (lastTelep != 0 && diff < 8000)) return;
        }

        lastTelep = System.currentTimeMillis();

        getBoss().get().setLocation(getRandomDest());
        sendAttackBroadcast("Pause for a second chap, I need to answer the teleport!", AttackSeverity.INFO);
    }

    private void freezeEntities() {
        Zombie boss = getBoss().get();

        double total = 0;
        for (Living entity : getContained(Living.class)) {
            if (entity.equals(boss)) continue;
            BlockType curType = entity.getLocation().getBlockType();
            if (curType != BlockTypes.WATER && curType != BlockTypes.FLOWING_WATER) {
                continue;
            }
            if (entity instanceof Zombie) {
                entity.offer(Keys.HEALTH, 0D);
                EntityHealthUtil.heal(boss, 1);
                total += .02;
            } else if (!Probability.getChance(5)) {
                entity.damage(Probability.getRandom(25), EntityDamageSource.builder().entity(boss).type(DamageTypes.MAGIC).build());
            }
        }
        modifyDifficulty(-total);
    }

    public void freezeBlocks(boolean throwExplosives) {
        freezeBlocks(config.iceChance, throwExplosives);
    }

    public void freezeBlocks(int percentage, boolean throwExplosives) {
        ice.forAll((pt) -> {
            BlockType aboveType = getRegion().getExtent().getBlockType(pt.add(0, 1, 0));
            BlockType belowType = getRegion().getExtent().getBlockType(pt.add(0, -1, 0));
            if (aboveType == BlockTypes.AIR && belowType == BlockTypes.WATER || belowType == BlockTypes.FLOWING_WATER) {
                if (percentage >= 100) {
                    getRegion().getExtent().setBlockType(
                            pt, BlockTypes.ICE, Cause.source(SkreePlugin.container()).build()
                    );
                    return;
                }

                BlockType curType = getRegion().getExtent().getBlockType(pt);

                if (curType == BlockTypes.PACKED_ICE || curType == BlockTypes.ICE) {
                    getRegion().getExtent().setBlockType(
                            pt, BlockTypes.WATER, Cause.source(SkreePlugin.container()).build()
                    );
                    if (!Probability.getChance(config.snowBallChance) || !throwExplosives) return;
                    Location target = new Location<>(getRegion().getExtent(), pt.add(0, 1, 0));
                    for (int i = Probability.getRandom(3); i > 0; i--) {
                        Optional<Entity> optEnt = getRegion().getExtent().createEntity(EntityTypes.SNOWBALL, target.getPosition());
                        if (optEnt.isPresent()) {
                            Snowball melivn = (Snowball) optEnt.get();
                            melivn.setVelocity(new Vector3d(
                                    0,
                                    Probability.getRangedRandom(.25, 1),
                                    0
                            ));
                            getRegion().getExtent().spawnEntity(melivn, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
                        }
                    }
                } else if (Probability.getChance(percentage, 100)) {
                    getRegion().getExtent().setBlockType(
                            pt, BlockTypes.PACKED_ICE, Cause.source(SkreePlugin.container()).build()
                    );
                }
            }
        });
    }

    public void spawnCreatures() {
        Zombie boss = getBoss().get();

        Collection<Living> entities = getContained(Living.class);
        if (entities.size() > 500) {
            sendAttackBroadcast("Ring-a-round the rosie, a pocket full of posies...", AttackSeverity.NORMAL);
            EntityHealthUtil.toFullHealth(boss);
            for (Entity entity : entities) {
                if (entity instanceof Player) {
                    entity.offer(Keys.HEALTH, 0D);
                } else if (!entity.equals(boss)) {
                    entity.remove();
                }
            }
            return;
        }

        double amt = getPlayers(PARTICIPANT).size() * difficulty;
        Location l = getCenter();
        for (int i = 0; i < amt; i++) {
            Optional<Entity> optEnt = getRegion().getExtent().createEntity(EntityTypes.ZOMBIE, l.getPosition());
            if (optEnt.isPresent()) {
                Entity zombie = optEnt.get();
                // TODO convert to Sponge Data API
                ((EntityZombie) zombie).setCanPickUpLoot(false);
                ((EntityZombie) zombie).setChild(true);
                getRegion().getExtent().spawnEntity(zombie, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
            }
        }
    }

    public Optional<PatientXAttack> getLastAttack() {
        return System.currentTimeMillis() > attackDur ? Optional.empty() : Optional.ofNullable(lastAttack);
    }

    private final EntityHealthPrinter healthPrinter = new EntityHealthPrinter(
            CombinedText.of(
                    TextColors.DARK_AQUA,
                    "Boss Health: ",
                    new PlaceHolderText("health int"),
                    " / ",
                    new PlaceHolderText("max health int"),
                    " Enragement: ",
                    new GeneratedText() {
                        @Override
                        public Text getText() {
                            double maxDiff = config.maxDifficulty - config.minDifficulty;
                            double curDiff = difficulty - config.minDifficulty;
                            return Text.of((int) Math.round((curDiff / maxDiff) * 100));
                        }
                    },
                    "%"
            ),
            null
    );

    public void printBossHealth() {
        Optional<Zombie> optBoss = getBoss();
        if (!optBoss.isPresent()) {
            return;
        }

        healthPrinter.print(getPlayerMessageChannel(SPECTATOR), optBoss.get());
    }

    @Override
    public boolean init() {
        setUp();
        remove();
        spawnBoss();
        return true;
    }

    @Override
    public void forceEnd() {
        remove(getPlayers(PARTICIPANT));
        remove();
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        // TODO convert to Sponge
        do {
            player.setLocation(getRandomDest());
        } while (getBoss() != null && ((EntityZombie) getBoss().get()).canEntityBeSeen(tf(player)));

        player.sendMessage(Text.of(TextColors.YELLOW, "It's been a long time since I had a worthy opponent..."));
        player.sendMessage(Text.of(TextColors.YELLOW, "Let's see if you have what it takes..."));

        return new Clause<>(player, ZoneStatus.ADDED);
    }

    protected enum AttackSeverity {
        INFO,
        NORMAL,
        ULTIMATE
    }

    protected void sendAttackBroadcast(String message, AttackSeverity severity) {
        TextColor color;
        switch (severity) {
            case INFO:
                color = TextColors.YELLOW;
                break;
            case ULTIMATE:
                color = TextColors.DARK_RED;
                break;
            default:
                color = TextColors.RED;
                break;
        }
        getPlayerMessageChannel(SPECTATOR).send(Text.of(color, message));
    }

    public void runAttack() {
        runAttack(Probability.pickOneOf(PatientXAttack.values()));
    }

    public void runAttack(PatientXAttack attackCase) {
        Optional<Zombie> optBoss = getBoss();

        if (!optBoss.isPresent()) {
            return;
        }

        Zombie boss = optBoss.get();

        Collection<Player> contained = getPlayers(PARTICIPANT);
        if (contained.isEmpty()) {
            return;
        }

        switch (attackCase) {
            case MUSICAL_CHAIRS:
                sendAttackBroadcast("Let's play musical chairs!", AttackSeverity.NORMAL);
                for (Player player : contained) {
                    do {
                        player.setLocation(getRandomDest());
                    } while (player.getLocation().getPosition().distanceSquared(boss.getLocation().getPosition()) <= 5 * 5);
                    // TODO convert to Sponge
                    if (((EntityZombie) boss).canEntityBeSeen(tf(player))) {
                        player.offer(Keys.HEALTH, Probability.getRandom(player.get(Keys.MAX_HEALTH).get()));
                        sendAttackBroadcast("Don't worry, I have a medical degree...", AttackSeverity.NORMAL);
                        sendAttackBroadcast("...or was that a certificate of insanity?", AttackSeverity.NORMAL);
                    }
                }
                attackDur = System.currentTimeMillis() + 2000;
                break;
            case SMASHING_HIT:
                for (Player player : contained) {
                    final double old = player.get(Keys.HEALTH).get();
                    player.offer(Keys.HEALTH, 3D);
                    Task.builder().execute(() -> {
                        if (!player.isRemoved() || !contains(player)) return;
                        player.offer(Keys.HEALTH, old * .75);
                    }).delay(2, TimeUnit.SECONDS).submit(SkreePlugin.inst());
                }
                attackDur = System.currentTimeMillis() + 3000;
                sendAttackBroadcast("This special attack will be a \"smashing hit\"!", AttackSeverity.NORMAL);
                break;
            case BOMB_PERFORMANCE:
                double tntQuantity = Math.max(2, difficulty / 2.4);
                for (Player player : contained) {
                    for (double i = Probability.getRangedRandom(tntQuantity, Math.pow(2, Math.min(9, tntQuantity))); i > 0; i--) {
                        Optional<Entity> optEntity = getRegion().getExtent().createEntity(EntityTypes.PRIMED_TNT, player.getLocation().getPosition());
                        if (optEntity.isPresent()) {
                            PrimedTNT explosive = (PrimedTNT) optEntity.get();
                            explosive.setVelocity(new Vector3d(
                                    random.nextDouble() * 1 - .5,
                                    random.nextDouble() * .8 + .2,
                                    random.nextDouble() * 1 - .5
                            ));
                            explosive.offer(Keys.FUSE_DURATION, 20 * 4);
                            getRegion().getExtent().spawnEntity(explosive, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
                        }
                    }
                }
                attackDur = System.currentTimeMillis() + 5000;
                sendAttackBroadcast("Your performance is really going to \"bomb\"!", AttackSeverity.NORMAL);
                break;
            case WITHER_AWAY:
                PotionEffect witherEffect = PotionEffect.of(PotionEffectTypes.WITHER, 1, 20 * 15);
                for (Player player : contained) {
                    List<PotionEffect> potionEffects = player.getOrElse(Keys.POTION_EFFECTS, new ArrayList<>(1));
                    potionEffects.add(witherEffect);
                    player.offer(Keys.POTION_EFFECTS, potionEffects);
                }
                attackDur = System.currentTimeMillis() + 15750;
                sendAttackBroadcast("Like a candle I hope you don't \"whither\" and die!", AttackSeverity.NORMAL);
                break;
            case SPLASH_TO_IT:
                for (Player player : contained) {
                    for (int i = Probability.getRandom(6) + 2; i > 0; --i) {
                        throwSlashPotion(player.getLocation());
                    }
                }
                attackDur = System.currentTimeMillis() + 2000;
                sendAttackBroadcast("Splash to it!", AttackSeverity.NORMAL);
                break;
            case COLD_FEET:
                PotionEffect slowEffect = PotionEffect.of(PotionEffectTypes.SLOWNESS, 2, 20 * 60);
                for (Player player : contained) {
                    List<PotionEffect> potionEffects = player.getOrElse(Keys.POTION_EFFECTS, new ArrayList<>(1));
                    potionEffects.add(slowEffect);
                    player.offer(Keys.POTION_EFFECTS, potionEffects);
                }
                attackDur = System.currentTimeMillis() + 20000;
                sendAttackBroadcast("What's the matter, got cold feet?", AttackSeverity.NORMAL);
                break;
            case IM_JUST_BATTY:
                for (Player player : contained) {
                    // player.chat("I love Patient X!");
                    Optional<Entity> optEntity = getRegion().getExtent().createEntity(EntityTypes.BAT, player.getLocation().getPosition());
                    if (optEntity.isPresent()) {
                        getRegion().getExtent().spawnEntity(optEntity.get(), Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
                        optEntity.get().getPassengers().add(player);
                    }
                }
                attackDur = System.currentTimeMillis() + 20000;
                sendAttackBroadcast("Awe, I love you too!", AttackSeverity.NORMAL);
                sendAttackBroadcast("But only cause I'm a little batty...", AttackSeverity.NORMAL);
                break;
            case RADIATION:
                ParticleEffect radiationEffect = ParticleEffect.builder().type(
                        ParticleTypes.FLAME
                ).count(1).build();

                Task.builder().execute(() -> {
                    for (int i = config.radiationTimes; i > 0; i--) {
                        Task.builder().execute(() -> {
                            if (isBossSpawned()) {
                                for (Player player : getPlayers(PlayerClassifier.PARTICIPANT)) {
                                    for (int e = 0; e < 30; ++e) {
                                        getRegion().getExtent().spawnParticles(radiationEffect, player.getLocation().getPosition().add(
                                            5 - Probability.getRandom(10) + Probability.getRangedRandom(0, 1.0),
                                            5 - Probability.getRandom(10) + Probability.getRangedRandom(0, 1.0),
                                            5 - Probability.getRandom(10) + Probability.getRangedRandom(0, 1.0)
                                        ));
                                    }
                                    if (LightLevelUtil.getMaxLightLevel(player.getLocation()).get() >= config.radiationLightLevel) {
                                        player.damage(difficulty * config.radiationMultiplier, EntityDamageSource.builder().entity(boss).type(DamageTypes.MAGIC).build());
                                    }
                                }
                            }
                        }).delay(i * 500, TimeUnit.MILLISECONDS).submit(SkreePlugin.inst());
                    }
                }).delay(3, TimeUnit.SECONDS).submit(SkreePlugin.inst());
                attackDur = System.currentTimeMillis() + (config.radiationTimes * 500);
                sendAttackBroadcast("Ahhh not the radiation treatment!", AttackSeverity.NORMAL);
                break;
            case SNOWBALL_FIGHT:
                final int burst = Probability.getRangedRandom(10, 20);
                Task.builder().execute(() -> {
                    for (int i = burst; i > 0; i--) {
                        Task.builder().execute(() -> {
                            if (boss != null) freezeBlocks(true);
                        }).delay(i * 500, TimeUnit.MILLISECONDS).submit(SkreePlugin.inst());
                    }
                }).delay(7, TimeUnit.SECONDS).submit(SkreePlugin.inst());
                attackDur = System.currentTimeMillis() + 7000 + (500 * burst);
                sendAttackBroadcast("Let's have a snow ball fight!", AttackSeverity.NORMAL);
                break;
        }
        lastAttack = attackCase;
    }

    private void throwSlashPotion(Location<World> location) {

        PotionEffectType[] thrownTypes = new PotionEffectType[]{
                PotionEffectTypes.INSTANT_DAMAGE, PotionEffectTypes.INSTANT_DAMAGE,
                PotionEffectTypes.POISON, PotionEffectTypes.WEAKNESS
        };

        Optional<Entity> optEntity = location.getExtent().createEntity(EntityTypes.SPLASH_POTION, location.getPosition());
        if (optEntity.isPresent()) {
            PotionEffectType type = Probability.pickOneOf(thrownTypes);
            PotionEffect effect = PotionEffect.of(type, 2, type.isInstant() ? 1 : 15 * 20);

            Entity entity = optEntity.get();
            entity.setVelocity(new Vector3d(
                    random.nextDouble() * .5 - .25,
                    random.nextDouble() * .4 + .1,
                    random.nextDouble() * .5 - .25
            ));
            entity.offer(Keys.POTION_EFFECTS, Lists.newArrayList(effect));

            getRegion().getExtent().spawnEntity(entity, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
        }
    }
}
