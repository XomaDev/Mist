package me.ekita.mist.runtime.structs;

import org.json.JSONObject;

import java.util.HashMap;

public class RDictionary extends HashMap<Object, Object> {
  @Override
  public String toString() {
    JSONObject json = new JSONObject();
    for (Entry<Object, Object> entry : entrySet()) {
      json.put(String.valueOf(entry.getKey()), entry.getValue());
    }
    return json.toString();
  }
}
