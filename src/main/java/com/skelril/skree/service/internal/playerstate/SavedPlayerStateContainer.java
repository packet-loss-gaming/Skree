/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.playerstate;

import java.util.HashMap;
import java.util.Map;

class SavedPlayerStateContainer {
    private String releasedState;

    private Map<String, SavedPlayerState> savedPlayerStates = new HashMap<>();

    public String getReleasedState() {
        return releasedState;
    }

    public void setReleasedState(String releasedState) {
        this.releasedState = releasedState;
    }

    public Map<String, SavedPlayerState> getSavedPlayerStates() {
        return savedPlayerStates;
    }
}
