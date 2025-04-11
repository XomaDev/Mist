package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import me.ekita.mist.syntax.Type;
import org.jetbrains.annotations.Nullable;

public class Unary extends Expr {

  public final Type type;
  public final Expr expr;

  public Unary(@Nullable Token token, Type type, Expr expr) {
    super(token);
    this.type = type;
    this.expr = expr;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.unary(this);
  }

  @Override
  public String toString() {
    return "UnaryExpr{" +
        "type=" + type +
        ", expr=" + expr +
        '}';
  }
}
