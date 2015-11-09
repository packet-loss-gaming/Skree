/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.google.common.collect.Sets;
import com.skelril.nitro.Clause;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.sink.MessageSinks;

import java.util.Collection;
import java.util.stream.Collectors;

public interface Zone {

    boolean init();

    default boolean end() {
        if (isActive()) {
            return false;
        }
        forceEnd();
        return true;
    }
    void forceEnd();

    boolean isActive();

    Clause<Player, ZoneStatus> add(Player player);
    Clause<Player, ZoneStatus> remove(Player player);

    default Collection<Clause<Player, ZoneStatus>> add(Collection<Player> players) {
        return players.stream().map(this::add).collect(Collectors.toList());
    }
    default Collection<Clause<Player, ZoneStatus>> remove(Collection<Player> players) {
        return players.stream().map(this::remove).collect(Collectors.toList());
    }

    ZoneRegion getRegion();
    Collection<Player> getPlayers(PlayerClassifier classifier);
    default MessageSink getPlayerMessageSink(PlayerClassifier classifier) {
        return MessageSinks.to(Sets.newHashSet(getPlayers(classifier)));
    }
}
