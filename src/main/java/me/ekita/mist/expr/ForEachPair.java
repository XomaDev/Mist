package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class ForEachPair extends Expr {

  public final Expr iterable;
  public final String keyName, valueName;
  public final Expr body;

  public ForEachPair(@Nullable Token token, Expr iterable, String keyName, String value, Expr body) {
    super(token);
    this.iterable = iterable;
    this.keyName = keyName;
    this.valueName = value;
    this.body = body;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.forEachPair(this);
  }

  @Override
  public String toString() {
    return "ForEachPair{" +
        "iterable=" + iterable +
        ", key='" + keyName + '\'' +
        ", value='" + valueName + '\'' +
        ", body=" + body +
        '}';
  }
}
