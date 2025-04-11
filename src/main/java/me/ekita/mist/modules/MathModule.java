package me.ekita.mist.modules;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.runtime.Evaluator;

import java.util.List;

public class MathModule extends Module {
  public MathModule() {
    define("bitwiseAnd", 2, new ModFunction() {
      @Override
      public Object call(Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        return runtime.numericExpr(l.token, l).and(runtime.numericExpr(r.token, r));
      }
    });
    define("bitwiseOr", 2, new ModFunction() {
      @Override
      public Object call(Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        return runtime.numericExpr(l.token, l).or(runtime.numericExpr(r.token, r));
      }
    });
    define("bitwiseXor", 2, new ModFunction() {
      @Override
      public Object call(Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        return runtime.numericExpr(l.token, l).xor(runtime.numericExpr(r.token, r));
      }
    });
  }
}
