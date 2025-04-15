package me.ekita.mist.modules.definitions;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.modules.ModFunction;
import me.ekita.mist.modules.ModMethod;
import me.ekita.mist.modules.ModTransformer;
import me.ekita.mist.modules.Module;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.runtime.structs.RList;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static me.ekita.mist.modules.ModuleHelper.asList;

public class ListModule extends Module {

  public ListModule() {
    defineFunc("isList", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return args.get(0).accept(runtime) instanceof List<?>;
      }
    });

    defineMethod("add", -1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        List<Object> list = asList(token, object);
        for (Expr arg : args) list.add(arg.accept(runtime));
        return list.size();
      }
    });
    defineMethod("contains", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return asList(token, object).contains(args.get(0).accept(runtime));
      }
    });
    defineMethod("len", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return new RNumber(asList(token, object).size());
      }
    });
    defineMethod("isEmpty", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return asList(token, object).isEmpty();
      }
    });
    defineMethod("random", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        List<Object> list = asList(token, object);
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
      }
    });
    defineMethod("indexOf", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return asList(token, object).indexOf(args.get(0).accept(runtime)) + 1;
      }
    });
    defineMethod("get", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        // Indexing in AI2 starts with 1
        int index = (int) runtime.numericExpr(args.get(0)).longValue() - 1;
        return asList(token, object).get(index);
      }
    });
    defineMethod("insert", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        int index = (int) runtime.numericExpr(args.get(0)).longValue() - 1;
        Object element = args.get(1).accept(runtime);
        asList(token, object).add(index, element);
        return element;
      }
    });
    defineMethod("set", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        int index = (int) runtime.numericExpr(args.get(0)).longValue() - 1;
        Object element = args.get(1).accept(runtime);
        return asList(token, object).set(index, element);
      }
    });
    defineMethod("remove", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        int index = (int) runtime.numericExpr(args.get(0)).longValue() - 1;
        return asList(token, object).remove(index);
      }
    });
    defineMethod("append", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        List<Object> left = asList(token, object);
        List<Object> right = asList(token, args.get(0).accept(runtime));
        return left.addAll(right);
      }
    });
    defineMethod("copy", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return new RList(asList(token, object));
      }
    });
    defineMethod("reverse", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        List<?> copy = new RList(asList(token, object));
        Collections.reverse(copy);
        return copy;
      }
    });
    defineMethod("allButFirst", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        List<Object> list = new RList(asList(token, object));
        list.remove(0);
        return list;
      }
    });
    defineMethod("allButLast", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        List<Object> list = new RList(asList(token, object));
        list.remove(list.size() - 1);
        return list;
      }
    });

    defineTransformer("map", new ModTransformer() {
      @Override
      public Object transform(Token token,
                              Evaluator runtime,
                              List<Expr> arguments,
                              List<String> paramNames,
                              Expr body) {
        List<Object> elements = asList(token, arguments.get(0).accept(runtime));
        List<Object> transformedElements = new RList();
        String eachElementName = paramNames.get(0);

        for (Object element : elements) {
          runtime.memory.enterScope();
          runtime.memory.declareVar(false, eachElementName, element);
          Object transformedElement = body.accept(runtime);
          transformedElements.add(transformedElement);
          runtime.memory.leaveScope();
        }
        return transformedElements;
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

  @Override
  public ModMethod getMethod(String name, int paramCount) {
    ModMethod method = super.getMethod(name, paramCount);
    // overriding behaviour to support functions with unlimited arguments like min()
    if (method == null) return super.getMethod(name, -1);
    return method;
  }
}
