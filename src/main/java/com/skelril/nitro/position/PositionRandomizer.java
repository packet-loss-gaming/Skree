/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.position;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.probability.Probability;

public class PositionRandomizer {
    private int noiseX;
    private int noiseY;
    private int noiseZ;

    public PositionRandomizer(int noise) {
        this(noise, noise, noise);
    }

    public PositionRandomizer(int noiseX, int noiseY, int noiseZ) {
        this.noiseX = noiseX;
        this.noiseY = noiseY;
        this.noiseZ = noiseZ;
    }

    public Vector3i createPosition3i(Vector3i src) {
        return src.add(
                Probability.getRangedRandom(-noiseX, noiseX * 2),
                Probability.getRangedRandom(-noiseY, noiseY * 2),
                Probability.getRangedRandom(-noiseZ, noiseZ * 2)
        );
    }

    public Vector3i createPosition3i(Vector3d src) {
        return createPosition3i(src.toInt());
    }
}
