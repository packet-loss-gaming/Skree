/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block;

import com.skelril.skree.content.registry.block.container.GraveStone;
import com.skelril.skree.content.registry.block.mushroom.MagicMushroom;
import com.skelril.skree.content.registry.block.region.RegionMarker;
import com.skelril.skree.content.registry.block.region.RegionMaster;
import com.skelril.skree.content.registry.block.terrain.JurackOre;
import com.skelril.skree.content.registry.block.terrain.MagicStone;
import com.skelril.skree.content.registry.block.utility.MagicLadder;
import com.skelril.skree.content.registry.block.utility.MagicPlatform;

public class CustomBlockTypes {
    /* ** Region ** */
    public static final RegionMarker REGION_MARKER = new RegionMarker();
    public static final RegionMaster REGION_MASTER = new RegionMaster();

    /* ** Utility ** */
    public static final MagicLadder MAGIC_LADDER = new MagicLadder();
    public static final MagicPlatform MAGIC_PLATFORM = new MagicPlatform();

    /* ** Wilderness ** */
    public static final GraveStone GRAVE_STONE = new GraveStone();

    public static final JurackOre JURACK_ORE = new JurackOre();

    public static final MagicMushroom MAGIC_MUSHROOM = new MagicMushroom();
    public static final MagicStone MAGIC_STONE = new MagicStone();
}
