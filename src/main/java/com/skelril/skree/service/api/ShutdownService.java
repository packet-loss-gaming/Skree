/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.api;

import org.spongepowered.api.text.Text;

public interface ShutdownService {
    int getTicksTilOffline();
    boolean isShuttingDown();

    boolean shutdown(int ticks);
    boolean shutdown(int ticks, long downtime);
    boolean shutdown(int ticks, Text message);
    boolean shutdown(int ticks, long downtime, Text message);

    void forceShutdown();
    void forceShutdown(Text message);
    void cancelShutdown();
}
