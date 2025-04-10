package me.ekita.mist.definitions;

import java.util.HashMap;

public abstract class DefinitionGroup {

  public static final HashMap<String, DefinitionGroup> definitionGroups = new HashMap<>();

  public abstract Definition get(String name, int paramCount);
}
