package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class ForEach extends Expr {

  public final Expr iterable;
  public final String asName;
  public final Expr body;

  public ForEach(@Nullable Token token, Expr iterable, String asName, Expr body) {
    super(token);
    this.iterable = iterable;
    this.asName = asName;
    this.body = body;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.forEach(this);
  }

  @Override
  public String toString() {
    return "ForEach{" +
        "iterable=" + iterable +
        ", asName='" + asName + '\'' +
        ", body=" + body +
        '}';
  }
}
