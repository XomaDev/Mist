package me.ekita.mist.modules.definitions;

import javafx.util.Pair;
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
import java.util.Comparator;
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
    defineMethod("join", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        StringBuilder builder = new StringBuilder();
        String separator = args.get(0).accept(runtime).toString();
        for (Object element : asList(token, object)) {
          builder.append(element).append(separator);
        }
        if (builder.length() > 0) {
          builder.setLength(builder.length() - separator.length());
        }
        return builder.toString();
      }
    });
    // TODO: Define Method: Make a List sorted
    //  We'll need to study how they have implemented the sorting mechanism

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
    defineTransformer("filter", new ModTransformer() {
      @Override
      public Object transform(Token token,
                              Evaluator runtime,
                              List<Expr> arguments,
                              List<String> paramNames,
                              Expr body) {
        List<Object> elements = asList(token, arguments.get(0).accept(runtime));
        List<Object> filteredElements = new RList();
        String eachElementName = paramNames.get(0);

        for (Object element : elements) {
          runtime.memory.enterScope();
          runtime.memory.declareVar(false, eachElementName, element);
          boolean keep = runtime.boolExpr(body.token, body);
          if (keep) filteredElements.add(element);
          runtime.memory.leaveScope();
        }
        return filteredElements;
      }
    });
    defineTransformer("reduce", new ModTransformer() {
      @Override
      public Object transform(Token token,
                              Evaluator runtime,
                              List<Expr> arguments,
                              List<String> paramNames,
                              Expr body) {
        List<Object> elements = asList(token, arguments.get(0).accept(runtime));
        Object answerSoFar = arguments.get(1).accept(runtime);

        String eachElementName = paramNames.get(0), answerSoFarName = paramNames.get(1);
        for (Object element : elements) {
          runtime.memory.enterScope();
          runtime.memory.declareVar(false, eachElementName, element);
          runtime.memory.declareVar(false, answerSoFarName, answerSoFar);
          answerSoFar = body.accept(runtime);
          runtime.memory.leaveScope();
        }
        return answerSoFar;
      }
    });
    defineTransformer("sort", new ModTransformer() {
      @Override
      public Object transform(Token token,
                              final Evaluator runtime,
                              List<Expr> arguments,
                              List<String> paramNames,
                              final Expr body) {
        List<Object> elements = new RList(asList(token, arguments.get(0).accept(runtime)));
        final String firstElementName = paramNames.get(0), secondElementName = paramNames.get(1);
        Collections.sort(elements, new Comparator<Object>() {
          @Override
          public int compare(Object o1, Object o2) {
            runtime.memory.enterScope();
            runtime.memory.declareVar(false, firstElementName, o1);
            runtime.memory.declareVar(false, secondElementName, o2);
            boolean o1PrecedesO2 = runtime.boolExpr(body.token, body);
            runtime.memory.leaveScope();
            return o1PrecedesO2 ? -1 : 1;
          }
        });
        return elements;
      }
    });
    defineTransformer("sortKey", new ModTransformer() {
      @Override
      public Object transform(Token token,
                              Evaluator runtime,
                              List<Expr> arguments,
                              List<String> paramNames, Expr body) {
        List<Object> elements = asList(token, arguments.get(0).accept(runtime));
        List<Pair<Object, Object>> pairs = new ArrayList<>();
        String eachElementName = paramNames.get(0);

        for (Object element : elements) {
          runtime.memory.enterScope();
          runtime.memory.declareVar(false, eachElementName, element);
          Object key = body.accept(runtime);
          pairs.add(new Pair<>(key, element));
          runtime.memory.leaveScope();
        }
        // TODO: Sort using Collection class, then create new RList and fill in sorted collection values
        //   and then return it.
        //   For this we'll first need to figure what sorting mechanism appinventor uses by default
        return null;
      }
    });
    defineTransformer("sortMin", new ModTransformer() {
      @Override
      public Object transform(Token token,
                              final Evaluator runtime,
                              List<Expr> arguments,
                              List<String> paramNames,
                              final Expr body) {
        List<Object> elements = new RList(asList(token, arguments.get(0).accept(runtime)));
        final String firstElementName = paramNames.get(0), secondElementName = paramNames.get(1);
        Collections.sort(elements, new Comparator<Object>() {
          @Override
          public int compare(Object o1, Object o2) {
            runtime.memory.enterScope();
            runtime.memory.declareVar(false, firstElementName, o1);
            runtime.memory.declareVar(false, secondElementName, o2);
            boolean o1PrecedesO2 = runtime.boolExpr(body.token, body);
            runtime.memory.leaveScope();
            return o1PrecedesO2 ? -1 : 1;
          }
        });
        return elements.isEmpty() ? elements : elements.get(0);
      }
    });
    defineTransformer("sortMax", new ModTransformer() {
      @Override
      public Object transform(Token token,
                              final Evaluator runtime,
                              List<Expr> arguments,
                              List<String> paramNames,
                              final Expr body) {
        List<Object> elements = new RList(asList(token, arguments.get(0).accept(runtime)));
        final String firstElementName = paramNames.get(0), secondElementName = paramNames.get(1);
        Collections.sort(elements, new Comparator<Object>() {
          @Override
          public int compare(Object o1, Object o2) {
            runtime.memory.enterScope();
            runtime.memory.declareVar(false, firstElementName, o1);
            runtime.memory.declareVar(false, secondElementName, o2);
            boolean o1PrecedesO2 = runtime.boolExpr(body.token, body);
            runtime.memory.leaveScope();
            return o1PrecedesO2 ? -1 : 1;
          }
        });
        return elements.isEmpty() ? elements : elements.get(elements.size() - 1);
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
