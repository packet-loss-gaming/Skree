/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import java.math.BigDecimal;

public interface PriceSnapshot {
    BigDecimal getRawValue();

    default BigDecimal getMeanAdvisory() {
        BigDecimal total = getMaxAdvisory().add(getMinAdvisory());
        return total.divide(new BigDecimal(2), BigDecimal.ROUND_HALF_UP);
    }

    BigDecimal getMinAdvisory();
    BigDecimal getMaxAdvisory();
}
