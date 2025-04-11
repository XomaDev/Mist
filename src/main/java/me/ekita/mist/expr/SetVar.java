package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class SetVar extends Expr {

  public final boolean global;
  public final String name;
  public final Expr expr;

  public SetVar(@Nullable Token token, boolean global, String name, Expr expr) {
    super(token);
    this.global = global;
    this.name = name;
    this.expr = expr;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.setVr(this);
  }

  @Override
  public String toString() {
    return "SetVar{" +
        "global=" + global +
        ", name='" + name + '\'' +
        ", expr=" + expr +
        '}';
  }
}
