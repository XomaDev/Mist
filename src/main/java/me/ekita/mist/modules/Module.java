package me.ekita.mist.modules;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Module {

  private final Map<String, ModFunction> functions = new HashMap<>();
  private final Map<String, ModMethod> methods = new HashMap<>();
  private final Map<String, ModPropGet> propGets = new HashMap<>();
  private final Map<String, ModPropSet> propSets = new HashMap<>();

  public void definePropGet(String name, ModPropGet get) {
    propGets.put(name, get);
  }

  public void definePropSet(String name, ModPropSet set) {
    propSets.put(name, set);
  }

  public void defineFunc(String name, int paramCount, ModFunction func) {
    functions.put(paramCount + name, func);
  }

  public void defineMethod(String name, int paramCount, ModMethod method) {
    methods.put(paramCount + name, method);
  }

  public @Nullable ModPropGet getProp(String name) {
    return propGets.get(name);
  }

  public @Nullable ModPropSet getPropSet(String name) {
    return propSets.get(name);
  }

  public @Nullable ModFunction getFunc(String name, int paramCount) {
    return functions.get(paramCount + name);
  }

  public @Nullable ModMethod getMethod(String name, int paramCount) {
    return methods.get(paramCount + name);
  }
}
