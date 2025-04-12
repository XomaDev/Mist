package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class VarSet extends Expr {

  public final boolean global;
  public final String name;
  public final int index;
  public final Expr value;

  public VarSet(@Nullable Token token, boolean global, String name, int index, Expr value) {
    super(token);
    this.global = global;
    this.name = name;
    this.index = index;
    this.value = value;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.varSet(this);
  }

  @Override
  public String toString() {
    return "VarSet(" + (global ? "global " : " ") + name + ", value=" + value + ")";
  }
}
