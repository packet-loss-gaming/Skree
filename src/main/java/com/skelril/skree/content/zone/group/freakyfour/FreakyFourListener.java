/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.entity.EntityDirectionUtil;
import com.skelril.nitro.numeric.MathExt;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Named;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;

import java.util.Optional;

public class FreakyFourListener {

  private final FreakyFourManager manager;

  public FreakyFourListener(FreakyFourManager manager) {
    this.manager = manager;
  }

  @Listener
  public void onBlockChange(ChangeBlockEvent event, @First Player player) {
    if (manager.getApplicableZone(player).isPresent()) {
      for (Transaction<BlockSnapshot> block : event.getTransactions()) {
        if (block.getOriginal().getState().getType() != BlockTypes.WEB) {
          event.setCancelled(true);
          return;
        }
      }
    }
  }

  @Listener
  public void onRightClick(InteractBlockEvent.Secondary.MainHand event, @Named(NamedCause.SOURCE) Player player) {
    Optional<ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);
    if (!optHeldItem.isPresent()) {
      return;
    }

    if (CustomItemTypes.PHANTOM_HYMN != optHeldItem.get().getItem()) {
      return;
    }

    event.setUseBlockResult(Tristate.FALSE);

    Optional<FreakyFourInstance> optInst = manager.getApplicableZone(player);
    if (!optInst.isPresent()) {
      return;
    }

    FreakyFourInstance inst = optInst.get();

    FreakyFourBoss boss = inst.getCurrentboss().orElse(null);
    if (boss == null) {
      inst.setCurrentboss(FreakyFourBoss.CHARLOTTE);
      player.sendMessage(Text.of(TextColors.RED, "You think you can beat us? Ha! we'll see about that..."));
    } else if (!inst.isSpawned(boss)) {
      switch (boss) {
        case CHARLOTTE:
          inst.setCurrentboss(FreakyFourBoss.FRIMUS);
          break;
        case FRIMUS:
          inst.setCurrentboss(FreakyFourBoss.DA_BOMB);
          break;
        case DA_BOMB:
          inst.setCurrentboss(FreakyFourBoss.SNIPEE);
          break;
        case SNIPEE:
          inst.setCurrentboss(null);
          inst.forceEnd();
          return;
      }
    } else {
      return;
    }
    boss = inst.getCurrentboss().orElse(null);

    if (!inst.getRegion(boss).contains(player.getLocation().getPosition())) {
      player.setLocation(new Location<>(inst.getRegion().getExtent(), inst.getCenter(boss)));
    }

    inst.spawnBoss(boss);
  }

  @Listener
  public void onCreeperExplode(ExplosionEvent.Pre event, @First Creeper entity) {
    Optional<FreakyFourInstance> optInst = manager.getApplicableZone(entity);
    if (!optInst.isPresent()) {
      return;
    }

    FreakyFourInstance inst = optInst.get();

    Optional<Living> boss = inst.getBoss(FreakyFourBoss.DA_BOMB);
    if (!boss.isPresent() || !boss.get().equals(entity)) {
      return;
    }

    inst.dabombDetonate(entity.get(Keys.HEALTH).get() / entity.get(Keys.MAX_HEALTH).get());
    throwBack(entity);

    event.setCancelled(true);
  }

  private void throwBack(Living entity) {
    Vector3d vel = EntityDirectionUtil.getFacingVector(entity);
    vel = vel.mul(-Probability.getRangedRandom(1.2, 1.5));
    vel = new Vector3d(vel.getX(), MathExt.bound(vel.getY(), .175, .8), vel.getZ());
    entity.setVelocity(vel);
  }
}
