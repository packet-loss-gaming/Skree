/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.module;

import com.google.common.collect.Lists;

import java.lang.reflect.Method;
import java.util.*;

public class NitroModule {
    private final Object moduleInstance;

    private Map<String, Method> invokables = new HashMap<>();
    private Map<String, List<NitroModule>> children = new HashMap<>();
    private Map<String, Integer> refCountMap = new HashMap<>();

    public NitroModule(Object moduleInstance) {
        this.moduleInstance = moduleInstance;
    }

    public Object getModuleInstance() {
        return moduleInstance;
    }

    public Class getModuleClass() {
        return moduleInstance.getClass();
    }

    public void setInit(String initPhase, Method method) {
        invokables.put(initPhase, method);
    }

    public Optional<Method> getInit(String initPhase) {
        return Optional.ofNullable(invokables.get(initPhase));
    }

    public void addChild(String initPhase, NitroModule module) {
        children.merge(initPhase, Lists.newArrayList(module), (a, b) -> {
            a.addAll(b);
            return a;
        });
    }

    public List<NitroModule> getChildren(String initPhase) {
        return children.getOrDefault(initPhase, new ArrayList<>());
    }

    public Optional<Integer> getReferenceCount(String initPhase) {
        return Optional.ofNullable(refCountMap.get(initPhase));
    }

    public void incrementReferenceCount(String initPhase) {
        refCountMap.merge(initPhase, 1, (a, b) -> a + b);
    }

    public void decrementReferenceCount(String initPhase) {
        refCountMap.merge(initPhase, -1, (a, b) -> a + b);
    }
}
