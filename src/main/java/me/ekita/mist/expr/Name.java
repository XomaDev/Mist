package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

public class Name extends Expr {

  public final String value;
  public final int index;

  public Name(Token token, int index) {
    super(token);
    value = (String) token.data;
    this.index = index;
  }

  public void invalidate() {
    token.error("Cannot find symbol '" + value + "'");
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.name(this);
  }

  @Override
  public String toString() {
    return "Name{" +
        "value='" + value + '\'' +
        ", index=" + index +
        '}';
  }
}
