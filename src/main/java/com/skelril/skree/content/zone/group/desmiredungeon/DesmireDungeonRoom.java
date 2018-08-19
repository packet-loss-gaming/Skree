/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.desmiredungeon;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.zone.ZoneBoundingBox;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Skeleton;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;

public class DesmireDungeonRoom {
  private final DesmireDungeonInstance inst;
  private final ZoneBoundingBox boundingBox;

  private ZoneBoundingBox[] doors;

  public DesmireDungeonRoom(DesmireDungeonInstance inst, ZoneBoundingBox boundingBox) {
    this.inst = inst;
    this.boundingBox = boundingBox;

    Vector3i origin = boundingBox.getOrigin();
    this.doors = new ZoneBoundingBox[] {
        new ZoneBoundingBox(origin.add(8, 2, 0), new Vector3i(2, 6, 1)),
        new ZoneBoundingBox(origin.add(0, 2, 8), new Vector3i(1, 6, 2)),
        new ZoneBoundingBox(origin.add(17, 2, 8), new Vector3i(1, 6, 2)),
        new ZoneBoundingBox(origin.add(8, 2, 17), new Vector3i(2, 6, 1))
    };
  }

  private World getWorld() {
    return inst.getRegion().getExtent();
  }

  public ZoneBoundingBox getBoundingBox() {
    return boundingBox;
  }

  public boolean contains(Entity entity) {
    return entity.getWorld().equals(getWorld()) && boundingBox.contains(entity.getLocation().getPosition());
  }

  private void changeDoors(BlockType from, BlockType to) {
    for (ZoneBoundingBox door : doors) {
      door.forAll(pt -> {
        if (getWorld().getBlockType(pt) != from) {
          return;
        }

        getWorld().setBlockType(pt, to);
      });
    }
  }

  private static final BlockType UNLOCKED_BLOCK = BlockTypes.AIR;
  private static final BlockType LOCKED_BLOCK = BlockTypes.NETHER_BRICK_FENCE;

  public void lockDoors() {
    changeDoors(UNLOCKED_BLOCK, LOCKED_BLOCK);
  }

  public void unlockDoors() {
    changeDoors(LOCKED_BLOCK, UNLOCKED_BLOCK);
  }

  public Location<World> getSpawnPoint() {
    Vector3d center = boundingBox.getCenter();
    Vector3d spawnPoint = new Vector3d(center.getX(), boundingBox.getMinimumPoint().getY() + 2, center.getZ());
    spawnPoint = spawnPoint.add(new Vector3d(
        Probability.getRangedRandom(-3.0, 3.0),
        0,
        Probability.getRangedRandom(-3.0, 3.0)
    ));

    return new Location<>(getWorld(), spawnPoint);
  }

  private static final List<EntityType> POSSIBLE_MOBS = Lists.newArrayList(
      EntityTypes.SKELETON, EntityTypes.ZOMBIE, EntityTypes.CREEPER, EntityTypes.SPIDER
  );

  public void summonCreatures() {
    List<Entity> entities = new ArrayList<>();

    for (int i = Probability.getRandom(inst.getPlayers(PARTICIPANT).size() * 5); i > 0; --i) {
      Entity e = getWorld().createEntity(Probability.pickOneOf(POSSIBLE_MOBS), getSpawnPoint().getPosition());
      if (e instanceof Skeleton) {
        ((Skeleton) e).setItemInHand(HandTypes.MAIN_HAND, newItemStack(ItemTypes.BOW));
      }
      entities.add(e);
    }

    getWorld().spawnEntities(entities);
  }
}
