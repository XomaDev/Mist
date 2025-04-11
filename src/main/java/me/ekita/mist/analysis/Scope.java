package me.ekita.mist.analysis;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Scope {

  public final Scope parent;

  public final Map<String, Integer> variables = new HashMap<>();
  public final Map<String, UniqueFunction> functions = new HashMap<>();

  public Scope(@Nullable Scope parent) {
    this.parent = parent;
  }

  public void defineVr(String name) {
    variables.put(name, variables.size());
  }

  public void defineFn(String name, int paramCount) {
    functions.put(name, new UniqueFunction(name, paramCount));
  }

  public int resolveVr(String name) {
    Integer index = variables.get(name);
    if (index != null) return index;
    if (parent != null) return parent.resolveVr(name);
    return -1;
  }

  public UniqueFunction resolveFn(String name) {
    UniqueFunction fn = functions.get(name);
    if (fn != null) return fn;
    if (parent != null) return parent.resolveFn(name);
    return null;
  }
}
