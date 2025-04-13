package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class Pair extends Expr {

  public final Expr key;
  public final Expr value;

  public Pair(@Nullable Token token, Expr key, Expr value) {
    super(token);
    this.key = key;
    this.value = value;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.pair(this);
  }

  @Override
  public String toString() {
    return "Pair{" +
        "key=" + key +
        ", value=" + value +
        '}';
  }
}
