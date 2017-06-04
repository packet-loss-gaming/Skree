/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro;

import java.util.AbstractMap;

public class Clause<Subject, Status> extends AbstractMap.SimpleEntry<Subject, Status> {
  public Clause(Subject key, Status value) {
    super(key, value);
  }
}
