/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.playerstate;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

class SavedPlayerState {
  private List<JsonElement> inventoryContents = new ArrayList<>();

  public List<JsonElement> getInventoryContents() {
    return inventoryContents;
  }
}
