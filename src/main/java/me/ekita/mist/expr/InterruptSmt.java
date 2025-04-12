package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import me.ekita.mist.syntax.Type;
import org.jetbrains.annotations.Nullable;

public class InterruptSmt extends Expr {

  public final Type type;
  @Nullable
  public final Expr value;

  public InterruptSmt(@Nullable Token token, Type type, @Nullable Expr value) {
    super(token);
    this.type = type;
    this.value = value;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.interruptSmt(this);
  }

  @Override
  public String toString() {
    return "InterruptSmt{" +
        "type=" + type +
        ", value=" + value +
        '}';
  }
}
