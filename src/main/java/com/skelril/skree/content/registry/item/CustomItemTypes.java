/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item;

import com.skelril.skree.content.registry.item.currency.CofferItem;
import com.skelril.skree.content.registry.item.currency.CondensedCofferItem;
import com.skelril.skree.content.registry.item.generic.BrokenGlass;
import com.skelril.skree.content.registry.item.generic.PrizeBox;
import com.skelril.skree.content.registry.item.minigame.GoldRushKey;
import com.skelril.skree.content.registry.item.minigame.SkyFeather;
import com.skelril.skree.content.registry.item.tool.*;
import com.skelril.skree.content.registry.item.tool.axe.CrystalAxe;
import com.skelril.skree.content.registry.item.tool.axe.JurackAxe;
import com.skelril.skree.content.registry.item.tool.hoe.CrystalHoe;
import com.skelril.skree.content.registry.item.tool.hoe.JurackHoe;
import com.skelril.skree.content.registry.item.tool.magic.MagicWand;
import com.skelril.skree.content.registry.item.tool.pickaxe.CrystalPickaxe;
import com.skelril.skree.content.registry.item.tool.pickaxe.HardenedDiamondPickaxe;
import com.skelril.skree.content.registry.item.tool.pickaxe.JurackPickaxe;
import com.skelril.skree.content.registry.item.tool.shovel.CrystalShovel;
import com.skelril.skree.content.registry.item.tool.shovel.JurackShovel;
import com.skelril.skree.content.registry.item.tool.terragu.*;
import com.skelril.skree.content.registry.item.weapon.RedFeather;
import com.skelril.skree.content.registry.item.zone.ZoneMasterOrb;
import com.skelril.skree.content.registry.item.zone.ZoneSlaveOrb;
import com.skelril.skree.content.registry.item.zone.ZoneTransitionalOrb;

public class CustomItemTypes {
  /* ** Minigame ** */
  public static final GoldRushKey GOLD_RUSH_KEY = new GoldRushKey();
  public static final PrizeBox PRIZE_BOX = new PrizeBox();
  public static final SkyFeather SKY_FEATHER = new SkyFeather();

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
  public static final MagicWand MAGIC_WAND = new MagicWand();
  public static final NetherBowl NETHER_BOWL = new NetherBowl();
  public static final PactScroll PACT_SCROLL = new PactScroll();
  public static final PhantomClock PHANTOM_CLOCK = new PhantomClock();
  public static final PhantomHymn PHANTOM_HYMN = new PhantomHymn();
  public static final ScrollOfSummation SCROLL_OF_SUMMATION = new ScrollOfSummation();
  public static final SkullOfTheFallen SKULL_OF_THE_FALLEN = new SkullOfTheFallen();
}
