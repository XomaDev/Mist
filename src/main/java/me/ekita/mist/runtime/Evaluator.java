package me.ekita.mist.runtime;

import me.ekita.mist.definitions.Definition;
import me.ekita.mist.definitions.DefinitionGroup;
import me.ekita.mist.definitions.predef.StandardDefinitionGroup;
import me.ekita.mist.definitions.predef.UserDefinitionGroup;
import me.ekita.mist.expr.*;
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
  public Number number(Num num) {
    // Using ternary operator will mess up coercion
    if (num.isFloat) return Double.parseDouble(num.value);
    return Long.parseLong(num.value);
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
  public Number binary(Binary binary) {
    Number left = (Number) binary.left.accept(this);
    Number right = (Number) binary.right.accept(this);
    return binaryOp(binary.type, left, right);
  }

  private Number binaryOp(Type type, Number left, Number right) {
    switch (type) {
      case PLUS:
        if (left instanceof Long && right instanceof Long) return left.longValue() + right.longValue();
        return left.doubleValue() + right.doubleValue();
      case NEGATE:
        if (left instanceof Long && right instanceof Long) return left.longValue() - right.longValue();
        return left.doubleValue() - right.doubleValue();
      case TIMES:
        if (left instanceof Long && right instanceof Long) return left.longValue() * right.longValue();
        return left.doubleValue() * right.doubleValue();
      case SLASH:
        if (left instanceof Long && right instanceof Long) return left.longValue() / right.longValue();
        return left.doubleValue() / right.doubleValue();
      case POWER:
        if (left instanceof Long && right instanceof Long) return Math.pow(left.longValue(), right.longValue());
        return Math.pow(left.doubleValue(), right.doubleValue());
      default:
        throw new RuntimeException("Unknown operator type: " + type);
    }
  }

  @Override
  public Object statements(Statements statements) {
    for (Expr expr : statements.expressions) expr.accept(this);
    return null;
  }

  @Override
  public Object forLoop(For f) {
    Number current = ((Number) f.from.accept(this));
    Number to = ((Number) f.to.accept(this));
    Number by = ((Number) f.by.accept(this));

    if (current.doubleValue() <= to.doubleValue()) {
      // a forward loop
      while (current.doubleValue() < to.doubleValue()) {
        f.body.accept(this);
        current = binaryOp(Type.PLUS, current, by);
      }
    } else {
      // a backward loop
      while (current.doubleValue() > to.doubleValue()) {
        f.body.accept(this);
        current = binaryOp(Type.NEGATE, current, by);;
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
