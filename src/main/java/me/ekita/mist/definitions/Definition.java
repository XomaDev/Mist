package me.ekita.mist.definitions;

public abstract class Definition {

  public final String name;
  public final int paramCount;

  public Definition(String name, int paramCount) {
    this.name = name;
    this.paramCount = paramCount;
  }

  public abstract Object call(Object[] arguments);
}
