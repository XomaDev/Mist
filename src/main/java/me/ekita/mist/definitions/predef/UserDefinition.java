package me.ekita.mist.definitions.predef;

import me.ekita.mist.definitions.Definition;
import me.ekita.mist.expr.Expr;
import me.ekita.mist.runtime.memory.Memory;

import java.util.List;

public class UserDefinition extends Definition {

  private final List<String> parameterNames;
  private final boolean returning;
  private final Expr body;

  private final Memory memory;
  private final Expr.Visitor<?> evaluator;

  public UserDefinition(String name,
                        boolean returning,
                        List<String> parameterNames,
                        Expr body,
                        Memory memory,
                        Expr.Visitor<?> evaluator) {
    super(name, parameterNames.size());
    this.returning = returning;
    this.parameterNames = parameterNames;
    this.body = body;

    this.memory = memory;
    this.evaluator = evaluator;
  }

  @Override
  public Object call(Object[] arguments) {
    memory.enterScope();
    int argLength = arguments.length;
    for (int i = 0; i < argLength; i++) {
      String argName = parameterNames.get(i);
      Object arg = arguments[i];
      memory.declareVar(false, argName, arg);
    }
    Object result = body.accept(evaluator);
    memory.leaveScope();
    return result;
  }
}