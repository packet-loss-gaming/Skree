package com.skelril.skree.system.cowcommand;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.cowcommand.CowCommand;
import org.spongepowered.api.Game;

/**
 * Created by cow_fu on 6/15/15 at 9:50 PM
 */
public class CowCommandSystem {

    public CowCommandSystem(SkreePlugin plugin,Game game){
        game.getCommandDispatcher().register(plugin, CowCommand.aquireSpec(),"Cowcommand","cowcommand");
    }
}
