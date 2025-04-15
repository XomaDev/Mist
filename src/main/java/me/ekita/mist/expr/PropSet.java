package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class PropSet extends Expr {

  public final String module;
  public final String property;
  public final Expr value;

  public PropSet(@Nullable Token token, String module, String color, Expr value) {
    super(token);
    this.module = module;
    this.property = color;
    this.value = value;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.propSet(this);
  }

  @Override
  public String toString() {
    return "PropSet{" +
        "module='" + module + '\'' +
        ", property='" + property + '\'' +
        ", value=" + value +
        '}';
  }
}
