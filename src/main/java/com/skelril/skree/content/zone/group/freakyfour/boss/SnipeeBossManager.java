/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour.boss;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.content.zone.group.catacombs.instruction.bossmove.NamedBindInstruction;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourConfig;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourInstance;
import com.skelril.skree.content.zone.group.freakyfour.boss.bossmove.BackTeleportInstruction;
import com.skelril.skree.content.zone.group.freakyfour.boss.bossmove.FreakyFourBossDeath;
import com.skelril.skree.content.zone.group.freakyfour.boss.bossmove.HealableInstruction;
import com.skelril.skree.content.zone.group.freakyfour.boss.bossmove.HealthBindInstruction;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.ItemTypes;

import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class SnipeeBossManager extends BossManager<Living, ZoneBossDetail<FreakyFourInstance>> {
  private FreakyFourConfig config;

  public SnipeeBossManager(FreakyFourConfig config) {
    this.config = config;
    handleBinds();
    handleUnbinds();
    handleDamage();
    handleDamaged();
  }

  private void handleBinds() {
    List<Instruction<BindCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> bindProcessor = getBindProcessor();
    bindProcessor.add(new NamedBindInstruction<>("Snipee"));
    bindProcessor.add(new HealthBindInstruction<>(config.snipeeHP));
    bindProcessor.add((condition, boss) -> {
      Living entity = boss.getTargetEntity().get();
      if (entity instanceof ArmorEquipable) {
        ((ArmorEquipable) entity).setItemInHand(HandTypes.MAIN_HAND, newItemStack(ItemTypes.BOW));
      }
      return Optional.empty();
    });
  }

  private void handleUnbinds() {
    List<Instruction<UnbindCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> unbindProcessor = getUnbindProcessor();
    unbindProcessor.add(new FreakyFourBossDeath());
  }

  private void handleDamage() {
    List<Instruction<DamageCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> damageProcessor = getDamageProcessor();
    damageProcessor.add((condition, boss) -> {
      Entity attacked = condition.getAttacked();
      if (attacked instanceof Living) {
        EntityHealthUtil.forceDamage(
            (Living) attacked,
            EntityHealthUtil.getMaxHealth((Living) attacked) * config.snipeeDamage
        );
        condition.getEvent().setBaseDamage(0);
      }
      return Optional.empty();
    });
  }

  private void handleDamaged() {
    List<Instruction<DamagedCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> damagedProcessor = getDamagedProcessor();
    damagedProcessor.add(new BackTeleportInstruction(config));
    damagedProcessor.add(new HealableInstruction());
  }
}
