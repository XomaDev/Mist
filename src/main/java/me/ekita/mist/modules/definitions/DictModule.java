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

import java.util.*;

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

        for (Object pair : pairs) {
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
        RList keys = (RList) asList(keysExpr.token, keysExpr.accept(runtime));
        Object defaultValue = args.get(1).accept(runtime);
        Map<Object, Object> map = asMap(token, object);

        Object result = getValueAtKeyPath(keys, map);
        return result == null ? defaultValue : result;
      }
    });

    defineMethod("setAtPath", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr keysExpr = args.get(0);
        RList keys = (RList) asList(keysExpr.token, keysExpr);
        Object value = args.get(1).accept(runtime);
        Map<Object, Object> currMap = asMap(token, object);

        setValueAtKeyPath(keys, currMap, value);
        return value;
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
        Map<Object, Object> intoMap = asMap(token, args.get(0).accept(runtime));
        intoMap.putAll(ourMap);
        return intoMap;
      }
    });

    defineFunc("wallAll", 0, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return WALK_ALL;
      }
    });

    defineMethod("walk", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        RDict dict = (RDict) asMap(token, object);
        RList list = (RList) asList(token, args.get(0).accept(runtime));
        return walkKeyPath(dict, list, 0, new RList());
      }
    });
  }

  private static int keyToIndexSafe(Object key) {
    if (key instanceof RNumber) {
      return ((RNumber) key).number.intValue() - 1;
    } else {
      try {
        return Integer.parseInt(key.toString()) - 1;
      } catch (NumberFormatException ignored) {
      }
    }
    return -1;
  }

  private static int keyToIndex(Object key) {
    int index = keyToIndexSafe(key);
    if (index == -1) {
      throw new NumberFormatException("Wrong lookup request, expected type index, but got " + key);
    }
    return index;
  }

  private static Object lookupTargetForKey(Object target, Object key) {
    if (target instanceof RDict) {
      return ((RDict) target).get(key);
    } else if (target instanceof RList) {
      return ((RList) target).get(keyToIndex(key));
    } else {
      throw new IllegalArgumentException("Invalid value in path, got type " + target.getClass().getSimpleName());
    }
  }

  private static void setValueAtKeyPath(RList keys, Object target, Object value) {
    if (keys.isEmpty()) return;
    Iterator<?> it = keys.iterator();
    while (it.hasNext()) {
      Object key = it.next();
      if (it.hasNext()) {
        target = lookupTargetForKey(target, key);
      } else {
        if (target instanceof RDict) {
          ((RDict) target).put(key, value);
        } else if (target instanceof RList) {
          // TODO:
          //  Pair access might be unstable
          RList list = (RList) target;
          int index = keyToIndex(key);
          if (index != -1) {
            list.set(index, value);
          } else {
            if (list.get(0).equals(key)) list.set(1, value);
          }
        } else {
          throw new IllegalArgumentException("Invalid value in path, got type " + target.getClass().getSimpleName());
        }
      }
    }
  }

  private static Object getValueAtKeyPath(RList keys, Object target) {
    for (Object currKey : keys) {
      if (target instanceof RDict) {
        target = ((RDict) target).get(currKey);
      } else if (target instanceof RList) {
        RList list = (RList) target;
        // TODO: pair access may be unstable
        int index = keyToIndexSafe(currKey);
        if (index != -1) {
          return list.get(index);
        } else if (list.size() == 2 && list.get(0).equals(currKey)) {
          return list.get(1);
        }
        target = ((RList) target).get(keyToIndex(currKey));
      }
    }
    return target;
  }

  private static Collection<Object> allOf(Object object) {
    if (object instanceof RDict) {
      return ((RDict) object).values();
    } else if (object instanceof RList) {
      return (RList) object;
    }
    return Collections.emptyList();
  }

  private static List<Object> walkKeyPath(Object root, RList keys, int currIndex, List<Object> result) {
    if (keys.isEmpty()) {
      if (result != null) result.add(root);
      return result;
    } else if (root == null) {
      return result;
    }

    Object currentKey = keys.get(currIndex++);
    if (currentKey == WALK_ALL) {
      for (Object child: allOf(root)) {
        walkKeyPath(child, keys, currIndex, result);
      }
    } else if (root instanceof RDict) {
      walkKeyPath(((RDict) root).get(currentKey), keys, currIndex, result);
    } else if (root instanceof RList) {
      RList list = (RList) root;
      int index = keyToIndex(currentKey);
      if (index != -1) {
        try {
          walkKeyPath(list.get(index), keys, currIndex, result);
        } catch (Exception ignored) {
        }
      }
    }
    return result;
  }

  public static final Object WALK_ALL = new Object() {
    @Override
    public String toString() {
      return "ALL_ITEMS";
    }
  };
}
