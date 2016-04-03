/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item;

import com.skelril.skree.content.registry.item.admin.HackBook;
import com.skelril.skree.content.registry.item.admin.SwordOGreatPain;
import com.skelril.skree.content.registry.item.armor.*;
import com.skelril.skree.content.registry.item.consumable.CookedGodFish;
import com.skelril.skree.content.registry.item.consumable.RawGodFish;
import com.skelril.skree.content.registry.item.currency.CofferItem;
import com.skelril.skree.content.registry.item.currency.CondensedCofferItem;
import com.skelril.skree.content.registry.item.generic.*;
import com.skelril.skree.content.registry.item.minigame.GoldRushKey;
import com.skelril.skree.content.registry.item.tool.*;
import com.skelril.skree.content.registry.item.tool.axe.CrystalAxe;
import com.skelril.skree.content.registry.item.tool.axe.JurackAxe;
import com.skelril.skree.content.registry.item.tool.hoe.CrystalHoe;
import com.skelril.skree.content.registry.item.tool.hoe.JurackHoe;
import com.skelril.skree.content.registry.item.tool.pickaxe.CrystalPickaxe;
import com.skelril.skree.content.registry.item.tool.pickaxe.HardenedDiamondPickaxe;
import com.skelril.skree.content.registry.item.tool.pickaxe.JurackPickaxe;
import com.skelril.skree.content.registry.item.tool.shovel.CrystalShovel;
import com.skelril.skree.content.registry.item.tool.shovel.JurackShovel;
import com.skelril.skree.content.registry.item.tool.terragu.*;
import com.skelril.skree.content.registry.item.weapon.RedFeather;
import com.skelril.skree.content.registry.item.weapon.bow.NetherBow;
import com.skelril.skree.content.registry.item.weapon.sword.*;
import com.skelril.skree.content.registry.item.zone.ZoneMasterOrb;
import com.skelril.skree.content.registry.item.zone.ZoneSlaveOrb;
import com.skelril.skree.content.registry.item.zone.ZoneTransitionalOrb;

public class CustomItemTypes {
    /* ** Admin ** */
    public static final HackBook HACK_BOOK = new HackBook();
    public static final SwordOGreatPain SWORD_O_GREAT_PAIN = new SwordOGreatPain();

    /* ** Minigame ** */
    public static final GoldRushKey GOLD_RUSH_KEY = new GoldRushKey();
    public static final PrizeBox PRIZE_BOX = new PrizeBox();

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
    public static final JurackGem JURACK_GEM = new JurackGem();
    public static final BloodDiamond BLOOD_DIAMOND = new BloodDiamond();
    public static final SeaCrystal SEA_CRYSTAL = new SeaCrystal();

    /* Resources */
    public static final AncientIngot ANCIENT_INGOT = new AncientIngot();
    public static final AncientMetalFragment ANCIENT_METAL_FRAGMENT = new AncientMetalFragment();
    public static final DemonicBlade DEMONIC_BLADE = new DemonicBlade();
    public static final DemonicHilt DEMONIC_HILT = new DemonicHilt();
    public static final DemonicIngot DEMONIC_INGOT = new DemonicIngot();
    public static final EmblemOfHallow EMBLEM_OF_HALLOW = new EmblemOfHallow();
    public static final EmblemOfTheForge EMBLEM_OF_THE_FORGE = new EmblemOfTheForge();
    public static final EnderFocus ENDER_FOCUS = new EnderFocus();
    public static final RedShard RED_SHARD = new RedShard();
    public static final FairyDust FAIRY_DUST = new FairyDust();
    public static final HolyBlade HOLY_BLADE = new HolyBlade();
    public static final HolyHilt HOLY_HILT = new HolyHilt();
    public static final HolyIngot HOLY_INGOT = new HolyIngot();
    public static final UnstableCatalyst UNSTABLE_CATALYST = new UnstableCatalyst();

    /* Food */
    public static final RawGodFish RAW_GOD_FISH = new RawGodFish();
    public static final CookedGodFish COOKED_GOD_FISH = new CookedGodFish();

    /* Armors */
    public static final JurackHelmet JURACK_HELMET = new JurackHelmet();
    public static final JurackChestplate JURACK_CHESTPLATE = new JurackChestplate();
    public static final JurackLeggings JURACK_LEGGINGS = new JurackLeggings();
    public static final JurackBoots JURACK_BOOTS = new JurackBoots();

    public static final CrystalHelmet CRYSTAL_HELMET = new CrystalHelmet();
    public static final CrystalChestplate CRYSTAL_CHESTPLATE = new CrystalChestplate();
    public static final CrystalLeggings CRYSTAL_LEGGINGS = new CrystalLeggings();
    public static final CrystalBoots CRYSTAL_BOOTS = new CrystalBoots();

    public static final GuardianHelmet GUARDIAN_HELMET = new GuardianHelmet();
    public static final GuardianChestplate GUARDIAN_CHESTPLATE = new GuardianChestplate();
    public static final GuardianLeggings GUARDIAN_LEGGINGS = new GuardianLeggings();
    public static final GuardianBoots GUARDIAN_BOOTS = new GuardianBoots();

    public static final DivineHelmet DIVINE_HELMET = new DivineHelmet();
    public static final DivineChestplate DIVINE_CHESTPLATE = new DivineChestplate();
    public static final DivineLeggings DIVINE_LEGGINGS = new DivineLeggings();
    public static final DivineBoots DIVINE_BOOTS = new DivineBoots();

    public static final TormentorHelmet TORMENTOR_HELMET = new TormentorHelmet();
    public static final TormentorChestplate TORMENTOR_CHESTPLATE = new TormentorChestplate();
    public static final TormentorLeggings TORMENTOR_LEGGINGS = new TormentorLeggings();
    public static final TormentorBoots TORMENTOR_BOOTS = new TormentorBoots();

    public static final WraithHelmet WRAITH_HELMET = new WraithHelmet();
    public static final WraithChestplate WRAITH_CHESTPLATE = new WraithChestplate();
    public static final WraithLeggings WRAITH_LEGGINGS = new WraithLeggings();
    public static final WraithBoots WRAITH_BOOTS = new WraithBoots();

    /* Weapons */
    public static final NetherBow NETHER_BOW = new NetherBow();

    public static final DemonicSword DEMONIC_SWORD = new DemonicSword();
    public static final HolySword HOLY_SWORD = new HolySword();

    public static final JurackSword JURACK_SWORD = new JurackSword();
    public static final CrystalSword CRYSTAL_SWORD = new CrystalSword();
    public static final TwoTailedSword TWO_TAILED_SWORD = new TwoTailedSword();

    /* Combat Items */
    public static final RedFeather RED_FEATHER = new RedFeather();

    /* Convenience Items */
    public static final BrokenGlass BROKEN_GLASS = new BrokenGlass();

    /* Zone Items */
    public static final ZoneMasterOrb ZONE_MASTER_ORB = new ZoneMasterOrb();
    public static final ZoneSlaveOrb ZONE_SLAVE_ORB = new ZoneSlaveOrb();
    public static final ZoneTransitionalOrb ZONE_TRANSITIONAL_ORB = new ZoneTransitionalOrb();

    /* Tools */
    public static final WoodTerragu WOOD_TERRAGU = new WoodTerragu();
    public static final StoneTerragu STONE_TERRAGU = new StoneTerragu();
    public static final IronTerragu IRON_TERRAGU = new IronTerragu();
    public static final GoldTerragu GOLD_TERRAGU = new GoldTerragu();
    public static final DiamondTerragu DIAMOND_TERRAGU = new DiamondTerragu();
    public static final JurackTerragu JURACK_TERRAGU = new JurackTerragu();
    public static final CrystalTerragu CRYSTAL_TERRAGU = new CrystalTerragu();

    public static final JurackAxe JURACK_AXE = new JurackAxe();
    public static final CrystalAxe CRYSTAL_AXE = new CrystalAxe();

    public static final JurackHoe JURACK_HOE = new JurackHoe();
    public static final CrystalHoe CRYSTAL_HOE = new CrystalHoe();

    public static final HardenedDiamondPickaxe HARDENED_DIAMOND_PICKAXE = new HardenedDiamondPickaxe();
    public static final JurackPickaxe JURACK_PICKAXE = new JurackPickaxe();
    public static final CrystalPickaxe CRYSTAL_PICKAXE = new CrystalPickaxe();

    public static final JurackShovel JURACK_SHOVEL = new JurackShovel();
    public static final CrystalShovel CRYSTAL_SHOVEL = new CrystalShovel();

    public static final FocusTeleporter FOCUS_TELEPORTER = new FocusTeleporter();
    public static final HymnOfSummation HYMN_OF_SUMMATION = new HymnOfSummation();
    public static final Luminositor LUMINOSITOR = new Luminositor();
    public static final NetherBowl NETHER_BOWL = new NetherBowl();
    public static final PhantomClock PHANTOM_CLOCK = new PhantomClock();
    public static final PhantomHymn PHANTOM_HYMN = new PhantomHymn();
    public static final ScrollOfSummation SCROLL_OF_SUMMATION = new ScrollOfSummation();
    public static final SkullOfTheFallen SKULL_OF_THE_FALLEN = new SkullOfTheFallen();
}
