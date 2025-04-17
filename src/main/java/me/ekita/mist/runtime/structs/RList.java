package me.ekita.mist.runtime.structs;

import me.ekita.mist.runtime.TypeSystem;
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
    for (Object element: this) {
      elements.put(TypeSystem.nakedValue(element));
    }
    return elements.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RList)) return false;
    return TypeSystem.listEquals(this, (RList) o);
  }
}
