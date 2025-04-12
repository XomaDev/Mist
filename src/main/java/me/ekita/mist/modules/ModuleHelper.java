package me.ekita.mist.modules;

import me.ekita.mist.runtime.structs.RDictionary;
import me.ekita.mist.runtime.structs.RList;
import me.ekita.mist.syntax.Token;

import java.util.Collections;
import java.util.Map;

public class ModuleHelper {

  public static String asString(Token token, Object value) {
    if (value instanceof String) return (String) value;
    return token.error("Expected a string but got " + value);
  }

  public static RList asList(Token token, Object value) {
    if (value instanceof RList) return (RList) value;
    return token.error("Expected a list but got " + value);
  }

  public static RList arrayToList(Object[] array) {
    RList list = new RList();
    Collections.addAll(list, array);
    return list;
  }

  public static RList makeFromArray(String... strings) {
    return arrayToList(strings);
  }

  public static RDictionary asMap(Token token, Object value) {
    if (value instanceof Map<?, ?>) return (RDictionary) value;
    return token.error("Expected a map but got " + value);
  }
}
