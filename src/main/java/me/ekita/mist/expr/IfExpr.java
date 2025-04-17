package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

import java.util.List;

public class IfExpr extends Expr {

  public final List<Expr> conditions, bodies;
  public final List<Boolean> requiresScopes;

  public IfExpr(Token token,
                List<Expr> conditions,
                List<Expr> bodies,
                List<Boolean> requiresScopes) {
    super(token);
    this.conditions = conditions;
    this.bodies = bodies;
    this.requiresScopes = requiresScopes;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.ifExpr(this);
  }

  @Override
  public String toString() {
    return "IfExpr{" +
        "conditions=" + conditions +
        ", bodies=" + bodies +
        '}';
  }
}
