package me.ekita.mist.modules;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Module {

  private final Map<String, ModFunction> functions = new HashMap<>();
  private final Map<String, ModMethod> methods = new HashMap<>();

  public void defineFunc(String name, int paramCount, ModFunction func) {
    functions.put(paramCount + name, func);
  }

  public void defineMethod(String name, int paramCount, ModMethod method) {
    methods.put(paramCount + name, method);
  }

  public @Nullable ModFunction getFunc(String name, int paramCount) {
    return functions.get(paramCount + name);
  }

  public @Nullable ModMethod getMethod(String name, int paramCount) {
    return methods.get(paramCount + name);
  }
}
