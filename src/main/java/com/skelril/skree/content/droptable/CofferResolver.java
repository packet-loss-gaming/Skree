/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.droptable;

import com.google.common.collect.Lists;
import com.skelril.nitro.droptable.resolver.point.SimplePointDropResolver;
import com.skelril.nitro.droptable.resolver.point.SimplePointValue;
import com.skelril.nitro.modifier.ModifierFunction;
import com.skelril.nitro.modifier.ModifierFunctions;
import org.spongepowered.api.Game;
import org.spongepowered.api.item.ItemType;

import static com.skelril.skree.content.registry.item.CustomItemTypes.*;

public class CofferResolver extends SimplePointDropResolver {
    public CofferResolver(Game game, int maxCoffers) {
        this(game, maxCoffers, ModifierFunctions.MULTI);
    }

    public CofferResolver(Game game, int maxCoffers, ModifierFunction modifierFunction) {
        super(
                Lists.newArrayList(
                        new SimplePointValue(
                                Lists.newArrayList(
                                        game.getRegistry().getItemBuilder().itemType((ItemType) TESTRIL).build()
                                ),
                                TESTRIL.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(
                                        game.getRegistry().getItemBuilder().itemType((ItemType) AQUIS).build()
                                ),
                                AQUIS.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(
                                        game.getRegistry().getItemBuilder().itemType((ItemType) MARSINCO).build()
                                ),
                                MARSINCO.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(
                                        game.getRegistry().getItemBuilder().itemType((ItemType) POSTRE).build()
                                ),
                                POSTRE.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(
                                        game.getRegistry().getItemBuilder().itemType((ItemType) EQESTA).build()
                                ),
                                EQESTA.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(
                                        game.getRegistry().getItemBuilder().itemType((ItemType) REDISTRAL).build()
                                ),
                                REDISTRAL.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(
                                        game.getRegistry().getItemBuilder().itemType((ItemType) RETESRUM).build()
                                ),
                                RETESRUM.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(
                                        game.getRegistry().getItemBuilder().itemType((ItemType) MESARDITH).build()
                                ),
                                MESARDITH.getCofferValue()
                        )
                ),
                maxCoffers,
                modifierFunction
        );
    }
}
