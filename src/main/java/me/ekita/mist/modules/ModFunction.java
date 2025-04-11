package me.ekita.mist.modules;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.runtime.Evaluator;

import java.util.List;

public interface ModFunction {
  public Object call(Evaluator runtime, List<Expr> args);
}
