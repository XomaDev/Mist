package me.ekita.mist.runtime.structs;

import org.json.JSONArray;

import java.util.ArrayList;

public class RList extends ArrayList<Object> {
  @Override
  public String toString() {
    JSONArray elements = new JSONArray();
    for (Object element: this) elements.put(element);
    return elements.toString();
  }
}
