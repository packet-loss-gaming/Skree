/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import org.spongepowered.api.text.Text;

public interface ShutdownService {
    int getSecondsTilOffline();
    boolean isShuttingDown();

    boolean shutdown(int seconds);
    boolean shutdown(int seconds, long downtime);
    boolean shutdown(int seconds, Text message);
    boolean shutdown(int seconds, long downtime, Text message);

    void forceShutdown();
    void forceShutdown(Text message);
    void cancelShutdown();
}
