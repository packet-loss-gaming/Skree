/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.Clause;
import com.skelril.nitro.item.ItemStackFactory;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LegacyZoneBase;
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
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ShnugglesPrimeInstance extends LegacyZoneBase implements Zone, Runnable {

    private Optional<Giant> boss = Optional.empty();
    private long lastAttack = 0;
    private int lastAttackNumber = -1;
    private long lastDeath = 0;
    private boolean damageHeals = false;
    private Set<Integer> activeAttacks = new HashSet<>();
    private Random random = new Random();

    private long lastUltimateAttack = -1;
    private boolean flagged = false;
    private int emptyTicks = 0;

    private double toHeal = 0;
    private List<Location<World>> spawnPts = new ArrayList<>();

    public ShnugglesPrimeInstance(ZoneRegion region) {
        super(region);
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
                requestXPCleanup();
                runAttack(Probability.getRandom(OPTION_COUNT));
            }
        }
    }

    public void buffBabies() {
        for (Entity zombie : getContained(Zombie.class)) {
            // addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 3), true);
        }
    }

    public void spawnBoss() {
        Optional<Entity> spawned = getRegion().getExtent().createEntity(EntityTypes.GIANT, getRegion().getCenter());
        if (spawned.isPresent()) {
            getRegion().getExtent().spawnEntity(spawned.get(), Cause.of(this));
            boss = Optional.of((Giant) spawned.get());
        }
    }
    
    public boolean isBossSpawned() {
        return boss.isPresent();
    }

    public void healBoss(float percentHealth) {
        if (boss.isPresent()) {
            // EntityUtil.heal(boss, boss.getMaxHealth() * percentHealth);
        }
    }

    public void bossDied() {
        activeAttacks.clear();
        lastDeath = System.currentTimeMillis();
        boss = Optional.empty();
    }

    public void requestXPCleanup() {
        // getContained(ExperienceOrb.class).stream().filter(e -> e.getTicksLived() > 20 * 13).forEach(Entity::remove);
    }

    public boolean damageHeals() {
        return damageHeals;
    }

    public boolean canUseUltimate(long time) {
        return System.currentTimeMillis() - lastUltimateAttack >= time;
    }

    public void updateLastUltimate() {
        lastUltimateAttack = System.currentTimeMillis();
    }

    public int getLastAttack() {
        return lastAttack + 13000 > System.currentTimeMillis() ? lastAttackNumber : -1;
    }

    public boolean isActiveAttack(Integer attack) {
        return activeAttacks.contains(attack);
    }

    public void printBossHealth() {
        if (!boss.isPresent()) return;
        Giant boss = this.boss.get();
        int current = (int) Math.ceil(boss.get(Keys.HEALTH).get());
        int max = (int) Math.ceil(boss.get(Keys.MAX_HEALTH).get());
        String message = "Boss Health: " + current + " / " + max;
        // ChatUtil.send(getContained(Player.class), ChatColor.DARK_AQUA, message);
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

    public void spawnMinions(Living target) {
        int spawnCount = Math.max(3, getPlayers().size());
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
                    }
                }
            }
        }
    }

    public static final int OPTION_COUNT = 9;

    public void runAttack(int attackCase) {
        Giant boss = this.boss.get();

        double bossHealth = boss.get(Keys.HEALTH).get();
        double maxBossHealth = boss.get(Keys.MAX_HEALTH).get();

        double delay = Math.max(5000, Probability.getRangedRandom(15 * bossHealth, 25 * bossHealth));

        if (lastAttack != 0 && System.currentTimeMillis() - lastAttack <= delay) return;

        Collection<Player> contained = getPlayers();
        if (contained == null || contained.size() <= 0) return;
        if (attackCase < 1 || attackCase > OPTION_COUNT) attackCase = Probability.getRandom(OPTION_COUNT);
        // AI-ish system
        if ((attackCase == 5 || attackCase == 9) && bossHealth > maxBossHealth * .9) {
            attackCase = Probability.getChance(2) ? 8 : 2;
        }
        if (flagged && Probability.getChance(4)) {
            attackCase = Probability.getChance(2) ? 4 : 7;
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
            runAttack(Probability.getRandom(OPTION_COUNT));
            return;
        }
        switch (attackCase) {
            case 1:
                // ChatUtil.sendWarning(spectator, "Taste my wrath!");
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
                // ChatUtil.sendWarning(spectator, "Embrace my corruption!");
                for (Player player : contained) {
                    // player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 12, 1));
                }
                break;
            case 3:
                // ChatUtil.sendWarning(spectator, "Are you BLIND? Mwhahahaha!");
                for (Player player : contained) {
                    // player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 4, 0));
                }
                break;
            case 4:
                // ChatUtil.sendWarning(spectator, ChatColor.DARK_RED + "Tango time!");
                activeAttacks.add(4);
                SkreePlugin.inst().getGame().getScheduler().createTaskBuilder().delay(7, TimeUnit.SECONDS).execute(() -> {
                    if (!isBossSpawned()) return;
                    Collection<Player> newContained = getContained(Player.class);
                    for (Player player : newContained) {
                        // TODO Convert to Sponge
                        if (((EntityGiantZombie) boss).canEntityBeSeen((EntityPlayer) player)) {
                            // ChatUtil.send(player, "Come closer...");
                            player.setLocation(boss.getLocation());
                            player.damage(100, Cause.of(boss));
                            // TODO convert to Sponge
                            ((EntityPlayer) player).setVelocity(
                                    random.nextDouble() * 1.7 - 1.5,
                                    random.nextDouble() * 2,
                                    random.nextDouble() * 1.7 - 1.5
                            );
                        } else {
                            // ChatUtil.send(player, "Fine... No tango this time...");
                        }
                    }
                    // ChatUtil.send(newContained, "Now wasn't that fun?");
                    activeAttacks.remove(4);
                }).submit(SkreePlugin.inst());
                break;
            case 5:
                if (!damageHeals) {
                    activeAttacks.add(5);
                    // ChatUtil.sendWarning(spectator, "I am everlasting!");
                    damageHeals = true;
                    SkreePlugin.inst().getGame().getScheduler().createTaskBuilder().delay(12, TimeUnit.SECONDS).execute(() -> {
                        if (damageHeals) {
                            damageHeals = false;
                            if (!isBossSpawned()) return;
                            // ChatUtil.send(getContained(Player.class), "Thank you for your assistance.");
                        }
                        activeAttacks.remove(5);
                    }).submit(SkreePlugin.inst());
                    break;
                }
                runAttack(Probability.getRandom(OPTION_COUNT));
                return;
            case 6:
                // ChatUtil.sendWarning(spectator, "Fire is your friend...");
                for (Player player : contained) {
                    // TODO convert to Sponge
                    ((EntityPlayer) player).setFire(30); // This is in seconds for some reason
                }
                break;
            case 7:
                if (!damageHeals) {
                    // ChatUtil.sendWarning(spectator, ChatColor.DARK_RED + "Bask in my glory!");
                    activeAttacks.add(7);
                    SkreePlugin.inst().getGame().getScheduler().createTaskBuilder().delay(7, TimeUnit.SECONDS).execute(() -> {
                        if (!isBossSpawned()) return;
                        
                        Collection<Player> newContained = getContained(Player.class);
                        boolean baskInGlory = newContained.size() == 0;
                        
                        for (Player player : newContained) {
                            // TODO Convert to Sponge
                            if (((EntityGiantZombie) boss).canEntityBeSeen((EntityPlayer) player)) {
                                // ChatUtil.sendWarning(player, ChatColor.DARK_RED + "You!");
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
                        // ChatUtil.send(newContained, "Gah... Afraid are you friends?");
                        activeAttacks.remove(7);
                    }).submit(SkreePlugin.inst());
                    break;
                }
                runAttack(Probability.getRandom(OPTION_COUNT));
                break;
            case 8:
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
            case 9:
                // ChatUtil.send(spectator, ChatColor.DARK_RED, "My minions our time is now!");
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
                        // TODO Timer Util is dead
//                        if (TimerUtil.matchesFilter(times + 1, -1, 2)) {
//                            ChatUtil.send(getContained(Player.class), ChatColor.DARK_AQUA, "The boss has drawn in: " + (int) toHeal + " health.");
//                        }
                        return true;
                    }

                    @Override
                    public void end() {
                        if (!isBossSpawned()) return;
                        // TODO Entity Util is dead
                        // EntityUtil.heal(boss, toHeal);
                        toHeal = 0;
                        // ChatUtil.send(getContained(Player.class), "Thank you my minions!");
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
