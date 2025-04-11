package me.ekita.mist.modules;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Module {
  private final Map<String, ModFunction> functions = new HashMap<>();

  public void define(String name, int paramCount, ModFunction func) {
    functions.put(paramCount + name, func);
  }

  public @Nullable ModFunction get(String name, int paramCount) {
    return functions.get(paramCount + name);
  }
}
