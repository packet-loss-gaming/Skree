/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.skelril.skree.content.cowcommand;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;
import org.spongepowered.api.world.World;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.world.Location;

import com.skelril.nitro.entity.SpawnEntity;

import com.skelril.skree.content.cowcommand.CowCommandRocks;

import static org.spongepowered.api.util.command.args.GenericArguments.*;


public class CowCommand implements CommandExecutor {

    final int MaxTNT = 1000;
    private static Game game;
    public static void getGame(Game game1){
        game = game1;
    }



    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        if(src instanceof Player){
            Player player = (Player) src;
            World world = player.getWorld();
            Location location = player.getLocation();
            int numberOfTnt = args.<Integer>getOne("Number of Tnt").get();

            if (numberOfTnt > MaxTNT){
                TextBuilder builder = Texts.builder();
                builder.color(TextColors.RED);
                builder.append(Texts.of("You can not have more than "+MaxTNT+" TNT at a time."));
                player.sendMessage(
                        ChatTypes.SYSTEM,
                        builder.build()
                );
                return CommandResult.empty();
            }

            TextBuilder textBuilder = Texts.builder();
            textBuilder.append(Texts.of("Hai "+player.getName()+", this is my first command :D"));
            textBuilder.onClick(TextActions.runCommand("/time set 0"));

            player.sendMessage(Texts.of(textBuilder.build()));

            for(int i = 0; i<numberOfTnt;++i){
                SpawnEntity.spawnMob(EntityTypes.PRIMED_TNT,world,location);
            }

            ItemStackBuilder builder = game.getRegistry().getItemBuilder().itemType(ItemTypes.MILK_BUCKET).quantity(1);
            SpawnEntity.spawnDroppedItem(builder, world, location);
        }

        else{
            src.sendMessage(Texts.of("Hai not player, its my first command :D!"));
        }
        return CommandResult.success();
    }


    public static CommandSpec aquireSpec(){
        return CommandSpec.builder()
                .description(Texts.of("Cow's First Command :D"))
                .permission("skree.cowcommand")
                .child(CowCommandRocks.cowrocks,"rocks")
                .arguments(
                        onlyOne(optional(integer(Texts.of("Number of Tnt")), 0))
                )
                .executor(new CowCommand()).build();
    }
}
