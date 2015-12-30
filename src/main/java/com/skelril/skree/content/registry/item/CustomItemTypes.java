/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item;

import com.skelril.skree.content.registry.item.admin.HackBook;
import com.skelril.skree.content.registry.item.admin.SwordOGreatPain;
import com.skelril.skree.content.registry.item.consumable.CookedGodFish;
import com.skelril.skree.content.registry.item.consumable.RawGodFish;
import com.skelril.skree.content.registry.item.currency.CofferItem;
import com.skelril.skree.content.registry.item.currency.CondensedCofferItem;
import com.skelril.skree.content.registry.item.generic.*;
import com.skelril.skree.content.registry.item.tool.*;
import com.skelril.skree.content.registry.item.tool.axe.CrystalAxe;
import com.skelril.skree.content.registry.item.tool.hoe.CrystalHoe;
import com.skelril.skree.content.registry.item.tool.pickaxe.CrystalPickaxe;
import com.skelril.skree.content.registry.item.tool.shovel.CrystalShovel;
import com.skelril.skree.content.registry.item.weapon.RedFeather;
import com.skelril.skree.content.registry.item.weapon.sword.CrystalSword;
import com.skelril.skree.content.registry.item.zone.ZoneMasterOrb;
import com.skelril.skree.content.registry.item.zone.ZoneSlaveOrb;

public class CustomItemTypes {
    /* ** Admin ** */
    public static final HackBook HACK_BOOK = new HackBook();
    public static final SwordOGreatPain SWORD_O_GREAT_PAIN = new SwordOGreatPain();

    /* ** Standard ** */

    /* Currency */

    // Coffer Base Item
    public static final CofferItem TESTRIL = new CofferItem("testril", 1);

    // Coffer Child Items
    public static final CondensedCofferItem AQUIS = new CondensedCofferItem("aquis", TESTRIL);
    public static final CondensedCofferItem MARSINCO = new CondensedCofferItem("marsinco", AQUIS);
    public static final CondensedCofferItem POSTRE = new CondensedCofferItem("postre", MARSINCO);
    public static final CondensedCofferItem EQESTA = new CondensedCofferItem("eqesta", POSTRE);
    public static final CondensedCofferItem REDISTRAL = new CondensedCofferItem("redistral", EQESTA);
    public static final CondensedCofferItem RETESRUM = new CondensedCofferItem("retesrum", REDISTRAL);
    public static final CondensedCofferItem MESARDITH = new CondensedCofferItem("mesardith", RETESRUM);

    /* Gems */
    public static final BloodDiamond BLOOD_DIAMOND = new BloodDiamond();
    public static final SeaCrystal SEA_CRYSTAL = new SeaCrystal();

    /* Resources */
    public static final EnderFocus ENDER_FOCUS = new EnderFocus();
    public static final RedShard RED_SHARD = new RedShard();
    public static final FairyDust FAIRY_DUST = new FairyDust();

    /* Food */
    public static final RawGodFish RAW_GOD_FISH = new RawGodFish();
    public static final CookedGodFish COOKED_GOD_FISH = new CookedGodFish();

    /* Weapons */
    public static final CrystalSword CRYSTAL_SWORD = new CrystalSword();

    /* Combat Items */
    public static final RedFeather RED_FEATHER = new RedFeather();

    /* Convenience Items */
    public static final BrokenGlass BROKEN_GLASS = new BrokenGlass();

    /* Zone Items */
    public static final ZoneMasterOrb ZONE_MASTER_ORB = new ZoneMasterOrb();
    public static final ZoneSlaveOrb ZONE_SLAVE_ORB = new ZoneSlaveOrb();

    /* Tools */
    public static final CrystalAxe CRYSTAL_AXE = new CrystalAxe();
    public static final CrystalHoe CRYSTAL_HOE = new CrystalHoe();
    public static final CrystalPickaxe CRYSTAL_PICKAXE = new CrystalPickaxe();
    public static final CrystalShovel CRYSTAL_SHOVEL = new CrystalShovel();
    public static final FocusTeleporter FOCUS_TELEPORTER = new FocusTeleporter();
    public static final HymnOfSummation HYMN_OF_SUMMATION = new HymnOfSummation();
    public static final Luminositor LUMINOSITOR = new Luminositor();
    public static final ScrollOfSummation SCROLL_OF_SUMMATION = new ScrollOfSummation();
    public static final SkullOfTheFallen SKULL_OF_THE_FALLEN = new SkullOfTheFallen();
}
