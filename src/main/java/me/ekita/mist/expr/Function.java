package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

import java.util.List;

public class Function extends Expr {

  public final String name;
  public final boolean returning;
  public final List<String> parameterNames;
  public final Expr body;

  public Function(Token token,
                  String name,
                  boolean returning,
                  List<String> parameterNames,
                  Expr body) {
    super(token);
    this.name = name;
    this.returning = returning;
    this.parameterNames = parameterNames;
    this.body = body;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.function(this);
  }

  @Override
  public String toString() {
    return "Function{" +
        "name='" + name + '\'' +
        ", returning=" + returning +
        ", paramNames=" + parameterNames +
        ", body=" + body +
        '}';
  }
}
