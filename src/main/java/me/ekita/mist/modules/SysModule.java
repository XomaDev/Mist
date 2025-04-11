package me.ekita.mist.modules;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Token;

import java.util.List;

public class SysModule extends Module {
  public SysModule() {
    define("println", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        System.out.println(args.get(0).accept(runtime));
        return 0;
      }
    });
  }
}
