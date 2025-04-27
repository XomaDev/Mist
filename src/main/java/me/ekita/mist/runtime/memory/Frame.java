package me.ekita.mist.runtime.memory;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Frame {

  @Nullable
  public Frame superFrame;
  public final List<Entry> entries = new ArrayList<>();

  public Frame(@Nullable Frame superFrame) {
    this.superFrame = superFrame;
  }

  public Object getVar(int index, String name) {
    if (entries.size() > index) {
      Entry entry = entries.get(index);
      if (entry.name.equals(name)) return entry.value;
    }
    if (superFrame != null) return superFrame.getVar(index, name);
    throw new RuntimeException("Unable to find variable " + name);
  }

  public boolean setVar(int index, String name, Object newValue) {
    if (entries.size() > index) {
      Entry entry = entries.get(index);
      if (entry.name.equals(name)) {
        entry.value = newValue;
        return true;
      }
    }
    if (superFrame != null) return superFrame.setVar(index, name, newValue);
    throw new RuntimeException("Unable to find variable " + name);
  }

  public void reset(Frame newSuperFrame) {
    superFrame = newSuperFrame;
    entries.clear();
  }
}
