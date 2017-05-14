/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness.wanderer;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.position.CuboidContainmentPredicate;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossListener;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.world.wilderness.WildernessBossDetail;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Spider;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.entity.EntityHealthUtil.setMaxHealth;

public class Fangz {
    private final BossManager<Spider, WildernessBossDetail> bossManager = new BossManager<>();

    public Fangz() {
        setupFangz();
    }

    public void bind(Spider entity, WildernessBossDetail detail) {
        bossManager.bind(new Boss<>(entity, detail));
    }

    private void setupFangz() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(bossManager, Spider.class)
        );

        List<Instruction<BindCondition, Boss<Spider, WildernessBossDetail>>> bindProcessor = bossManager.getBindProcessor();
        bindProcessor.add((condition, boss) -> {
            Optional<Spider> optBossEnt = boss.getTargetEntity();
            if (optBossEnt.isPresent()) {
                Spider bossEnt = optBossEnt.get();
                bossEnt.offer(Keys.DISPLAY_NAME, Text.of("Fangz"));
                double bossHealth = 20 * 50 * boss.getDetail().getLevel();
                setMaxHealth(bossEnt, bossHealth, true);
            }
            return Optional.empty();
        });

        List<Instruction<UnbindCondition, Boss<Spider, WildernessBossDetail>>> unbindProcessor = bossManager.getUnbindProcessor();
        unbindProcessor.add((condition, boss) -> {
            Optional<Spider> optBossEnt = boss.getTargetEntity();
            if (optBossEnt.isPresent()) {
                Spider bossEnt = optBossEnt.get();
                CuboidContainmentPredicate predicate = new CuboidContainmentPredicate(bossEnt.getLocation().getPosition(), 3, 2, 3);
                for (Entity aEntity : bossEnt.getNearbyEntities((entity) -> predicate.test(entity.getLocation().getPosition()))) {
                    Optional<PotionEffectData> optPotionEffects = aEntity.getOrCreate(PotionEffectData.class);
                    if (!optPotionEffects.isPresent()) {
                        continue;
                    }

                    PotionEffectData potionEffects = optPotionEffects.get();
                    potionEffects.addElement(PotionEffect.of(PotionEffectTypes.SLOWNESS, 2, 20 * 30));
                    potionEffects.addElement(PotionEffect.of(PotionEffectTypes.POISON, 2, 20 * 30));
                }
            }
            return Optional.empty();
        });

        List<Instruction<DamageCondition, Boss<Spider, WildernessBossDetail>>> damageProcessor = bossManager.getDamageProcessor();
        damageProcessor.add((condition, boss) -> {
            Optional<Spider> optBossEnt = boss.getTargetEntity();
            if (optBossEnt.isPresent()) {
                Spider bossEnt = optBossEnt.get();
                Entity eToHit = condition.getAttacked();
                if (!(eToHit instanceof Living)) return Optional.empty();
                Living toHit = (Living) eToHit;

                DamageEntityEvent event = condition.getEvent();
                event.setBaseDamage(event.getBaseDamage() * 2);
                EntityHealthUtil.heal(bossEnt, event.getBaseDamage());

                Optional<PotionEffectData> optPotionEffects = toHit.getOrCreate(PotionEffectData.class);
                if (!optPotionEffects.isPresent()) {
                    return Optional.empty();
                }

                PotionEffectData potionEffects = optPotionEffects.get();
                potionEffects.addElement(PotionEffect.of(PotionEffectTypes.SLOWNESS, 1, 20 * 15));
                potionEffects.addElement(PotionEffect.of(PotionEffectTypes.POISON, 1, 20 * 15));
            }
            return Optional.empty();
        });
    }
}
