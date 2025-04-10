package me.ekita.mist.definitions.predef;

import me.ekita.mist.definitions.Definition;
import me.ekita.mist.definitions.DefinitionGroup;
import me.ekita.mist.expr.Expr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDefinitionGroup extends DefinitionGroup {

  private static final Map<String, UserDefinition> definitions = new HashMap<>();

  public static void define(String name,
                            boolean returning,
                            List<String> parameterNames,
                            Expr body,
                            Expr.Visitor<?> evaluator) {
    definitions.put(parameterNames.size() + name, new UserDefinition(name, returning, parameterNames, body, evaluator));
  }

  @Override
  public Definition get(String name, int paramCount) {
    return definitions.get(paramCount + name);
  }
}
