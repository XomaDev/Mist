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
      if (entry != null) return entry.value;
    }
    if (superFrame != null) return superFrame.getVar(index, name);
    throw new RuntimeException("Unable to find variable " + name);
  }

  public void reset(Frame newSuperFrame) {
    superFrame = newSuperFrame;
    entries.clear();
  }
}
