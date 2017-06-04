/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.text;

import com.google.common.collect.Lists;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;

public class CombinedText {

  private List<Object> objectList;

  private CombinedText(List<Object> objs) {
    objectList = objs;
  }

  public static CombinedText of(Object... objs) {
    return new CombinedText(Lists.newArrayList(objs));
  }

  public Text substitue(Map<String, Object> valueMap) {
    Object[] outputStream = new Object[objectList.size()];
    for (int i = 0; i < objectList.size(); ++i) {
      Object obj = objectList.get(i);
      if (obj instanceof PlaceHolderText) {
        Object result = valueMap.get(((PlaceHolderText) obj).getKey());
        if (result != null) {
          outputStream[i] = result;
          continue;
        }
      } else if (obj instanceof GeneratedText) {
        outputStream[i] = ((GeneratedText) obj).getText();
        continue;
      }
      outputStream[i] = obj;
    }
    return Text.of(outputStream);
  }
}
