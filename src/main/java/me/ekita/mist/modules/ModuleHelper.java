package me.ekita.mist.modules;

import me.ekita.mist.runtime.structs.RList;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Token;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModuleHelper {

  public static RNumber asNumber(Token token, Object value) {
    if (value instanceof RNumber) return (RNumber) value;
    return token.error("Expected a number but got " + value);
  }

  public static String asString(Token token, Object value) {
    if (value instanceof String) return (String) value;
    if (value instanceof RNumber) return value.toString();
    return token.error("Expected a string but got " + value);
  }

  public static List<Object> asList(Token token, Object value) {
    if (value instanceof List<?>) return (List<Object>) value;
    return token.error("Expected a list but got " + value);
  }

  public static List<Object> arrayToList(Object[] array) {
    List<Object> list = new RList();
    Collections.addAll(list, array);
    return list;
  }

  public static List<Object> makeFromArray(String... strings) {
    return arrayToList(strings);
  }

  public static Map<Object, Object> asMap(Token token, Object value) {
    if (value instanceof Map<?, ?>) return (Map<Object, Object>) value;
    return token.error("Expected a map but got " + value);
  }
}
