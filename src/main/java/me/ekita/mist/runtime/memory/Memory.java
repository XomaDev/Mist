package me.ekita.mist.runtime.memory;

public class Memory {

  private Frame recyclePool = null;

  private Frame currentFrame;

  public Memory() {
    currentFrame = new Frame(null);
  }

  private void recycleFrame(Frame frame) {
    frame.superFrame = recyclePool;
    recyclePool = frame;
  }

  private Frame enterScope() {
    if (recyclePool == null) return new Frame(currentFrame);
    Frame tail = recyclePool;
    recyclePool = recyclePool.superFrame;
    tail.reset(currentFrame);
    return tail;
  }

  public void leaveScope() {
    Frame reusable = currentFrame;
    if (reusable.superFrame == null) throw new RuntimeException("Cannot leave global scope!");
    currentFrame = reusable.superFrame;
    recycleFrame(reusable);
  }

  public void declareVar(String name, Object value) {
    currentFrame.entries.add(new Entry(name, value));
  }

  public Object getVar(int index, String name) {
    return currentFrame.getVar(index, name);
  }
}
