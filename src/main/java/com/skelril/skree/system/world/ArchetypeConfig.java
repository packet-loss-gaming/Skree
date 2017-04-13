/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.world;

import java.util.List;

public class ArchetypeConfig {
    private String id;
    private String name;
    private String dimension;
    private String generator;
    private boolean usesMapFeatures;
    private List<String> modifiers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public boolean usesMapFeatures() {
        return usesMapFeatures;
    }

    public void setUsesMapFeatures(boolean usesMapFeatures) {
        this.usesMapFeatures = usesMapFeatures;
    }

    public List<String> getModifiers() {
        return modifiers;
    }
}
