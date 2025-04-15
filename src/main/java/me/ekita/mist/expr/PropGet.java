package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class PropGet extends Expr {

  public final String module;
  public final String property;

  public PropGet(@Nullable Token token, String module, String property) {
    super(token);
    this.module = module;
    this.property = property;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.propGet(this);
  }

  @Override
  public String toString() {
    return "PropGet{" +
        "module='" + module + '\'' +
        ", property='" + property + '\'' +
        '}';
  }
}
