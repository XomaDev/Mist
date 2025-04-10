package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class Bool extends Expr {

  public final boolean value;

  public Bool(@Nullable Token token, boolean value) {
    super(token);
    this.value = value;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.bool(this);
  }
}
