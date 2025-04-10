package me.ekita.mist.runtime;

import me.ekita.mist.definitions.Definition;
import me.ekita.mist.definitions.DefinitionGroup;
import me.ekita.mist.definitions.predef.StandardDefinitionGroup;
import me.ekita.mist.definitions.predef.UserDefinitionGroup;
import me.ekita.mist.expr.*;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Type;

import java.util.ArrayList;
import java.util.List;

public class Evaluator implements Expr.Visitor<Object> {

  private final List<DefinitionGroup> definitionGroups = new ArrayList<>();

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
  public Object binary(Binary binary) {
    Expr left = binary.left;
    Expr right = binary.right;
    switch (binary.type) {
      case PLUS:
        return numericExpr(left).add(numericExpr(right));
      case NEGATE:
        return numericExpr(left).sub(numericExpr(right));
      case TIMES:
        return numericExpr(left).mul(numericExpr(right));
      case SLASH:
        return numericExpr(left).div(numericExpr(right));
      case POWER:
        return numericExpr(left).pow(numericExpr(right));
      case ASSIGNMENT:
        Var var = (Var) left;
        Object result = right.accept(this);
        setVar(var.global, var.name, result);
        return result;
      default:
        throw new RuntimeException("Unknown operator type: " + binary.type);
    }
  }

  @Override
  public Object varExpr(Var v) {
    // here'll we'll simply return the value
    return null;
  }

  public void setVar(boolean global, String name, Object value) {
    // TODO!
  }

  @Override
  public Object statements(Statements statements) {
    for (Expr expr : statements.expressions) expr.accept(this);
    return null;
  }

  @Override
  public Object forLoop(For f) {
    RNumber current = ((RNumber) f.from.accept(this));
    RNumber to = ((RNumber) f.to.accept(this));
    RNumber by = ((RNumber) f.by.accept(this));

    if (current.compareTo(to) <= 0) {
      // a forward loop
      while (current.compareTo(to) < 0) {
        f.body.accept(this);
        current = current.add(by);
      }
    } else {
      // a backward loop
      while (current.compareTo(to) > 0) {
        f.body.accept(this);
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
    UserDefinitionGroup.define(func.name, func.returning, func.parameterNames, func.body, this);
    return null;
  }
}
