package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class Var extends Expr {

  public final boolean global;
  public final String name;

  public Var(@Nullable Token token, boolean global, String name) {
    super(token);
    this.global = global;
    this.name = name;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.varExpr(this);
  }
}
