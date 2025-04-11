package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class GetVar extends Expr {

  public final boolean global;
  public final String name;
  public final int index;

  public GetVar(@Nullable Token token, boolean global, String name, int index) {
    super(token);
    this.global = global;
    this.name = name;
    this.index = index;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.getVr(this);
  }

  @Override
  public String toString() {
    return "Var{" +
        "global=" + global +
        ", name='" + name + '\'' +
        ", index=" + index +
        '}';
  }
}
