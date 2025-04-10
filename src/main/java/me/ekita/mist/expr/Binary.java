package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import me.ekita.mist.syntax.Type;

public class Binary extends Expr {

  public final Type type;
  public final Expr left;
  public final Expr right;

  public Binary(Token op, Expr left, Expr right) {
    super(op);
    type = op.type;
    this.left = left;
    this.right = right;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.binary(this);
  }
}
