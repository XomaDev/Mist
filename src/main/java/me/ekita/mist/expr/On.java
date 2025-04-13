package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class On extends Expr {

  public final String component, name;
  public final List<String> paramNames;
  public final Expr body;

  public On(@Nullable Token token, String component, String name, List<String> paramNames, Expr body) {
    super(token);
    this.component = component;
    this.name = name;
    this.paramNames = paramNames;
    this.body = body;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.on(this);
  }

  @Override
  public String toString() {
    return "On{" +
        "component='" + component + '\'' +
        ", name='" + name + '\'' +
        ", paramNames=" + paramNames +
        '}';
  }
}
