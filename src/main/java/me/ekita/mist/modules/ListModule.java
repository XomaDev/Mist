package me.ekita.mist.modules;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Token;

import java.util.ArrayList;
import java.util.List;

public class ListModule extends Module {

  public ListModule() {
    define("emptyList", 0, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new ArrayList<>();
      }
    });
    define("makeList", -1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        List<Object> evaluated = new ArrayList<>();
        for (Expr item : args) evaluated.add(item.accept(runtime));
        return evaluated;
      }
    });
  }

  @Override
  public ModFunction get(String name, int paramCount) {
    ModFunction func = super.get(name, paramCount);
    // overriding behaviour to support functions with unlimited arguments like min()
    if (func == null) return super.get(name, -1);
    return null;
  }
}
