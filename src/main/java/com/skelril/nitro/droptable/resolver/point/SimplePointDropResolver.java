/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.resolver.point;

import com.skelril.nitro.modifier.ModifierFunction;
import com.skelril.nitro.modifier.ModifierFunctions;

import java.util.List;

public class SimplePointDropResolver extends AbstractSlipperyPointResolver implements PointDropResolver {
    private final int maxPoints;
    private final ModifierFunction modiFunc;

    public SimplePointDropResolver(List<PointValue> choices, int maxPoints) {
        this(choices, maxPoints, ModifierFunctions.MULTI);
    }

    public SimplePointDropResolver(List<PointValue> choices, int maxPoints, ModifierFunction modiFunc) {
        super(choices);
        this.maxPoints = maxPoints;
        this.modiFunc = modiFunc;
    }

    @Override
    public int getMaxPoints(double modifier) {
        return (int) this.modiFunc.apply(maxPoints, modifier);
    }
}
