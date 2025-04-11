package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

public class IfExpr extends Expr {

  public final Expr condition;
  public final Expr thenExpr;
  public final Expr elseExpr;

  public IfExpr(Token token, Expr condition, Expr thenExpr, Expr elseExpr) {
    super(token);
    this.condition = condition;
    this.thenExpr = thenExpr;
    this.elseExpr = elseExpr;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.ifExpr(this);
  }

  @Override
  public String toString() {
    return "IfExpr{" +
        "condition=" + condition +
        ", thenExpr=" + thenExpr +
        ", elseExpr=" + elseExpr +
        '}';
  }
}
