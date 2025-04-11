package me.ekita.mist.runtime.memory;

import java.util.List;

public class Memory {

  private Frame recyclePool = null;
  private Frame currentFrame;

  private final Frame globalFrame;

  public Memory() {
    currentFrame = new Frame(null);
    globalFrame = currentFrame;
  }

  private void recycleFrame(Frame frame) {
    frame.superFrame = recyclePool;
    recyclePool = frame;
  }

  private Frame newScope() {
    if (recyclePool == null) return new Frame(currentFrame);
    Frame tail = recyclePool;
    recyclePool = recyclePool.superFrame;
    tail.reset(currentFrame);
    return tail;
  }

  public void enterScope() {
    currentFrame = newScope();
  }

  public void leaveScope() {
    Frame reusable = currentFrame;
    if (reusable.superFrame == null) throw new RuntimeException("Cannot leave global scope!");
    currentFrame = reusable.superFrame;
    recycleFrame(reusable);
  }

  public void declareVar(boolean global, String name, Object value) {
    List<Entry> entries = global ? globalFrame.entries : currentFrame.entries;
    entries.add(new Entry(name, value));
  }

  public Object getVar(boolean global, int index, String name) {
    return global ? globalFrame.getVar(index, name) : currentFrame.getVar(index, name);
  }
}
