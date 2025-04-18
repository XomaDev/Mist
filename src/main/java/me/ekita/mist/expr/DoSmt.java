package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class DoSmt extends Expr {

  public final boolean requireScope;
  public final Expr body;
  public final Expr result;

  public DoSmt(@Nullable Token token, boolean requireScope, Expr body, Expr result) {
    super(token);
    this.requireScope = requireScope;
    this.body = body;
    this.result = result;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.doSmt(this);
  }

  @Override
  public String toString() {
    return "DoSmt{" +
        "requireScope=" + requireScope +
        ", body=" + body +
        ", result=" + result +
        '}';
  }
}
