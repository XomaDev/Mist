package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

import java.util.List;

public class TransformCall extends Expr {

  public final Expr object;
  public final String transformerName;
  public final List<Expr> arguments;
  public final List<String> paramNames;
  public final Expr body;

  public TransformCall(Token token,
                       Expr object,
                       String transformerName,
                       List<Expr> arguments,
                       List<String> parameters,
                       Expr body) {
    super(token);
    this.object = object;
    this.transformerName = transformerName;
    this.arguments = arguments;
    this.paramNames = parameters;
    this.body = body;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.transformCall(this);
  }

  @Override
  public String toString() {
    return "TransformCall{" +
        "object=" + object +
        ", transformerName='" + transformerName + '\'' +
        ", arguments=" + arguments +
        ", paramNames=" + paramNames +
        ", body=" + body +
        '}';
  }
}
