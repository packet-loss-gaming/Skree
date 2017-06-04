/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class NitroModuleManager {
  private final Map<String, NitroModule> nitroModules = new HashMap<>();

  public Optional<NitroModule> getModule(String name) {
    return Optional.ofNullable(nitroModules.get(name));
  }

  public void discover() {
    discoverModules();
    discoverInit();
  }

  private void discoverModules() {
  }

  public void registerModule(Object object) {
    NModule module = object.getClass().getAnnotation(NModule.class);
    if (module != null) {
      nitroModules.put(module.name(), new NitroModule(object));
    }
  }

  private void discoverInit() {
    for (NitroModule module : nitroModules.values()) {
      for (Method method : module.getModuleClass().getMethods()) {
        NModuleTrigger annotation = method.getAnnotation(NModuleTrigger.class);
        if (annotation == null) {
          continue;
        }

        module.setInit(annotation.trigger(), method);
        for (String dependency : annotation.dependencies()) {
          NitroModule parent = nitroModules.get(dependency);
          module.incrementReferenceCount(annotation.trigger());
          parent.addChild(annotation.trigger(), module);
        }
      }
    }
  }

  public void trigger(String initPhase) {
    Queue<NitroModule> moduleQueue = new ArrayDeque<>();
    moduleQueue.addAll(nitroModules.values());
    while (!moduleQueue.isEmpty()) {
      NitroModule module = moduleQueue.poll();
      if (!handle(initPhase, module)) {
        moduleQueue.add(module);
      }
    }
  }

  private boolean handle(String initPhase, NitroModule module) {
    if (module.getReferenceCount(initPhase).orElse(0) > 0) {
      return false;
    }

    Optional<Method> initMethod = module.getInit(initPhase);
    if (initMethod.isPresent()) {
      try {
        initMethod.get().invoke(module.getModuleInstance());
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }

    for (NitroModule child : module.getChildren(initPhase)) {
      child.decrementReferenceCount(initPhase);
    }

    return true;
  }
}
