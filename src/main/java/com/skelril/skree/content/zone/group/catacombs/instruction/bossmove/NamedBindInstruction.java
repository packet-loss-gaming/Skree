/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction.bossmove;

import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class NamedBindInstruction<T extends Boss<? extends Living, ?>> implements Instruction<BindCondition, T> {
    private final String name;

    public NamedBindInstruction(String name) {
        this.name = name;
    }

    @Override
    public Optional<Instruction<BindCondition, T>> apply(BindCondition bindCondition, T t) {
        t.getTargetEntity().get().offer(Keys.DISPLAY_NAME, Text.of(name));
        return Optional.empty();
    }
}
