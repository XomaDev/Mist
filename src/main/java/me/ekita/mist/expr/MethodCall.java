package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

import java.util.List;

public class MethodCall extends Expr {

  public final String name;
  public final List<Expr> arguments;

  public MethodCall(Token token, String name, List<Expr> arguments) {
    super(token);
    this.name = name;
    this.arguments = arguments;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.methodCall(this);
  }
}
