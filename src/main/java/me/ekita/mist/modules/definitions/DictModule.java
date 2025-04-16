package me.ekita.mist.modules.definitions;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.modules.ModFunction;
import me.ekita.mist.modules.ModMethod;
import me.ekita.mist.modules.Module;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.runtime.structs.RDict;
import me.ekita.mist.runtime.structs.RList;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Token;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static me.ekita.mist.modules.ModuleHelper.asList;
import static me.ekita.mist.modules.ModuleHelper.asMap;

public class DictModule extends Module {
  public DictModule() {
    defineFunc("pairsToDict", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr pairsExpr = args.get(0);
        List<Object> pairs = asList(pairsExpr.token, pairsExpr.accept(runtime));
        RDict dict = new RDict();

        for (Object pair: pairs) {
          if (!(pair instanceof List<?>)) {
            return token.error("Expected a valid pair but got " + pair + " of class " + pair.getClass().getName());
          }
          List<Object> entry = (List<Object>) pair;
          if (entry.size() != 2) {
            return token.error("Expected pair of element size 2 but got " + entry.size());
          }
          dict.put(entry.get(0), entry.get(1));
        }
        return dict;
      }
    });

    defineFunc("isDict", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return args.get(0).accept(runtime) instanceof Map<?, ?>;
      }
    });

    defineMethod("get", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Object key = args.get(0).accept(runtime);
        Object value = asMap(token, object).get(key);
        return value == null ? args.get(1).accept(runtime) : value;
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

    defineMethod("getAtPath", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr keysExpr = args.get(0);
        Iterator<Object> keys = asList(keysExpr.token, keysExpr.accept(runtime)).iterator();
        Object defaultValue = args.get(1).accept(runtime);

        final Map<Object, Object> map = asMap(token, object);
        Object result = map;

        while (keys.hasNext()) {
          Object key = keys.next();
          result = ((Map<Object, Object>) result).get(key);

          if (result == null) break;
          if (keys.hasNext() && !(result instanceof Map<?, ?>)) {
            result = null;
            break;
          }
        }

        return (result == null || result == map) ?  defaultValue: result;
      }
    });

    defineMethod("setAtPath", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr keysExpr = args.get(0);
        Iterator<Object> keys = asList(keysExpr.token, keysExpr).iterator();
        Object value = args.get(1).accept(runtime);

        Map<Object, Object> currMap = asMap(token, object);

        while (keys.hasNext()) {
          Object key = keys.next();
          boolean isEnd = !keys.hasNext();
          // set the currMap
          if (isEnd) currMap.put(key, value);
          else {
            Object nextMap = currMap.get(key);
            if (nextMap instanceof Map<?, ?>) currMap = (Map<Object, Object>) nextMap;
            else token.error("Expected a map but got " + nextMap + " at key " + key);
          }
        }
        return null;
      }
    });

    defineMethod("keys", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        List<Object> keys = new RList();
        keys.addAll(asMap(token, object).keySet());
        return keys;
      }
    });

    defineMethod("values", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        List<Object> keys = new RList();
        keys.addAll(asMap(token, object).values());
        return keys;
      }
    });

    defineMethod("hasKey", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return asMap(token, object).containsKey(args.get(0).accept(runtime));
      }
    });

    defineMethod("len", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return new RNumber(asMap(token, object).size());
      }
    });

    defineMethod("toPairs", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        RList list = new RList();
        Map<Object, Object> map = asMap(token, object);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
          RList entryList = new RList();
          entryList.add(entry.getKey());
          entryList.add(entry.getValue());
          list.add(entryList);
        }
        return list;
      }
    });

    defineMethod("copy", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return new RDict(asMap(token, object));
      }
    });

    defineMethod("mergeInto", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Map<Object, Object> ourMap = asMap(token, object);
        Map<Object, Object> intoMap = asMap(token, object);
        intoMap.putAll(ourMap);
        return intoMap;
      }
    });
  }
}
