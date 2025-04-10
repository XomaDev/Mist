package me.ekita.mist.definitions.predef;

import me.ekita.mist.definitions.Definition;
import me.ekita.mist.expr.Expr;

import java.util.List;

public class UserDefinition extends Definition {

  public final List<String> parameterNames;
  public final boolean returning;
  public final Expr body;

  public final Expr.Visitor<?> evaluator;

  public UserDefinition(String name,
                        boolean returning,
                        List<String> parameterNames,
                        Expr body,
                        Expr.Visitor<?> evaluator) {
    super(name, parameterNames.size());
    this.returning = returning;
    this.parameterNames = parameterNames;
    this.body = body;
    this.evaluator = evaluator;
  }

  @Override
  public Object call(Object[] arguments) {
    return body.accept(evaluator);
  }
}