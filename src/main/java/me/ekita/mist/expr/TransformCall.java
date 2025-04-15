package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;

import java.util.List;

public class TransformCall extends Expr {

  public final String moduleName;
  public final String transformerName;
  public final List<Expr> arguments;
  public final List<String> paramNames;
  public final Expr body;

  public TransformCall(Token token,
                       String moduleName,
                       String transformerName,
                       List<Expr> arguments,
                       List<String> paramNames,
                       Expr body) {
    super(token);
    this.moduleName = moduleName;
    this.transformerName = transformerName;
    this.arguments = arguments;
    this.paramNames = paramNames;
    this.body = body;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.transformCall(this);
  }

  @Override
  public String toString() {
    return "TransformCall{" +
        "moduleName='" + moduleName + '\'' +
        ", transformerName='" + transformerName + '\'' +
        ", arguments=" + arguments +
        ", paramNames=" + paramNames +
        ", body=" + body +
        '}';
  }
}
