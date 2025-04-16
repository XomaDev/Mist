package me.ekita.mist.runtime.structs;

import me.ekita.mist.runtime.Evaluator;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;

public class RList extends ArrayList<Object> {

  public RList() {
    super();
  }

  public RList(@NotNull Collection<?> c) {
    super(c);
  }

  @Override
  public String toString() {
    JSONArray elements = new JSONArray();
    for (Object element: this) elements.put(element);
    return elements.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RList)) return false;
    RList list = (RList) o;
    int size = size();
    if (size != list.size()) return false;
    for (int i = 0; i < size; i++) {
      if (!Evaluator.valueEquals(get(i), list.get(i))) {
        return false;
      }
    }
    return true;
  }
}
