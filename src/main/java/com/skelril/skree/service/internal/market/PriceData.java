/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import java.math.BigDecimal;

public interface PriceData {
    BigDecimal getRawValue();

    default BigDecimal getMeanAdvisory() {
        BigDecimal total = getMaxAdvisory().add(getMinAdvisory());
        return total.divide(new BigDecimal(2), BigDecimal.ROUND_HALF_UP);
    }

    Mode getMinMode();
    Mode getMaxMode();

    BigDecimal getMin();
    BigDecimal getMax();

    default BigDecimal getMinAdvisory() {
        if (getMinMode() == Mode.PERCENT) {
            return getRawValue().multiply(getMin());
        } else {
            return getMin();
        }
    }
    default BigDecimal getMaxAdvisory() {
        if (getMaxMode() == Mode.PERCENT) {
            return getRawValue().multiply(getMax());
        } else {
            return getMax();
        }
    }

    enum Mode {
        STATIC,
        PERCENT
    }
}
