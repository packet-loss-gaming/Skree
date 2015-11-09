/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.item.ItemStackFactory;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimeFilter;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossManager;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.zone.Zone;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Giant;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.potion.PotionEffect;
import org.spongepowered.api.potion.PotionEffectTypes;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.skelril.nitro.entity.EntityHealthUtil.*;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.SPECTATOR;

public class ShnugglesPrimeInstance extends LegacyZoneBase implements Zone, Runnable {

    private final BossManager<Giant, ZoneBossDetail<ShnugglesPrimeInstance>> bossManager;

    private Optional<Boss<Giant, ZoneBossDetail<ShnugglesPrimeInstance>>> boss = Optional.empty();
    private long lastAttack = 0;
    private int lastAttackNumber = -1;
    private long lastDeath = 0;
    private boolean damageHeals = false;
    private Set<Integer> activeAttacks = new HashSet<>();
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
        PotionEffect strengthBuff = SkreePlugin.inst().getGame().getRegistry().createPotionEffectBuilder()
                .duration(20 * 20).amplifier(3).potionType(PotionEffectTypes.STRENGTH).build();
        for (Entity zombie : getContained(Zombie.class)) {
            zombie.offer(Keys.POTION_EFFECTS, Lists.newArrayList(strengthBuff));
        }
    }

    public void spawnBoss() {
        Optional<Entity> spawned = getRegion().getExtent().createEntity(EntityTypes.GIANT, getRegion().getCenter());
        if (spawned.isPresent()) {
            getRegion().getExtent().spawnEntity(spawned.get(), Cause.of(this));
            boss = Optional.of(new Boss<>((Giant) spawned.get(), new ZoneBossDetail<>(this)));
        }
    }


    public boolean isBossSpawned() {
        getContained(Giant.class).stream().filter(e -> e.isLoaded() && !e.isRemoved()).forEach(e -> {
            Optional<Boss<Giant, ZoneBossDetail<ShnugglesPrimeInstance>>> b = bossManager.updateLookup(e);
            if (!b.isPresent()) {
                e.remove();
            }
        });
        return boss != null;
    }

    public Optional<Giant> getBoss() {
        return isBossSpawned() ? Optional.of(boss.get().getTargetEntity()) : Optional.empty();
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
        boss = Optional.empty();
    }

    public boolean damageHeals() {
        return damageHeals;
    }

    public int getLastAttack() {
        return lastAttack + 13000 > System.currentTimeMillis() ? lastAttackNumber : -1;
    }

    public boolean isActiveAttack(Integer attack) {
        return activeAttacks.contains(attack);
    }

    public void printBossHealth() {
        Optional<Giant> optBoss = getBoss();
        if (!optBoss.isPresent()) {
            return;
        }

        Giant boss = optBoss.get();
        int current = (int) Math.ceil(getHealth(boss));
        int max = (int) Math.ceil(getMaxHealth(boss));

        getPlayerMessageSink(SPECTATOR).sendMessage(
                Texts.of(TextColors.DARK_AQUA, "Boss Health: " + current + " / " + max)
        );
    }

    private static final ItemStack weapon = ItemStackFactory.newItemStack(ItemTypes.BONE);

    static {
        Optional<List<ItemEnchantment>> optEnchantments = weapon.get(Keys.ITEM_ENCHANTMENTS);
        if (optEnchantments.isPresent()) {
            List<ItemEnchantment> enchantments = optEnchantments.get();
            enchantments.add(new ItemEnchantment(Enchantments.SHARPNESS, 2));
            weapon.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
        }
    }

    public void spawnMinions(Optional<Living> target) {
        int spawnCount = Math.max(3, getPlayers(PARTICIPANT).size());
        for (Location<World> spawnPt : spawnPts) {
            if (Probability.getChance(11)) {
                for (int i = spawnCount; i > 0; --i) {
                    Optional<Entity> spawned = getRegion().getExtent().createEntity(EntityTypes.ZOMBIE, spawnPt.getPosition());
                    if (spawned.isPresent()) {
                        Zombie zombie = (Zombie) spawned.get();
                        // TODO convert to Sponge Data API
                        ((EntityZombie) zombie).setChild(true);
                        zombie.setItemInHand(weapon.copy());
                        getRegion().getExtent().spawnEntity(zombie, Cause.of(this));

                        if (target.isPresent()) {
                            zombie.offer(Keys.TARGETS, Lists.newArrayList(target.get()));
                        }
                    }
                }
            }
        }
    }

    private enum AttackSeverity {
        INFO,
        NORMAL,
        ULTIMATE
    }

    private void sendAttackBroadcast(String message, AttackSeverity severity) {
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
        getPlayerMessageSink(SPECTATOR).sendMessage(Texts.of(color, message));
    }

    private static final int OPTION_COUNT = 9;
    
    public void runRandomAttack() {
        runRandomAttack();
    }

    public void runAttack(int attackCase) {
        Optional<Giant> optBoss = getBoss();

        if (!optBoss.isPresent()) {
            return;
        }

        Giant boss = optBoss.get();

        double bossHealth = getHealth(boss);
        double maxBossHealth = getMaxHealth(boss);

        double delay = Math.max(5000, Probability.getRangedRandom(15 * bossHealth, 25 * bossHealth));
        if (lastAttack != 0 && System.currentTimeMillis() - lastAttack <= delay) {
            return;
        }

        Collection<Player> contained = getPlayers(PARTICIPANT);
        if (contained.isEmpty()) {
            return;
        }

        if (attackCase < 1 || attackCase > OPTION_COUNT) attackCase = Probability.getRandom(OPTION_COUNT);
        // AI-ish system
        if ((attackCase == 5 || attackCase == 9) && bossHealth > maxBossHealth * .9) {
            attackCase = Probability.getChance(2) ? 8 : 2;
        }
        for (Player player : contained) {
            if (player.get(Keys.HEALTH).get() < 4) {
                attackCase = 2;
                break;
            }
        }
        Collection<Zombie> zombies = getContained(Zombie.class);
        if (zombies.size() > 200) {
            attackCase = 7;
        }
        if (bossHealth < maxBossHealth * .4 && Probability.getChance(5)) {
            if (zombies.size() < 100 && bossHealth > 200) {
                attackCase = 5;
            } else {
                attackCase = 9;
            }
        }
        if ((attackCase == 3 || attackCase == 6) && bossHealth < maxBossHealth * .15) {
            runRandomAttack();
            return;
        }

        switch (attackCase) {
            case 1:
                sendAttackBroadcast("Taste my wrath!", AttackSeverity.NORMAL);
                for (Player player : contained) {
                    // TODO convert to Sponge
                    ((EntityPlayer) player).setVelocity(
                            random.nextDouble() * 3 - 1.5,
                            random.nextDouble() * 1 + 1.3,
                            random.nextDouble() * 3 - 1.5
                    );
                    ((EntityPlayer) player).setFire(3); // This is in seconds for some reason
                }
                break;
            case 2:
                sendAttackBroadcast("Embrace my corruption!", AttackSeverity.NORMAL);
                PotionEffect witherEffect = SkreePlugin.inst().getGame().getRegistry().createPotionEffectBuilder()
                        .duration(20 * 12).amplifier(1).potionType(PotionEffectTypes.WITHER).build();
                for (Player player : contained) {
                    Optional<List<PotionEffect>> optPotionEffects = player.get(Keys.POTION_EFFECTS);
                    if (!optPotionEffects.isPresent()) {
                        optPotionEffects = Optional.of(new ArrayList<>(1));
                    }
                    List<PotionEffect> potionEffects = optPotionEffects.get();
                    potionEffects.add(witherEffect);
                    player.offer(Keys.POTION_EFFECTS, potionEffects);
                }
                break;
            case 3:
                sendAttackBroadcast("Are you BLIND? Mwhahahaha!", AttackSeverity.NORMAL);
                PotionEffect blindnessEffect = SkreePlugin.inst().getGame().getRegistry().createPotionEffectBuilder()
                        .duration(20 * 4).amplifier(0).potionType(PotionEffectTypes.BLINDNESS).build();
                for (Player player : contained) {
                    Optional<List<PotionEffect>> optPotionEffects = player.get(Keys.POTION_EFFECTS);
                    if (!optPotionEffects.isPresent()) {
                        optPotionEffects = Optional.of(new ArrayList<>(1));
                    }
                    List<PotionEffect> potionEffects = optPotionEffects.get();
                    potionEffects.add(blindnessEffect);
                    player.offer(Keys.POTION_EFFECTS, potionEffects);
                }
                break;
            case 4:
                sendAttackBroadcast("Tango time!", AttackSeverity.ULTIMATE);
                activeAttacks.add(4);
                SkreePlugin.inst().getGame().getScheduler().createTaskBuilder().delay(7, TimeUnit.SECONDS).execute(() -> {
                    if (!isBossSpawned()) return;
                    for (Player player : getPlayers(PARTICIPANT)) {
                        // TODO Convert to Sponge
                        if (((EntityGiantZombie) boss).canEntityBeSeen((EntityPlayer) player)) {
                            player.sendMessage(Texts.of(TextColors.YELLOW, "Come closer..."));
                            player.setLocation(boss.getLocation());
                            player.damage(100, Cause.of(boss));
                            // TODO convert to Sponge
                            ((EntityPlayer) player).setVelocity(
                                    random.nextDouble() * 1.7 - 1.5,
                                    random.nextDouble() * 2,
                                    random.nextDouble() * 1.7 - 1.5
                            );
                        } else {
                            player.sendMessage(Texts.of(TextColors.YELLOW, "Fine... No tango this time..."));
                        }
                    }
                    sendAttackBroadcast("Now wasn't that fun?", AttackSeverity.INFO);
                    activeAttacks.remove(4);
                }).submit(SkreePlugin.inst());
                break;
            case 5:
                if (!damageHeals) {
                    activeAttacks.add(5);
                    sendAttackBroadcast("I am everlasting!", AttackSeverity.NORMAL);
                    damageHeals = true;
                    SkreePlugin.inst().getGame().getScheduler().createTaskBuilder().delay(12, TimeUnit.SECONDS).execute(() -> {
                        if (damageHeals) {
                            damageHeals = false;
                            if (!isBossSpawned()) return;
                            sendAttackBroadcast("Thank you for your assistance.", AttackSeverity.INFO);
                        }
                        activeAttacks.remove(5);
                    }).submit(SkreePlugin.inst());
                    break;
                }
                runRandomAttack();
                return;
            case 6:
                sendAttackBroadcast("Fire is your friend...", AttackSeverity.NORMAL);
                for (Player player : contained) {
                    // TODO convert to Sponge
                    ((EntityPlayer) player).setFire(30); // This is in seconds for some reason
                }
                break;
            case 7:
                if (!damageHeals) {
                    sendAttackBroadcast("Bask in my glory!", AttackSeverity.ULTIMATE);
                    activeAttacks.add(7);
                    SkreePlugin.inst().getGame().getScheduler().createTaskBuilder().delay(7, TimeUnit.SECONDS).execute(() -> {
                        if (!isBossSpawned()) return;

                        boolean baskInGlory = false;
                        for (Player player : getContained(Player.class)) {
                            // TODO Convert to Sponge
                            if (((EntityGiantZombie) boss).canEntityBeSeen((EntityPlayer) player)) {
                                player.sendMessage(Texts.of(TextColors.DARK_RED, "You!"));
                                baskInGlory = true;
                            }
                        }

                        //Attack
                        if (baskInGlory) {
                            damageHeals = true;
                            spawnPts.stream().filter(pt -> Probability.getChance(12)).forEach(pt -> {
                                Explosion explosion = SkreePlugin.inst().getGame().getRegistry()
                                        .createExplosionBuilder()
                                        .shouldBreakBlocks(false)
                                        .origin(pt.getPosition())
                                        .radius(10)
                                        .world(getRegion().getExtent())
                                        .build();
                                
                                getRegion().getExtent().triggerExplosion(explosion);
                            });
                            //Schedule Reset
                            SkreePlugin.inst().getGame().getScheduler().createTaskBuilder().delay(500, TimeUnit.MILLISECONDS).execute(() -> {
                                damageHeals = false;
                            }).submit(SkreePlugin.inst());
                            return;
                        }
                        // Notify if avoided
                        sendAttackBroadcast("Gah... Afraid are you friends?", AttackSeverity.INFO);
                        activeAttacks.remove(7);
                    }).submit(SkreePlugin.inst());
                    break;
                }
                runRandomAttack();
                break;
            case 8:
                runRandomAttack();
                return;
                /*
                // ChatUtil.sendWarning(spectator, ChatColor.DARK_RED + "I ask thy lord for aid in this all mighty battle...");
                // ChatUtil.sendWarning(spectator, ChatColor.DARK_RED + "Heed thy warning, or perish!");
                activeAttacks.add(8);
                SkreePlugin.inst().getGame().getScheduler().createTaskBuilder().delay(7, TimeUnit.SECONDS).execute(() -> {
                    if (!isBossSpawned()) return;
                    Collection<Player> newContained = getContained(Player.class);
                    // ChatUtil.sendWarning(newContained, "May those who appose me die a death like no other...");
                    // TODO convert to Sponge
                    newContained.stream().filter(e -> ((EntityGiantZombie) boss).canEntityBeSeen((EntityPlayer) e)).forEach(player -> {
                        // ChatUtil.sendWarning(newContained, "Perish " + player.getName() + "!");
                        // TODO Doom Prayer was used previously
                    });
                    activeAttacks.remove(8);
                }).submit(SkreePlugin.inst());
                break;
                */
            case 9:
                sendAttackBroadcast("My minions our time is now!", AttackSeverity.ULTIMATE);
                activeAttacks.add(9);
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
                                entity.damage(realDamage, Cause.of(boss));
                            }
                            toHeal += realDamage / 3;
                        }
                        if (new TimeFilter(-1, 2).matchesFilter(times + 1)) {
                            getPlayerMessageSink(SPECTATOR).sendMessage(
                                    Texts.of(
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
                        activeAttacks.remove(9);
                    }
                };
                TimedRunnable<IntegratedRunnable> minonEatingTask = new TimedRunnable<>(minionEater, 20);
                Task minionEatingTaskExecutor = SkreePlugin.inst().getGame().getScheduler().createTaskBuilder()
                        .interval(500, TimeUnit.MILLISECONDS).execute(minonEatingTask).submit(SkreePlugin.inst());
                minonEatingTask.setTask(minionEatingTaskExecutor);
                break;
        }
        lastAttack = System.currentTimeMillis();
        lastAttackNumber = attackCase;
    }

    @Override
    public boolean init() {
        probeArea();
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
        player.setLocation(Probability.pickOneOf(spawnPts));
        return new Clause<>(player, ZoneStatus.ADDED);
    }

    @Override
    public Clause<Player, ZoneStatus> remove(Player player) {
        WorldService service = SkreePlugin.inst().getGame().getServiceManager().provideUnchecked(WorldService.class);
        player.setLocation(service.getEffectWrapper("Main").getWorlds().iterator().next().getSpawnLocation());
        return new Clause<>(player, ZoneStatus.REMOVED);
    }
}
