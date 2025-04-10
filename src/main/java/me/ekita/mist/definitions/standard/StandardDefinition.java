package me.ekita.mist.definitions.standard;

import me.ekita.mist.definitions.Definition;
import me.ekita.mist.definitions.DefinitionGroup;

import java.util.HashMap;

public class StandardDefinition extends DefinitionGroup {

  private static final Definition println = new Definition("println", 1) {
    @Override
    public Object call(Object[] arguments) {
      System.out.println(arguments[0]);
      return 0;
    }
  };

  private static final HashMap<String, Definition> definitions = new HashMap<>();

  private static void addDefinition(String name, int paramCount, Definition def) {
    definitions.put(paramCount + name, def);
  }

  static {
    addDefinition("println", 1, println);
  }

  @Override
  public Definition get(String name, int paramCount) {
    return definitions.get(paramCount + name);
  }
}
