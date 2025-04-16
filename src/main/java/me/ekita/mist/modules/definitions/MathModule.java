package me.ekita.mist.modules.definitions;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.modules.ModFunction;
import me.ekita.mist.modules.Module;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Token;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static me.ekita.mist.modules.ModuleHelper.asString;


public class MathModule extends Module {

  public MathModule() {
    defineFunc("isNum", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return args.get(0).accept(runtime) instanceof RNumber;
      }
    });
    defineFunc("bitwiseAnd", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        return runtime.numericExpr(l.token, l).and(runtime.numericExpr(r.token, r));
      }
    });
    defineFunc("bitwiseOr", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        return runtime.numericExpr(l.token, l).or(runtime.numericExpr(r.token, r));
      }
    });
    defineFunc("bitwiseXor", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr l = args.get(0), r = args.get(1);
        return runtime.numericExpr(l.token, l).xor(runtime.numericExpr(r.token, r));
      }
    });
    defineFunc("random", 2, new ModFunction() {
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
    defineFunc("randomFraction", 0, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.random());
      }
    });
    // behaviour not supported
    defineFunc("randomSetSeed", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr f = args.get(0);
        runtime.numericExpr(f.token, f);
        return null;
      }
    });
    defineFunc("min", -1, new ModFunction() {
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
    defineFunc("max", -1, new ModFunction() {
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
    defineFunc("decToHex", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        RNumber number = runtime.numericExpr(token, args.get(0));
        return Long.toHexString(number.longValue());
      }
    });
    defineFunc("hexToDec", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        String hexadecimal = asString(token, args.get(0).accept(runtime));
        return new RNumber(Long.parseLong(hexadecimal, 16));
      }
    });
    defineFunc("decToBin", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        RNumber number = runtime.numericExpr(token, args.get(0));
        return Long.toBinaryString(number.longValue());
      }
    });
    defineFunc("binToDec", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        String hexadecimal = asString(token, args.get(0).accept(runtime));
        return new RNumber(Long.parseLong(hexadecimal, 2));
      }
    });
  }

  @Override
  public ModFunction getFunc(String name, int paramCount) {
    ModFunction func = super.getFunc(name, paramCount);
    // overriding behaviour to support functions with unlimited arguments like min()
    if (func == null) return super.getFunc(name, -1);
    return func;
  }
}
