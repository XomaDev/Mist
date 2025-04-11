package me.ekita.mist.modules;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Token;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MathModule extends Module {

  private static final Random RANDOM = new Random();

  public MathModule() {
    define("bitwiseAnd", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        return runtime.numericExpr(l.token, l).and(runtime.numericExpr(r.token, r));
      }
    });
    define("bitwiseOr", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        return runtime.numericExpr(l.token, l).or(runtime.numericExpr(r.token, r));
      }
    });
    define("bitwiseXor", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        return runtime.numericExpr(l.token, l).xor(runtime.numericExpr(r.token, r));
      }
    });
    define("random", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        long left = runtime.numericExpr(l.token, l).longValue();
        long right = runtime.numericExpr(r.token, r).longValue();

        long origin = Math.min(left, right);
        long bound = Math.max(left, right);
        return new RNumber(ThreadLocalRandom.current().nextDouble(origin, bound));
      }
    });
    define("randomFraction", 0, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.random());
      }
    });
    // behaviour not supported
    define("randomSetSeed", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr f = args.get(0);
        runtime.numericExpr(f.token, f);
        return null;
      }
    });
    define("min", -1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        int argsSize = args.size();
        RNumber[] numbers = new RNumber[argsSize];
        for (int i = 0; i < argsSize; i++) {
          Expr arg = args.get(i);
          numbers[i] = runtime.numericExpr(arg.token, arg);
        }
        RNumber min = numbers[0];
        for (RNumber number : numbers) {
          if (min.compareTo(number) > 0) min = number;
        }
        return min;
      }
    });
    define("max", -1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        int argsSize = args.size();
        RNumber[] numbers = new RNumber[argsSize];
        for (int i = 0; i < argsSize; i++) {
          Expr arg = args.get(i);
          numbers[i] = runtime.numericExpr(arg.token, arg);
        }
        RNumber min = numbers[0];
        for (RNumber number : numbers) {
          if (min.compareTo(number) < 0) min = number;
        }
        return min;
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
