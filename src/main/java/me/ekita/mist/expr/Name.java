package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

public class Name extends Expr {

  public final String value;

  public Name(Token token) {
    super(token);
    value = (String) token.data;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return null;
  }
}
