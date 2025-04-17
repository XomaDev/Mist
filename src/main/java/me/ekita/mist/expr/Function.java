package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

import java.util.List;

public class Function extends Expr {

  public final String name;
  public final List<String> parameters;
  public final Expr content;
  public final boolean requireScope;

  public Function(Token token,
                  String name,
                  List<String> parameters,
                  Expr content,
                  boolean requireScope) {
    super(token);
    this.name = name;
    this.parameters = parameters;
    this.content = content;
    this.requireScope = requireScope;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.function(this);
  }

  @Override
  public String toString() {
    return "Function{" +
        "name='" + name + '\'' +
        ", params=" + parameters +
        ", body=" + content +
        '}';
  }
}
