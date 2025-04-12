package me.ekita.mist.modules;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Token;

import java.util.List;

import static me.ekita.mist.modules.ModuleHelper.asMap;

public class DictModule extends Module {
  public DictModule() {
    defineMethod("get", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Object key = args.get(0).accept(runtime), defaultValue = args.get(1).accept(runtime);
        Object value = asMap(token, object).get(key);
        return value == null ? defaultValue : value;
      }
    });

    defineMethod("set", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Object key = args.get(0).accept(runtime), value = args.get(1).accept(runtime);
        asMap(token, object).put(key, value);
        return value;
      }
    });

    defineMethod("remove", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Object key = args.get(0).accept(runtime);
        return asMap(token, object).remove(key);
      }
    });
  }
}
