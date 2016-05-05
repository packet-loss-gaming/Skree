/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

public enum ZoneStatus {
    ADDED,
    REMOVED,

    REF_LOST,

    NO_REJOIN,
    ERROR,
    MAX_GROUP_SIZE_EXCEEDED,
    CREATION_FAILED,
    EXIST_AND_ACTIVE,
    DESPAWNED
}
