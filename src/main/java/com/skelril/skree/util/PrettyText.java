/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.util;

import java.util.Calendar;
import java.util.Locale;

public class PrettyText {
    public static String date(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        StringBuilder builder = new StringBuilder();
        builder.append(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
        builder.append(" ");
        builder.append(calendar.get(Calendar.DAY_OF_MONTH));
        builder.append(" ");
        builder.append(calendar.get(Calendar.YEAR));
        builder.append(" at ");
        builder.append(calendar.get(Calendar.HOUR));
        builder.append(":");
        builder.append(calendar.get(Calendar.MINUTE));
        builder.append(calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
        return builder.toString();
    }

    public static String dateFromCur(long time) {
        Calendar curTime = Calendar.getInstance();
        Calendar newTime = Calendar.getInstance();
        newTime.setTimeInMillis(System.currentTimeMillis() + time);

        // TODO make this actually work

        return "30 seconds";
    }
}
