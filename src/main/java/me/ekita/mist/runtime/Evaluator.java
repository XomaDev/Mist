package me.ekita.mist.runtime;

import me.ekita.mist.definitions.Definition;
import me.ekita.mist.definitions.DefinitionGroup;
import me.ekita.mist.definitions.predef.StandardDefinitionGroup;
import me.ekita.mist.definitions.predef.UserDefinitionGroup;
import me.ekita.mist.expr.*;
import me.ekita.mist.runtime.memory.Memory;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Type;

import java.util.ArrayList;
import java.util.List;

public class Evaluator implements Expr.Visitor<Object> {

  private final List<DefinitionGroup> definitionGroups = new ArrayList<>();

  private final Memory memory = new Memory();

  public Evaluator() {
    definitionGroups.add(new StandardDefinitionGroup());
    definitionGroups.add(new UserDefinitionGroup());
  }

  public void addDefinitionGroup(DefinitionGroup group) {
    definitionGroups.add(group);
  }

  @Override
  public RNumber number(Num num) {
    // Using ternary operator will mess up coercion
    if (num.isFloat) return new RNumber(Double.parseDouble(num.value));
    return new RNumber(Long.parseLong(num.value));
  }

  private RNumber numericExpr(Expr expr) {
    Object result = expr.accept(this);
    if (result instanceof RNumber) return (RNumber) result;
    throw new RuntimeException("Expected RNumber but got " + result + " of class " + result.getClass());
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
  public Object binary(Binary bin) {
    switch (bin.type) {
      case PLUS:
        return numericExpr(bin.left).add(numericExpr(bin.right));
      case NEGATE:
        return numericExpr(bin.left).sub(numericExpr(bin.right));
      case TIMES:
        return numericExpr(bin.left).mul(numericExpr(bin.right));
      case SLASH:
        return numericExpr(bin.left).div(numericExpr(bin.right));
      case POWER:
        return numericExpr(bin.left).pow(numericExpr(bin.right));
      case ASSIGNMENT:
        Object result = bin.right.accept(this);
        if (bin.left instanceof GetVar) {
          GetVar getVar = (GetVar) bin.left;
          memory.declareVar(getVar.global, getVar.name, result);
          return result;
        }
        memory.declareVar(false, (String) bin.left.accept(this), result);
        return result;
      case EQUALS:
      case NOT_EQUALS:
        Object left = bin.left.accept(this);
        Object right = bin.right.accept(this);
        if (bin.type == Type.EQUALS) return valueEquals(left, right);
        return !valueEquals(left, right);
      case LEFT_DIAMOND:
        return numericExpr(bin.left).compareTo(numericExpr(bin.right)) < 0;
      case RIGHT_DIAMOND:
        return numericExpr(bin.left).compareTo(numericExpr(bin.right)) > 0;
      default:
        throw new RuntimeException("Unknown operator type: " + bin.type);
    }
  }

  public boolean valueEquals(Object left, Object right) {
    if (left instanceof RNumber && right instanceof RNumber)
      return ((RNumber) left).compareTo((RNumber) right) == 0;
    return left == right;
  }

  @Override
  public Object getVr(GetVar v) {
    return memory.getVar(v.global, v.index, v.name);
  }

  @Override
  public Object setVr(SetVar v) {
    Object result = v.expr.accept(this);
    memory.declareVar(v.global, v.name, result);
    return result;
  }

  @Override
  public Object statements(Statements statements) {
    List<Expr> exprs = statements.expressions;
    int until = exprs.size() - 1;
    for (int i = 0; i < until; i++) exprs.get(i).accept(this);
    return exprs.get(until).accept(this);
  }

  @Override
  public Object ifExpr(IfExpr ifExpr) {
    Object result = ifExpr.condition.accept(this);
    if (!(result instanceof Boolean))
      ifExpr.token.error("Expected type bool for if-expr but got " + result + " of class " + result.getClass());
    Expr thenBody = ifExpr.thenExpr;
    Expr elseBody = ifExpr.elseExpr;

    Object exprResult = null;
    if ((boolean) result) {
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
  public Object forLoop(For f) {
    String name = f.name;
    RNumber current = ((RNumber) f.from.accept(this));
    RNumber to = ((RNumber) f.to.accept(this));
    RNumber by = ((RNumber) f.by.accept(this));

    if (current.compareTo(to) <= 0) {
      // a forward loop
      while (current.compareTo(to) <= 0) {
        memory.enterScope();
        memory.declareVar(false, name, current);
        f.body.accept(this);
        memory.leaveScope();
        current = current.add(by);
      }
    } else {
      // a backward loop
      while (current.compareTo(to) >= 0) {
        memory.enterScope();
        memory.declareVar(false, name, current);
        f.body.accept(this);
        memory.leaveScope();
        current = current.sub(by);
      }
    }
    return null;
  }

  @Override
  public Object functionCall(FunctionCall call) {
    List<Expr> args = call.arguments;
    int argSize = args.size();
    // search for the function down the hierarchy
    for (DefinitionGroup group : definitionGroups) {
      Definition def = group.get(call.name, argSize);
      if (def == null) continue;
      Object[] evaluated = new Object[argSize];
      for (int i = 0; i < argSize; i++) {
        evaluated[i] = args.get(i).accept(this);
      }
      return def.call(evaluated);
    }
    throw new RuntimeException("Could not find method named " + call.name + " of " + argSize + " arguments");
  }

  @Override
  public Object function(Function func) {
    UserDefinitionGroup.define(func.name, func.returning, func.parameterNames, func.body, memory, this);
    return null;
  }
}
