package me.ekita.mist.modules;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Token;

import java.util.List;

public abstract class ModTransformer {
  public abstract Object transform(Token token,
                                   Evaluator runtime,
                                   Object object,
                                   List<Expr> arguments,
                                   List<String> paramNames,
                                   Expr body);
}
