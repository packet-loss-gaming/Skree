package com.skelril.nitro.entity;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


/**
 * Created by cow_fu on 6/24/15 at 5:38 PM
 *
 * Added basic entity spawning
 */
public class SpawnEntity {

    public static void spawnMob(EntityType entity, World world, Location location){
        Optional<Entity> optEntity = world.createEntity(entity,location.getPosition());
        if (optEntity.isPresent()){
            world.spawnEntity(optEntity.get());
        }
    }

    public static void spawnDroppedItem(ItemStackBuilder builder, World world, Location location){
        Optional<Entity> optEntity = world.createEntity(EntityTypes.DROPPED_ITEM, location.getPosition());
        if (optEntity.isPresent()) {
            Item item = (Item) optEntity.get();
            item.offer(item.getItemData().setValue(builder.build()));
            world.spawnEntity(item);
        }
    }
}
