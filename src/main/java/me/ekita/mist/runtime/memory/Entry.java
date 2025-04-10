package me.ekita.mist.runtime.memory;

public class Entry {

  public final String name;
  public Object value;

  public Entry(String name, Object value) {
    this.name = name;
    this.value = value;
  }
}
