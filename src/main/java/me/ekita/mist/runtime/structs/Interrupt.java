package me.ekita.mist.runtime.structs;

import me.ekita.mist.syntax.Token;
import me.ekita.mist.syntax.Type;

public class Interrupt {

  public final Token token;
  public final Type type;
  public final Object value;

  public Interrupt(Token token, Type type, Object value) {
    this.token = token;
    this.type = type;
    this.value = value;
  }
}
