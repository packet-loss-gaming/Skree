/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.skelril.skree.content.cowcommand;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;
import org.spongepowered.api.world.World;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.world.Location;

import com.google.common.base.Optional;

import static org.spongepowered.api.util.command.args.GenericArguments.*;


public class CowCommand implements CommandExecutor {

    public void spawnEntity(EntityType entity,World world, Location location){
        Optional<Entity> optional = world.createEntity(entity,location.getPosition());
        if (optional.isPresent()){
            world.spawnEntity(optional.get());
        }
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        if(src instanceof Player){
            Player player = (Player) src;
            World world = player.getWorld();
            Location location = player.getLocation();
            int numberOfTnt = args.<Integer>getOne("Number of Tnt").get();

            player.sendMessage(Texts.of("Hai "+player.getName()+", this is my first command :D"));
            //spawnEntity(EntityTypes.ENDER_CRYSTAL,world,location);
            //spawnEntity(EntityTypes.ARROW,world,location.add(0,5,0));
            for(int i = 0; i<numberOfTnt;++i){
                spawnEntity(EntityTypes.PRIMED_TNT,world,location);
            }


        }

        else
            src.sendMessage(Texts.of("Hai not player, its my first command :D!"));


        return CommandResult.success();
    }
    public static CommandSpec aquireSpec(){
        return CommandSpec.builder()
                .description(Texts.of("Cow's First Command :D"))
                .permission("skree.cowcommand")
                .arguments(
                        onlyOne(optional(integer(Texts.of("Number of Tnt")),0))
                )
                .executor(new CowCommand()).build();
    }
}
