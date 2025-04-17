package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class While extends Expr {

  public final Expr condition;
  public final Expr body;
  public final boolean newScope;

  public While(@Nullable Token token, Expr condition, Expr body, boolean newScope) {
    super(token);
    this.condition = condition;
    this.body = body;
    this.newScope = newScope;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.whileLoop(this);
  }


  @Override
  public String toString() {
    return "While{" +
        "condition=" + condition +
        ", body=" + body +
        ", newScope=" + newScope +
        '}';
  }
}
