package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class On extends Expr {

  public final String component, name;
  public final List<String> parameters;
  public final Expr content;
  public final boolean requireScope;

  public On(@Nullable Token token,
            String component,
            String event,
            List<String> parameters,
            Expr content,
            boolean requireScope) {
    super(token);
    this.component = component;
    this.name = event;
    this.parameters = parameters;
    this.content = content;
    this.requireScope = requireScope;
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
        ", params=" + parameters +
        '}';
  }
}
