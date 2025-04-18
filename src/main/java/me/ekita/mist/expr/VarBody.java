package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VarBody extends Expr {

  public final List<String> varNames;
  public final List<Expr> values;
  public final Expr body;

  public VarBody(@Nullable Token token,
                 List<String> varNames,
                 List<Expr> values,
                 Expr body) {
    super(token);
    this.varNames = varNames;
    this.values = values;
    this.body = body;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.varBody(this);
  }

  @Override
  public String toString() {
    return "VarBody{" +
        "varNames=" + varNames +
        ", values=" + values +
        ", body=" + body +
        '}';
  }
}
