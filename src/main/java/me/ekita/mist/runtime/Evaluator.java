package me.ekita.mist.runtime;

import me.ekita.mist.expr.*;
import me.ekita.mist.modules.*;
import me.ekita.mist.modules.definitions.*;
import me.ekita.mist.runtime.memory.Memory;
import me.ekita.mist.runtime.structs.Interrupt;
import me.ekita.mist.runtime.structs.RDict;
import me.ekita.mist.runtime.structs.RList;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Token;
import me.ekita.mist.syntax.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.ekita.mist.runtime.MistEquality.contentEquals;

public class Evaluator implements Expr.Visitor<Object> {

  public final Memory memory = new Memory();
  private final Map<String, Function> functions = new HashMap<>();

  private final Map<String, Module> modules = new HashMap<>();

  public Evaluator() {
    modules.put("Sys", new SysModule());
    modules.put("Math", new MathModule());
    modules.put("Text", new TextModule());
    modules.put("List", new ListModule());
    modules.put("Dict", new DictModule());
    modules.put("Color", new ColorModule());
  }

  private Object unbox(Object value) {
    if (value instanceof Interrupt) return unbox(((Interrupt) value).value);
    return value;
  }

  private Object unboxEval(Expr expr) {
    return unbox(expr.accept(this));
  }

  @Override
  public RNumber number(Num num) {
    // Using ternary operator will mess up coercion
    if (num.isFloat) return new RNumber(Double.parseDouble(num.value));
    return new RNumber(Long.parseLong(num.value));
  }

  public RNumber numericExpr(Token token, Expr expr) {
    Object result = unboxEval(expr);
    if (result instanceof RNumber) return (RNumber) result;
    if (result instanceof String) {
      // try to parse 'em
      String string = (String) result;
      try {
        return new RNumber(string.contains(".") ? Double.parseDouble(string) : Long.parseLong(string));
      } catch (NumberFormatException ignored) {
      }
    }
    return token.error("Expected RNumber but got " + result + " of class " + result.getClass());
  }

  public RNumber numericExpr(Expr expr) {
    Object result = unboxEval(expr);
    if (result instanceof RNumber) return (RNumber) result;
    return expr.token.error("Expected RNumber but got " + result + " of class " + result.getClass());
  }

  public RNumber numericCast(Token token, Object value) {
    if (value instanceof RNumber) return (RNumber) value;
    return token.error("Expected RNumber but got " + value + " of class " + value.getClass());
  }

  public boolean boolExpr(Token token, Expr expr) {
    Object result = unboxEval(expr);
    if (result instanceof Boolean) return (boolean) result;
    return token.error("Expected Bool but got " + result + " of class " + result.getClass());
  }

  public boolean boolExpr(Expr expr) {
    Object result = unboxEval(expr);
    if (result instanceof Boolean) return (boolean) result;
    return expr.token.error("Expected Bool but got " + result + " of class " + result.getClass());
  }

  @Override
  public Boolean bool(Bool bool) {
    return bool.value;
  }

  @Override
  public Object text(Text text) {
    return text.content;
  }

  @Override
  public Object name(Name name) {
    return memory.getVar(false, name.index, name.value);
  }

  @Override
  public Object makeList(MakeList makeList) {
    RList evaluated = new RList();
    for (Expr item : makeList.items) evaluated.add(unboxEval(item));
    return evaluated;
  }

  @Override
  public Object makeDict(MakeDict makeDict) {
    RDict evaluated = new RDict();
    for (Expr entry : makeDict.entries) {
      if (!(entry instanceof Pair)) {
        Object value = unboxEval(entry);
        return makeDict.token.error("Not a valid dictionary entry, got " + value + " of class " + value.getClass());
      }
      evaluated.put(unboxEval(((Pair) entry).key), unboxEval(((Pair) entry).value));
    }
    return evaluated;
  }

  @Override
  public Object pair(Pair pair) {
    List<Object> evaluated = new ArrayList<>();
    evaluated.add(unboxEval(pair.key));
    evaluated.add(unboxEval(pair.value));
    return evaluated;
  }

  @Override
  public Object unary(Unary unary) {
    Token t = unary.token;
    switch (unary.type) {
      case NEGATE:
        return numericExpr(t, unary.expr).negate();
      case EXCLAMATION:
        return !boolExpr(t, unary.expr);
      default:
        return t.error("Unknown unary operator type: " + unary.type);
    }
  }

  @Override
  public Object binary(Binary bin) {
    Token t = bin.token;
    switch (bin.type) {
      case PLUS:
        return performPlus(bin, t);
      case NEGATE:
        return numericExpr(t, bin.left).sub(numericExpr(t, bin.right));
      case TIMES:
        return numericExpr(t, bin.left).mul(numericExpr(t, bin.right));
      case SLASH:
        return numericExpr(t, bin.left).div(numericExpr(t, bin.right));
      case POWER:
        return numericExpr(t, bin.left).pow(numericExpr(t, bin.right));
      case EQUALS:
      case NOT_EQUALS:
        Object first = unboxEval(bin.left);
        Object second = unboxEval(bin.right);
        if (bin.type == Type.EQUALS) return contentEquals(first, second);
        return !contentEquals(first, second);
      case LOGICAL_OR:
        return boolExpr(t, bin.left) || boolExpr(t, bin.right);
      case LOGICAL_AND:
        return boolExpr(t, bin.left) && boolExpr(t, bin.right);
      case LEFT_DIAMOND:
        return mathCompare(t, "<", bin.left, bin.right) < 0;
      case RIGHT_DIAMOND:
        return mathCompare(t, ">", bin.left, bin.right) > 0;
      case LESSER_THAN_EQUALS:
        return mathCompare(t, "<=", bin.left, bin.right) <= 0;
      case GREATER_THAN_EQUALS:
        return mathCompare(t, ">=", bin.left, bin.right) >= 0;
      case COLON:
        RList evaluated = new RList();
        evaluated.add(unboxEval(bin.left));
        evaluated.add(unboxEval(bin.right));
        return evaluated;
      default:
        return t.error("Unknown binary operator type: " + bin.type);
    }
  }

  private int mathCompare(Token token, String operator, Expr left, Expr right) {
    Object result = MistEquality.compare(left.accept(this), right.accept(this));
    if (result instanceof String)
      token.error("Cannot apply operator '" + operator + "': " + result); // indicates an error
    return (int) result;
  }

  private Object performPlus(Binary bin, Token t) {
    Object left = unboxEval(bin.left);
    Object right = unboxEval(bin.right);
    if (left instanceof String || right instanceof String)
      return String.valueOf(left) + right;
    return numericCast(t, left).add(numericCast(t, right));
  }

  @Override
  public Object varSmt(VarSmt smt) {
    Object value = unboxEval(smt.expr);
    memory.declareVar(smt.global, smt.name, value);
    return value;
  }

  @Override
  public Object varSet(VarSet set) {
    // We'll get back to this later
    return memory.setVar(set.global, set.name, set.index, unboxEval(set.value));
  }

  @Override
  public Object varGet(VarGet get) {
    return memory.getVar(get.global, get.index, get.name);
  }

  @Override
  public Object statements(Statements statements) {
    Object result = null;
    for (Expr expr : statements.expressions) {
      result = expr.accept(this);

      if (result instanceof Interrupt) {
        switch (((Interrupt) result).type) {
          case RETURN:
          case CONTINUE:
          case BREAK:
            return result;
        }
      }
    }
    return result;
  }

  @Override
  public Object ifExpr(IfExpr ifExpr) {
    boolean result = boolExpr(ifExpr.token, ifExpr.condition);
    Expr thenBody = ifExpr.thenExpr;
    Expr elseBody = ifExpr.elseExpr;

    Object exprResult = null;
    if (result) {
      memory.enterScope();
      exprResult = thenBody.accept(this);
      memory.leaveScope();
    } else if (elseBody != null) {
      memory.enterScope();
      exprResult = elseBody.accept(this);
      memory.leaveScope();
    }
    return exprResult;
  }

  @Override
  public Object whileLoop(While l) {
    Expr condition = l.condition;
    int numIterations = 0;
    whileLoop:
    while (boolExpr(l.token, condition)) {
      numIterations++;
      memory.enterScope();
      Object result = l.body.accept(this);
      memory.leaveScope();

      if (result instanceof Interrupt) {
        switch (((Interrupt) result).type) {
          case BREAK:
            break whileLoop;
          //case CONTINUE: continue whileLoop;
          case RETURN:
            return ((Interrupt) result).value;
        }
      }
    }
    return numIterations;
  }

  @Override
  public Object forLoop(For f) {
    String name = f.name;
    RNumber current = numericExpr(f.token, f.from);
    RNumber to = numericExpr(f.token, f.to);
    RNumber by = numericExpr(f.token, f.by);

    if (current.compareTo(to) <= 0) {
      // a forward loop
      whileLoop:
      while (current.compareTo(to) <= 0) {
        memory.enterScope();
        memory.declareVar(false, name, current);
        Object result = f.body.accept(this);
        memory.leaveScope();
        current = current.add(by);

        if (result instanceof Interrupt) {
          switch (((Interrupt) result).type) {
            case BREAK:
              break whileLoop;
            //case CONTINUE: continue whileLoop;
            case RETURN:
              return ((Interrupt) result).value;
          }
        }
      }
    } else {
      // a backward loop
      whileLoop:
      while (current.compareTo(to) >= 0) {
        memory.enterScope();
        memory.declareVar(false, name, current);
        Object result = f.body.accept(this);
        memory.leaveScope();
        current = current.sub(by);

        if (result instanceof Interrupt) {
          switch (((Interrupt) result).type) {
            case BREAK:
              break whileLoop;
            //case CONTINUE: continue whileLoop;
            case RETURN:
              return ((Interrupt) result).value;
          }
        }
      }
    }
    return null;
  }

  @Override
  public Object forEach(ForEach f) {
    Object iterable = unboxEval(f.iterable);
    if (!(iterable instanceof List<?>))
      return f.token.error("Expected a list type but got " + iterable + " of class " + iterable.getClass().getName());
    List<Object> elements = (List<Object>) iterable;

    int numIterations = 0;
    forLoop:
    for (Object element : elements) {
      numIterations++;
      memory.enterScope();
      memory.declareVar(false, f.asName, element);
      Object result = f.body.accept(this);
      memory.leaveScope();

      if (result instanceof Interrupt) {
        switch (((Interrupt) result).type) {
          case BREAK:
            break forLoop;
          case RETURN:
            return ((Interrupt) result).value;
        }
      }
    }
    return numIterations;
  }

  @Override
  public Object forEachPair(ForEachPair f) {
    Object iterable = unboxEval(f.iterable);
    if (!(iterable instanceof Map<?, ?>))
      return f.token.error("Expected a map type but got " + iterable + " of class " + iterable.getClass().getName());
    Map<Object, Object> map = (Map<Object, Object>) iterable;

    int numIterations = 0;
    forLoop:
    for (Map.Entry<Object, Object> entry : map.entrySet()) {
      memory.enterScope();
      memory.declareVar(false, f.keyName, entry.getKey());
      memory.declareVar(false, f.valueName, entry.getValue());
      Object result = f.body.accept(this);
      memory.leaveScope();
      if (result instanceof Interrupt) {
        switch (((Interrupt) result).type) {
          case BREAK:
            break forLoop;
          case RETURN:
            return ((Interrupt) result).value;
        }
      }
    }
    return numIterations;
  }

  @Override
  public Object interruptSmt(InterruptSmt iSmt) {
    return new Interrupt(iSmt.token, iSmt.type, iSmt.value == null ? null : iSmt.value.accept(this));
  }

  @Override
  public Object propGet(PropGet get) {
    Module module = modules.get(get.module);
    if (module == null)
      return get.token.error("Cannot find module " + get.module);
    ModPropGet prop = module.getProp(get.property);
    if (prop == null)
      return get.token.error("Cannot find property " + get.property + " in module " + get.module);
    return prop.get();
  }

  @Override
  public Object propSet(PropSet set) {
    Module module = modules.get(set.module);
    if (module == null)
      return set.token.error("Cannot find module " + set.module);
    ModPropSet prop = module.getPropSet(set.property);
    if (prop == null)
      return set.token.error("Cannot find property " + set.property + " in module " + set.module);
    Object value = unboxEval(set.value);
    prop.set(value);
    return value;
  }

  @Override
  public Object functionCall(FunctionCall call) {
    List<Expr> args = call.arguments;
    int argsSize = args.size();

    Function func = functions.get(argsSize + call.name);
    List<String> paramNames = func.parameterNames;

    Object[] evaluatedArgs = new Object[argsSize];
    for (int i = 0; i < argsSize; i++) {
      evaluatedArgs[i] = unboxEval(args.get(i));
    }

    memory.enterScope();
    for (int i = 0; i < argsSize; i++) {
      String paramName = paramNames.get(i);
      Object value = evaluatedArgs[i];
      memory.declareVar(false, paramName, value);
    }
    Object callResult = unboxEval(func.body);
    memory.leaveScope();
    return callResult;
  }

  @Override
  public Object moduleCall(ModuleCall call) {
    Module module = modules.get(call.moduleName);
    if (module == null)
      return call.token.error("Cannot find module " + call.moduleName);
    ModFunction func = module.getFunc(call.funcName, call.arguments.size());
    if (func == null)
      return call.token.error("Cannot find function " + call.funcName + "() of arg size "
          + call.arguments.size() + " in module " + call.moduleName);
    return func.call(call.token, this, call.arguments);
  }

  @Override
  public Object objectCall(ObjectCall call) {
    Object object = unboxEval(call.object);
    String moduleName = getModuleName(call.token, object);
    String methodName = call.methodName;

    Module module = modules.get(moduleName);
    if (module == null)
      return call.token.error("Cannot find module " + moduleName);
    ModMethod method = module.getMethod(methodName, call.arguments.size());
    if (method == null)
      return call.token.error("Cannot find object method " + methodName + "() of arg size "
          + call.arguments.size() + " in module " + moduleName);
    return method.call(call.token, this, object, call.arguments);
  }

  @Override
  public Object transformCall(TransformCall call) {
    Module module = modules.get(call.moduleName);
    if (module == null)
      return call.token.error("Cannot find module " + call.moduleName);
    ModTransformer transformer = module.getTransformer(call.transformerName);
    if (transformer == null)
      return call.token.error("Cannot find transformer '" + call.transformerName + "' in module " + call.moduleName);
    return transformer.transform(call.token, this, call.arguments, call.paramNames, call.body);
  }

  private String getModuleName(Token token, Object value) {
    if (value instanceof String) return "Text";
    else if (value instanceof RNumber) return "Number";
      //else if (value instanceof Boolean) return "Logic";
    else if (value instanceof RList) return "List";
    else if (value instanceof RDict) return "Dict";
    return token.error("Module unknown for value: " + value);
  }

  @Override
  public Object function(Function func) {
    functions.put(func.parameterNames.size() + func.name, func);
    return null;
  }

  @Override
  public Object on(On o) {
    // providing a way for external interaction!
    // We don't need to implement this now :)
    return null;
  }
}
