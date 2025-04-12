package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class VarGet extends Expr {

  public final boolean global;
  public final String name;
  public final int index;

  public VarGet(@Nullable Token token, boolean global, String name, int index) {
    super(token);
    this.global = global;
    this.name = name;
    this.index = index;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.varGet(this);
  }

  @Override
  public String toString() {
    return "VarGet(" + (global ? "global " : "") +  name + ", " + index + ")";
  }
}
