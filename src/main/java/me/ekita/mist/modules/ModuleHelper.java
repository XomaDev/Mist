package me.ekita.mist.modules;

import me.ekita.mist.syntax.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleHelper {

  public static String asString(Token token, Object value) {
    if (value instanceof String) return (String) value;
    return token.error("Expected a string but got " + value);
  }

  public static List<Object> asList(Token token, Object value) {
    if (value instanceof List<?>) return (List<Object>) value;
    return token.error("Expected a list but got " + value);
  }

  public static List<Object> arrayToList(Object[] array) {
    List<Object> list = new ArrayList<>();
    Collections.addAll(list, array);
    return list;
  }

  public static List<Object> makeArray(String... strings) {
    return arrayToList(strings);
  }
}
