/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction.bossmove;

import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.EntityDetail;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CatacombsDamageNearby extends DamageNearby<Boss<Zombie, CatacombsBossDetail>> {
    @Override
    public boolean checkTarget(Boss<Zombie, CatacombsBossDetail> boss, Living entity) {
        return entity instanceof Player && boss.getDetail().getZone().contains(entity);
    }

    @Override
    public double getDamage(EntityDetail detail) {
        return Probability.getRandom(20);
    }

    @Override
    public void damage(Boss<Zombie, CatacombsBossDetail> boss, Living entity) {
        super.damage(boss, entity);
        if (entity instanceof Player) {
            ((Player) entity).sendMessage(Text.of(TextColors.RED, "The boss sends some of the damage back to you"));
        }
    }
}
