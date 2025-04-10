package me.ekita.mist.expr;

import java.util.List;

public class Statements extends Expr {

  public final List<Expr> expressions;

  public Statements(List<Expr> expressions) {
    super(null);
    this.expressions = expressions;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.statements(this);
  }
}
