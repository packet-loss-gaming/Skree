/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.theforge;

import com.google.gson.JsonElement;
import com.skelril.nitro.item.ItemSerializer;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SerializableForgeState {
  private List<JsonElement> item = new ArrayList<>();
  private List<Integer> quantity = new ArrayList<>();

  public SerializableForgeState() {
    this(new ForgeState());
  }

  public SerializableForgeState(ForgeState forgeState) {
    forgeState.getResults().forEach((key, value) -> {
      try {
        item.add(ItemSerializer.serializeItemStack(key));
        quantity.add(value);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private Map<ItemStack, Integer> getResults() {
    Map<ItemStack, Integer> results = new HashMap<>();
    for (int i = 0; i < item.size(); ++i) {
      try {
        results.put(ItemSerializer.deserializeItemStack(item.get(i)), quantity.get(i));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return results;
  }

  public ForgeState toForgeState() {
    return new ForgeState(getResults());
  }
}
