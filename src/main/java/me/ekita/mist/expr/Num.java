package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

public class Num extends Expr {

  public final String value;
  public final boolean isFloat;

  public Num(Token token, String value) {
    super(token);
    this.value = value;
    isFloat = value.contains(".");
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.number(this);
  }

  @Override
  public String toString() {
    return "Num{" +
        "value='" + value + '\'' +
        '}';
  }
}
