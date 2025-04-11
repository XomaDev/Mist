package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObjectCall extends Expr {

  public final Expr object;
  public final String methodName;
  public final List<Expr> arguments;

  public ObjectCall(@Nullable Token token, Expr object, String methodName, List<Expr> arguments) {
    super(token);
    this.object = object;
    this.methodName = methodName;
    this.arguments = arguments;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.objectCall(this);
  }

  @Override
  public String toString() {
    return "ObjectCall{" +
        "object=" + object +
        ", methodName='" + methodName + '\'' +
        ", arguments=" + arguments +
        '}';
  }
}
