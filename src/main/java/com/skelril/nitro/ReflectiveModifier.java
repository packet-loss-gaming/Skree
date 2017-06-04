/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectiveModifier {
  public static <T> void modifyFieldValue(Class<T> clazz, T object, String feildName, Object value) {
    try {
      Field field = clazz.getDeclaredField(feildName); // Found in the MCP Mappings
      field.setAccessible(true);

      Field modifiersField = Field.class.getDeclaredField("modifiers");

      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

      field.set(object, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("Exception while modifying inaccessible variable: " + e.getMessage());
    }
  }
}
