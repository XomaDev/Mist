package me.ekita.mist.runtime;

import me.ekita.mist.definitions.Definition;
import me.ekita.mist.definitions.DefinitionGroup;
import me.ekita.mist.definitions.standard.StandardDefinition;
import me.ekita.mist.expr.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;

public class Evaluator implements Expr.Visitor<Object> {

  private final DefinitionGroup defaultDefnGroup = new StandardDefinition();

  @Override
  public Number number(Num num) {
    return num.isFloat ? new BigDecimal(num.value) : new BigInteger(num.value);
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
    switch (binary.type) {
      case PLUS:
        if (left instanceof BigInteger && right instanceof BigInteger)
          return ((BigInteger) left).add((BigInteger) right);
        if (left instanceof BigDecimal && right instanceof BigDecimal)
          return ((BigDecimal) left).add((BigDecimal) right);
        if (left instanceof BigInteger)
          return new BigDecimal((BigInteger) left).add((BigDecimal) right);
        assert left instanceof BigDecimal;
        return ((BigDecimal) left).add(new BigDecimal((BigInteger) right));
      case NEGATE:
        if (left instanceof BigInteger && right instanceof BigInteger)
          return ((BigInteger) left).subtract((BigInteger) right);
        if (left instanceof BigDecimal && right instanceof BigDecimal)
          return ((BigDecimal) left).subtract((BigDecimal) right);
        if (left instanceof BigInteger)
          return new BigDecimal((BigInteger) left).subtract((BigDecimal) right);
        assert left instanceof BigDecimal;
        return ((BigDecimal) left).subtract(new BigDecimal((BigInteger) right));
      case TIMES:
        if (left instanceof BigInteger && right instanceof BigInteger)
          return ((BigInteger) left).multiply((BigInteger) right);
        if (left instanceof BigDecimal && right instanceof BigDecimal)
          return ((BigDecimal) left).multiply((BigDecimal) right);
        if (left instanceof BigInteger)
          return new BigDecimal((BigInteger) left).multiply((BigDecimal) right);
        assert left instanceof BigDecimal;
        return ((BigDecimal) left).multiply(new BigDecimal((BigInteger) right));
      case SLASH:
        if (left instanceof BigInteger && right instanceof BigInteger)
          return ((BigInteger) left).divide((BigInteger) right);
        if (left instanceof BigDecimal && right instanceof BigDecimal)
          return ((BigDecimal) left).divide((BigDecimal) right, MathContext.DECIMAL128);
        if (left instanceof BigInteger)
          return new BigDecimal((BigInteger) left).divide((BigDecimal) right, MathContext.DECIMAL128);
        assert left instanceof BigDecimal;
        return ((BigDecimal) left).divide(new BigDecimal((BigInteger) right), MathContext.DECIMAL128);
    }
    return null;
  }

  @Override
  public Object statements(Statements statements) {
    for (Expr expr : statements.expressions) {
      expr.accept(this);
    }
    return null;
  }

  @Override
  public Object methodCall(MethodCall call) {
    List<Expr> args = call.arguments;
    int argSize = args.size();
    Definition def = defaultDefnGroup.get(call.name, argSize);
    if (def == null) {
      throw new RuntimeException("Could not find method named " + call.name + " of " + argSize + " arguments");
    }
    Object[] evaluated = new Object[argSize];
    for (int i = 0; i < argSize; i++) {
      evaluated[i] = args.get(i).accept(this);
    }
    return def.call(evaluated);
  }
}
