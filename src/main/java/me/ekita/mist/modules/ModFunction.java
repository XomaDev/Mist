package me.ekita.mist.modules;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Token;

import java.util.List;

public interface ModFunction {
  public Object call(Token token, Evaluator runtime, List<Expr> args);
}
