/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.entity.EntityHealthPrinter;
import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.text.CombinedText;
import com.skelril.nitro.text.PlaceHolderText;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimeFilter;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossManager;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.service.internal.zone.Zone;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityZombie;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Giant;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.skelril.nitro.entity.EntityHealthUtil.*;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.SPECTATOR;

public class ShnugglesPrimeInstance extends LegacyZoneBase implements Zone, Runnable {

    private final BossManager<Giant, ZoneBossDetail<ShnugglesPrimeInstance>> bossManager;

    private Boss<Giant, ZoneBossDetail<ShnugglesPrimeInstance>> boss = null;
    private long lastAttackTime = 0;
    private ShnugglesPrimeAttack lastAttack = null;
    private long lastDeath = 0;
    private boolean damageHeals = false;
    private Set<ShnugglesPrimeAttack> activeAttacks = EnumSet.noneOf(ShnugglesPrimeAttack.class);
    private Random random = new Random();

    private int emptyTicks = 0;

    private double toHeal = 0;
    private List<Location<World>> spawnPts = new ArrayList<>();

    public ShnugglesPrimeInstance(ZoneRegion region, BossManager<Giant, ZoneBossDetail<ShnugglesPrimeInstance>> bossManager) {
        super(region);
        this.bossManager = bossManager;
    }

    public void probeArea() {
        spawnPts.clear();
        Vector3i min = getRegion().getMinimumPoint();
        Vector3i max = getRegion().getMaximumPoint();
        int minX = min.getX();
        int minZ = min.getZ();
        int minY = min.getY();
        int maxX = max.getX();
        int maxZ = max.getZ();
        int maxY = max.getY();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = maxY; y >= minY; --y) {
                    BlockType type = getRegion().getExtent().getBlockType(x, y, z);
                    if (type == BlockTypes.GOLD_BLOCK) {
                        Location<World> target = new Location<>(getRegion().getExtent(), x, y + 2, z);
                        if (target.getBlockType() == BlockTypes.AIR) {
                            spawnPts.add(target);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        if (lastDeath != 0 || emptyTicks > 60) {
            if (System.currentTimeMillis() - lastDeath >= 1000 * 60 * 3) {
                expire();
            }
        } else {
            if (isEmpty()) {
                ++emptyTicks;
            } else {
                emptyTicks = 0;
                runRandomAttack();
            }
        }
    }

    public void buffBabies() {
        PotionEffect strengthBuff = PotionEffect.of(PotionEffectTypes.STRENGTH, 3, 20 * 20);
        for (Entity zombie : getContained(Zombie.class)) {
            zombie.offer(Keys.POTION_EFFECTS, Lists.newArrayList(strengthBuff));
        }
    }

    public void spawnBoss() {
        Optional<Entity> spawned = getRegion().getExtent().createEntity(EntityTypes.GIANT, getRegion().getCenter());
        if (spawned.isPresent()) {
            getRegion().getExtent().spawnEntity(spawned.get(), Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());

            Boss<Giant, ZoneBossDetail<ShnugglesPrimeInstance>> boss = new Boss<>((Giant) spawned.get(), new ZoneBossDetail<>(this));
            bossManager.bind(boss);
            this.boss = boss;
        }
    }


    public boolean isBossSpawned() {
        getContained(Giant.class).stream().filter(e -> e.isLoaded() && !e.isRemoved()).forEach(e -> {
            Optional<Boss<Giant, ZoneBossDetail<ShnugglesPrimeInstance>>> b = bossManager.updateLookup(e);
            if (!b.isPresent()) {
                e.remove();
            }
        });
        return boss != null && boss.getTargetEntity().isPresent();
    }

    public Optional<Giant> getBoss() {
        return isBossSpawned() ? boss.getTargetEntity() : Optional.empty();
    }

    public void healBoss(float percentHealth) {
        Optional<Giant> optBoss = getBoss();
        if (optBoss.isPresent()) {
            Giant boss = optBoss.get();
            heal(boss, getMaxHealth(boss) * percentHealth);
        }
    }

    public void bossDied() {
        activeAttacks.clear();
        lastDeath = System.currentTimeMillis();
        boss = null;
    }

    public boolean damageHeals() {
        return damageHeals;
    }

    public Optional<ShnugglesPrimeAttack> getLastAttack() {
        return lastAttackTime + 13000 > System.currentTimeMillis() ? Optional.ofNullable(lastAttack) : Optional.empty();
    }

    public boolean isActiveAttack(ShnugglesPrimeAttack attack) {
        return activeAttacks.contains(attack);
    }

    private final EntityHealthPrinter healthPrinter = new EntityHealthPrinter(
            CombinedText.of(
                    TextColors.DARK_AQUA,
                    "Boss Health: ",
                    new PlaceHolderText("health int"),
                    " / ",
                    new PlaceHolderText("max health int")
            ),
            null
    );

    public void printBossHealth() {
        Optional<Giant> optBoss = getBoss();
        if (!optBoss.isPresent()) {
            return;
        }

        healthPrinter.print(getPlayerMessageChannel(SPECTATOR), optBoss.get());
    }

    public void spawnMinions(@Nullable Living target) {
        int spawnCount = Math.max(3, getPlayers(PARTICIPANT).size());
        for (Location<World> spawnPt : spawnPts) {
            if (Probability.getChance(11)) {
                for (int i = spawnCount; i > 0; --i) {
                    Optional<Entity> spawned = getRegion().getExtent().createEntity(EntityTypes.ZOMBIE, spawnPt.getPosition());
                    if (spawned.isPresent()) {
                        Zombie zombie = (Zombie) spawned.get();
                        // TODO convert to Sponge Data API
                        ((EntityZombie) zombie).setChild(true);
                        EntityHealthUtil.setMaxHealth(zombie, 1);
                        getRegion().getExtent().spawnEntity(zombie, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());

                        if (target != null) {
                            zombie.setTarget(target);
                        }
                    }
                }
            }
        }
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
    
    public void runRandomAttack() {
        runAttack(Probability.pickOneOf(ShnugglesPrimeAttack.values()));
    }

    public void runAttack(ShnugglesPrimeAttack attackCase) {
        Optional<Giant> optBoss = getBoss();

        if (!optBoss.isPresent()) {
            return;
        }

        Giant boss = optBoss.get();

        double bossHealth = getHealth(boss);
        double maxBossHealth = getMaxHealth(boss);

        double delay = 8500;
        if (lastAttackTime != 0 && System.currentTimeMillis() - lastAttackTime <= delay) {
            return;
        }

        Collection<Player> contained = getPlayers(PARTICIPANT);
        if (contained.isEmpty()) {
            return;
        }

        // AI-ish system
        if ((attackCase == ShnugglesPrimeAttack.EVERLASTING || attackCase == ShnugglesPrimeAttack.MINION_LEECH) && bossHealth > maxBossHealth * .9) {
            attackCase = Probability.getChance(2) ? ShnugglesPrimeAttack.DARK_POTIONS : ShnugglesPrimeAttack.CORRUPTION;
        }
        for (Player player : contained) {
            if (player.get(Keys.HEALTH).get() < 4) {
                attackCase = ShnugglesPrimeAttack.CORRUPTION;
                break;
            }
        }
        Collection<Zombie> zombies = getContained(Zombie.class);
        if (zombies.size() > 200) {
            attackCase = ShnugglesPrimeAttack.BASK_IN_MY_GLORY;
        }
        if (bossHealth < maxBossHealth * .4 && Probability.getChance(5)) {
            if (zombies.size() < 100 && bossHealth > 200) {
                attackCase = ShnugglesPrimeAttack.EVERLASTING;
            } else {
                attackCase = ShnugglesPrimeAttack.MINION_LEECH;
            }
        }
        if ((attackCase == ShnugglesPrimeAttack.BLINDNESS || attackCase == ShnugglesPrimeAttack.FIRE) && bossHealth < maxBossHealth * .15) {
            runRandomAttack();
            return;
        }

        switch (attackCase) {
            case WRATH:
                sendAttackBroadcast("Taste my wrath!", AttackSeverity.NORMAL);
                for (Player player : contained) {
                    player.offer(Keys.VELOCITY,
                            new Vector3d(
                                    random.nextDouble() * 3 - 1.5,
                                    random.nextDouble() * 1 + .5,
                                    random.nextDouble() * 3 - 1.5
                            )
                    );
                    player.offer(Keys.FIRE_TICKS, 20 * 3);
                }
                break;
            case CORRUPTION:
                sendAttackBroadcast("Embrace my corruption!", AttackSeverity.NORMAL);
                PotionEffect witherEffect = PotionEffect.of(PotionEffectTypes.WITHER, 1, 20 * 3);
                for (Player player : contained) {
                    List<PotionEffect> potionEffects = player.getOrElse(Keys.POTION_EFFECTS, new ArrayList<>(1));
                    potionEffects.add(witherEffect);
                    player.offer(Keys.POTION_EFFECTS, potionEffects);
                }
                break;
            case BLINDNESS:
                sendAttackBroadcast("Are you BLIND? Mwhahahaha!", AttackSeverity.NORMAL);
                PotionEffect blindnessEffect = PotionEffect.of(PotionEffectTypes.BLINDNESS, 0, 20 * 4);

                for (Player player : contained) {
                    List<PotionEffect> potionEffects = player.getOrElse(Keys.POTION_EFFECTS, new ArrayList<>(1));
                    potionEffects.add(blindnessEffect);
                    player.offer(Keys.POTION_EFFECTS, potionEffects);
                }
                break;
            case TANGO_TIME:
                sendAttackBroadcast("Tango time!", AttackSeverity.ULTIMATE);
                activeAttacks.add(ShnugglesPrimeAttack.TANGO_TIME);
                Task.builder().delay(7, TimeUnit.SECONDS).execute(() -> {
                    if (!isBossSpawned()) return;
                    for (Player player : getPlayers(PARTICIPANT)) {
                        // TODO Convert to Sponge
                        if (((EntityGiantZombie) boss).canEntityBeSeen(tf(player))) {
                            player.sendMessage(Text.of(TextColors.YELLOW, "Come closer..."));
                            player.setLocation(boss.getLocation());


                            EntityDamageSource source = EntityDamageSource.builder().entity(
                                    boss
                            ).type(
                                    DamageTypes.ATTACK
                            ).build();

                            player.damage(100, source, Cause.source(boss).build());
                            player.offer(Keys.VELOCITY,
                                    new Vector3d(
                                            random.nextDouble() * 1.7 - 1.5,
                                            random.nextDouble() * 2,
                                            random.nextDouble() * 1.7 - 1.5
                                    )
                            );
                        } else {
                            player.sendMessage(Text.of(TextColors.YELLOW, "Fine... No tango this time..."));
                        }
                    }
                    sendAttackBroadcast("Now wasn't that fun?", AttackSeverity.INFO);
                    activeAttacks.remove(ShnugglesPrimeAttack.TANGO_TIME);
                }).submit(SkreePlugin.inst());
                break;
            case EVERLASTING:
                if (!damageHeals) {
                    activeAttacks.add(ShnugglesPrimeAttack.EVERLASTING);
                    sendAttackBroadcast("I am everlasting!", AttackSeverity.NORMAL);
                    damageHeals = true;
                    Task.builder().delay(12, TimeUnit.SECONDS).execute(() -> {
                        if (damageHeals) {
                            damageHeals = false;
                            if (!isBossSpawned()) return;
                            sendAttackBroadcast("Thank you for your assistance.", AttackSeverity.INFO);
                        }
                        activeAttacks.remove(ShnugglesPrimeAttack.EVERLASTING);
                    }).submit(SkreePlugin.inst());
                    break;
                }
                runRandomAttack();
                return;
            case FIRE:
                sendAttackBroadcast("Fire is your friend...", AttackSeverity.NORMAL);
                for (Player player : contained) {
                    player.offer(Keys.FIRE_TICKS, 20 * 5);
                }
                break;
            case BASK_IN_MY_GLORY:
                if (!damageHeals) {
                    sendAttackBroadcast("Bask in my glory!", AttackSeverity.ULTIMATE);
                    activeAttacks.add(ShnugglesPrimeAttack.BASK_IN_MY_GLORY);
                    Task.builder().delay(7, TimeUnit.SECONDS).execute(() -> {
                        if (!isBossSpawned()) return;

                        boolean baskInGlory = false;
                        for (Player player : getContained(Player.class)) {
                            // TODO Convert to Sponge
                            if (((EntityGiantZombie) boss).canEntityBeSeen(tf(player))) {
                                player.sendMessage(Text.of(TextColors.DARK_RED, "You!"));
                                baskInGlory = true;
                            }
                        }

                        //Attack
                        if (baskInGlory) {
                            damageHeals = true;
                            spawnPts.stream().filter(pt -> Probability.getChance(12)).forEach(pt -> {
                                Explosion explosion = Explosion.builder()
                                        .shouldBreakBlocks(false)
                                        .location(pt)
                                        .radius(10)
                                        .build();
                                
                                getRegion().getExtent().triggerExplosion(explosion);
                            });
                            //Schedule Reset
                            Task.builder().delay(500, TimeUnit.MILLISECONDS).execute(() -> {
                                damageHeals = false;
                            }).submit(SkreePlugin.inst());
                            return;
                        }
                        // Notify if avoided
                        sendAttackBroadcast("Gah... Afraid are you friends?", AttackSeverity.INFO);
                        activeAttacks.remove(ShnugglesPrimeAttack.BASK_IN_MY_GLORY);
                    }).submit(SkreePlugin.inst());
                    break;
                }
                runRandomAttack();
                break;
            case DARK_POTIONS:
                sendAttackBroadcast("Unleash the inner darkness!", AttackSeverity.NORMAL);
                PotionEffect instantDamageEffect = PotionEffect.of(PotionEffectTypes.INSTANT_DAMAGE, 0, 1);
                for (Living entity : getContained(Zombie.class)) {
                    if (!Probability.getChance(5)) {
                        continue;
                    }

                    entity.offer(Keys.HEALTH, 0D);

                    Location targetLoc = entity.getLocation();
                    Optional<Entity> optEntity = getRegion().getExtent().createEntity(
                            EntityTypes.SPLASH_POTION,
                            targetLoc.getPosition()
                    );

                    if (optEntity.isPresent()) {
                        Entity potion = optEntity.get();
                        potion.offer(Keys.POTION_EFFECTS, Lists.newArrayList(instantDamageEffect));
                        getRegion().getExtent().spawnEntity(potion, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
                    }
                }
                return;
            case MINION_LEECH:
                sendAttackBroadcast("My minions our time is now!", AttackSeverity.ULTIMATE);
                activeAttacks.add(ShnugglesPrimeAttack.MINION_LEECH);
                IntegratedRunnable minionEater = new IntegratedRunnable() {
                    @Override
                    public boolean run(int times) {
                        if (!isBossSpawned()) return true;
                        for (Living entity : getContained(Living.class)) {
                            // TODO convert to Sponge
                            if (entity instanceof Giant || !Probability.getChance(5) || !((EntityGiantZombie) boss).canEntityBeSeen((EntityLiving) entity)) {
                                continue;
                            }

                            double realDamage = entity.get(Keys.HEALTH).get();
                            // TODO convert to Sponge
                            if (entity instanceof Zombie && ((EntityZombie) entity).isChild()) {
                                entity.offer(Keys.HEALTH, 0D);
                            } else {
                                DamageSource source = DamageSource.builder().type(
                                        DamageTypes.ATTACK
                                ).build();

                                entity.damage(realDamage, source, Cause.source(boss).build());
                            }
                            toHeal += realDamage / 3;
                        }
                        if (new TimeFilter(-1, 2).matchesFilter(times + 1)) {
                            getPlayerMessageChannel(SPECTATOR).send(
                                    Text.of(
                                            TextColors.DARK_AQUA,
                                            "The boss has drawn in: " + (int) toHeal + " health."
                                    )
                            );
                        }
                        return true;
                    }

                    @Override
                    public void end() {
                        if (!isBossSpawned()) return;
                        heal(boss, toHeal);
                        toHeal = 0;
                        sendAttackBroadcast("Thank you my minions!", AttackSeverity.INFO);
                        printBossHealth();
                        activeAttacks.remove(ShnugglesPrimeAttack.MINION_LEECH);
                    }
                };
                TimedRunnable<IntegratedRunnable> minonEatingTask = new TimedRunnable<>(minionEater, 20);
                Task minionEatingTaskExecutor = Task.builder().interval(500, TimeUnit.MILLISECONDS)
                        .execute(minonEatingTask).submit(SkreePlugin.inst());
                minonEatingTask.setTask(minionEatingTaskExecutor);
                break;
        }
        lastAttackTime = System.currentTimeMillis();
        lastAttack = attackCase;
    }

    @Override
    public boolean init() {
        probeArea();
        remove();
        Task.builder().delayTicks(1).execute(this::spawnBoss).submit(SkreePlugin.inst());
        return true;
    }

    @Override
    public void forceEnd() {
        remove(getPlayers(PARTICIPANT));
        remove();
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        player.setLocation(Probability.pickOneOf(spawnPts));
        player.getInventory().offer(tf(new net.minecraft.item.ItemStack(CustomItemTypes.ZONE_TRANSITIONAL_ORB)));
        tf(player).inventoryContainer.detectAndSendChanges();
        return new Clause<>(player, ZoneStatus.ADDED);
    }
}
