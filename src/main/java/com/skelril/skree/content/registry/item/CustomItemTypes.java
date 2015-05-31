/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item;

import com.skelril.skree.content.registry.item.admin.HackBook;
import com.skelril.skree.content.registry.item.admin.SwordOGreatPain;
import com.skelril.skree.content.registry.item.generic.Luminositor;
import com.skelril.skree.content.registry.item.weapon.sword.CrystalSword;

public class CustomItemTypes {
    // Admin
    public static final HackBook HACK_BOOK = new HackBook();
    public static final SwordOGreatPain SWORD_O_GREAT_PAIN = new SwordOGreatPain();

    // Standard
    public static final CrystalSword CRYSTAL_SWORD = new CrystalSword();
    public static final Luminositor LUMINOSITOR = new Luminositor();
}
