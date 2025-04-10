package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class For extends Expr {

  public final String name;
  public final Expr from;
  public final Expr to;
  public final Expr by;

  public final Expr body;

  public For(@Nullable Token token, String name, Expr from, Expr to, Expr by, Expr body) {
    super(token);
    this.name = name;
    this.from = from;
    this.to = to;
    this.by = by;
    this.body = body;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.forLoop(this);
  }
}
