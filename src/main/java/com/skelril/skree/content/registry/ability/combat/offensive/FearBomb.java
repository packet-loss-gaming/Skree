/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.offensive;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.position.CuboidContainmentPredicate;
import com.skelril.nitro.registry.dynamic.ability.SpecialAttack;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.AbstractProperty;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class FearBomb implements SpecialAttack {
  private Collection<Entity> getTargetEntities(Location<World> originalLocation) {
    CuboidContainmentPredicate predicate = new CuboidContainmentPredicate(originalLocation.getPosition(), 3, 2, 3);
    return originalLocation.getExtent().getEntities((entity) -> predicate.test(entity.getLocation().getPosition()));
  }

  private boolean isPassable(BlockType blockType) {
    Optional<PassableProperty> optProp = blockType.getProperty(PassableProperty.class);
    return optProp.map(AbstractProperty::getValue).orElse(false);
  }

  private static final BlockState WHITE_WOOL_STATE = BlockState.builder().blockType(BlockTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.WHITE).build();
  private static final BlockState RED_WOOL_STATE = BlockState.builder().blockType(BlockTypes.WOOL).add(Keys.DYE_COLOR, DyeColors.RED).build();

  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    final List<Location<World>> blocks = new ArrayList<>();

    Location<World> originalLocation = target.getLocation();

    Vector3d max = originalLocation.getPosition().add(1, 0, 1);
    Vector3d min = originalLocation.getPosition().sub(1, 0, 1);

    for (int x = min.getFloorX(); x <= max.getFloorX(); ++x) {
      for (int z = min.getFloorZ(); z <= max.getFloorZ(); ++z) {
        Location<World> loc = target.getLocation().setBlockPosition(new Vector3i(x, target.getLocation().getBlockY(), z));

        while (loc.getY() > 0 && isPassable(loc.getBlockType())) {
          loc = loc.add(0, -1, 0);
        }

        blocks.add(loc);
      }
    }

    IntegratedRunnable bomb = new IntegratedRunnable() {
      @Override
      public boolean run(int times) {
        Collection<Player> players = blocks.get(0).getExtent().getPlayers();

        for (Location<World> loc : blocks) {
          if (isPassable(loc.getBlockType())) {
            continue;
          }

          for (Player player : players) {
            player.sendBlockChange(loc.getBlockPosition(), times % 2 == 0 ? WHITE_WOOL_STATE : RED_WOOL_STATE);
          }
        }

        return true;
      }

      @Override
      public void end() {
        Collection<Player> players = blocks.get(0).getExtent().getPlayers();

        for (Location<World> loc : blocks) {
          loc.getExtent().triggerExplosion(
              Explosion.builder()
                  .radius(3)
                  .location(loc)
                  .shouldBreakBlocks(false)
                  .shouldDamageEntities(false)
                  .canCauseFire(false)
                  .build()
          );

          for (Player player : players) {
            player.resetBlockChange(loc.getBlockPosition());
          }
        }

        getTargetEntities(originalLocation).stream().filter((e) -> e instanceof Monster || e instanceof Player).forEach((entity) -> {
          entity.damage(10000, damageSource(owner));
        });
      }
    };

    TimedRunnable<IntegratedRunnable> timedRunnable = new TimedRunnable<>(bomb, 6);

    Task task = Task.builder().execute(timedRunnable).interval(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
    timedRunnable.setTask(task);

    notify(owner, Text.of(TextColors.YELLOW, "Your bow creates a powerful bomb."));
  }
}
