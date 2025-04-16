package me.ekita.mist.runtime.structs;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RDict extends HashMap<Object, Object> {

  public RDict() {
    super();
  }

  public RDict(Map<?, ?> m) {
    super(m);
  }

  @Override
  public String toString() {
    JSONObject json = new JSONObject();
    for (Entry<Object, Object> entry : entrySet()) {
      json.put(String.valueOf(entry.getKey()), entry.getValue());
    }
    return json.toString();
  }
}
