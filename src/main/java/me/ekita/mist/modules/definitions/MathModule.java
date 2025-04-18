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
    defineFunc("format", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        RNumber number = runtime.numericExpr(token, args.get(0));
        int places = (int) runtime.numericExpr(args.get(1)).longValue();
        if (places == 0) return new RNumber(Math.round(number.doubleValue()));
        if (places > 0) {
          String formatted = String.format("%." + places + "f", number.doubleValue());
          return new RNumber(formatted.contains(".") ? Double.parseDouble(formatted) : Long.parseLong(formatted));
        }
        return token.error("Math.format: Places must be a non-negative integer");
      }
    });
    defineFunc("root", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.sqrt(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("abs", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.abs(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("neg", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return runtime.numericExpr(token, args.get(0)).negate();
      }
    });
    defineFunc("log", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.log(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("exp", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.exp(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("round", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.round(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("ceil", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.ceil(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("floor", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.floor(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("degree", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.toDegrees(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("radians", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.toRadians(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });

    defineFunc("sin", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.sin(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("cos", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.cos(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("tan", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.tan(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("asin", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.asin(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("acos", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.acos(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("atan", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.atan(runtime.numericExpr(token, args.get(0)).doubleValue()));
      }
    });
    defineFunc("atan2", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return new RNumber(Math.atan2(
            runtime.numericExpr(token, args.get(0)).doubleValue(),
            runtime.numericExpr(token, args.get(1)).doubleValue()
        ));
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
