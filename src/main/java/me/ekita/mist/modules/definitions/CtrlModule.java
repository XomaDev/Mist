package me.ekita.mist.modules.definitions;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.modules.ModFunction;
import me.ekita.mist.modules.ModPropGet;
import me.ekita.mist.modules.Module;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Token;

import java.util.List;

public class CtrlModule extends Module {
  public CtrlModule() {
    defineFunc("closeScreenWithPlainText", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return args.get(0).accept(runtime);
      }
    });
    defineFunc("closeWithValue", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return args.get(0).accept(runtime);
      }
    });
    defineFunc("closeApp", 0, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return false;
      }
    });
    defineFunc("closeScreen", 0, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return false;
      }
    });
    defineFunc("openScreen", 2, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        args.get(0).accept(runtime);
        args.get(1).accept(runtime);
        return false;
      }
    });

    definePropGet("plainStartValue", new ModPropGet() {
      @Override
      public Object get() {
        return "mist_not_found";
      }
    });
    definePropGet("startValue", new ModPropGet() {
      @Override
      public Object get() {
        return "mist_not_found";
      }
    });
  }
}
