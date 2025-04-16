package me.ekita.mist.runtime.structs;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RDictionary extends HashMap<Object, Object> {

  public RDictionary() {
    super();
  }


  public RDictionary(Map<?, ?> m) {
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

  // TODO:
  //  implement a custom equals()
}
