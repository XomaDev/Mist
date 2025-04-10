package me.ekita.mist.syntax;

import java.util.HashMap;

public enum Type {
  LOGICAL_AND, LOGICAL_OR,
  //BITWISE_AND, BITWISE_OR, BITWISE_XOR, <-- moved to math.and(), math.or(), math.xor() function

  EQUALS, NOT_EQUALS,
  RIGHT_DIAMOND, LEFT_DIAMOND,
  GREATER_THAN_EQUALS, LESSER_THAN_EQUALS,

  SLASH, TIMES, REMAINDER, POWER,
  PLUS, NEGATE,
  EXCLAMATION,

  ASSIGNMENT,
  DOT,

  OPEN_CURVE, CLOSE_CURVE,
  OPEN_CURLY, CLOSE_CURLY,
  OPEN_SQUARE, CLOSE_SQUARE,
  COMMA,

  M_NUM, M_BOOL, M_TEXT, M_LIST, M_DICT,

  ALPHA,
  M_TRUE, M_FALSE,

  GLOBAL, LOCAL,

  IF, ELSE,
  TO, BY, IN, WITH,

  MAKE_LIST, MAKE_DICT,

  WHEN,
  VOID, RET,

  RETURN, BREAK, CONTINUE,

  ;

  @Override
  public String toString() {
    return name().toLowerCase();
  }

  static final HashMap<String, StaticToken> SYMBOLS;
  static final HashMap<String, StaticToken> KEYWORDS;

  static {
    SYMBOLS = new HashMap<String, StaticToken>() {{
      put("=", new StaticToken(Type.ASSIGNMENT, Flag.ASSIGNMENT_TYPE, Flag.OPERATOR));

      put("or", new StaticToken(LOGICAL_OR, Flag.LOGICAL_OR, Flag.OPERATOR));
      put("and", new StaticToken(LOGICAL_AND, Flag.LOGICAL_AND, Flag.OPERATOR));

      put("==", new StaticToken(Type.EQUALS, Flag.EQUALITY, Flag.OPERATOR));
      put("!=", new StaticToken(Type.NOT_EQUALS, Flag.EQUALITY, Flag.OPERATOR));

      put(">", new StaticToken(RIGHT_DIAMOND, Flag.RELATIONAL, Flag.OPERATOR));
      put("<", new StaticToken(LEFT_DIAMOND, Flag.RELATIONAL, Flag.OPERATOR));
      put(">=", new StaticToken(Type.GREATER_THAN_EQUALS, Flag.RELATIONAL, Flag.OPERATOR));
      put("<=", new StaticToken(Type.LESSER_THAN_EQUALS, Flag.RELATIONAL, Flag.OPERATOR));

      put("/", new StaticToken(Type.SLASH, Flag.BINARY, Flag.OPERATOR));
      put("*", new StaticToken(Type.TIMES, Flag.BINARY, Flag.OPERATOR));
      put("^", new StaticToken(Type.POWER, Flag.BINARY, Flag.OPERATOR));

      put("+", new StaticToken(Type.PLUS, Flag.UNARY, Flag.OPERATOR));
      put("-", new StaticToken(Type.NEGATE, Flag.UNARY, Flag.OPERATOR));

      put("!", new StaticToken(Type.EXCLAMATION, Flag.UNARY));

      put(".", new StaticToken(Type.DOT));

      put("(", new StaticToken(Type.OPEN_CURVE));
      put(")", new StaticToken(Type.CLOSE_CURVE));
      put("[", new StaticToken(Type.OPEN_SQUARE));
      put("]", new StaticToken(Type.CLOSE_SQUARE));
      put("{", new StaticToken(Type.OPEN_CURLY));
      put("}", new StaticToken(Type.CLOSE_CURLY));

      put(",", new StaticToken(Type.COMMA));
    }};

    KEYWORDS = new HashMap<String, StaticToken>() {{
      put("true", new StaticToken(M_TRUE, Flag.VALUE, Flag.M_BOOL));
      put("false", new StaticToken(M_FALSE, Flag.VALUE, Flag.M_BOOL));

      put("glob", new StaticToken(GLOBAL, Flag.CONTEXT));
      put("loc", new StaticToken(LOCAL, Flag.CONTEXT));

      put("if", new StaticToken(IF));
      put("else", new StaticToken(ELSE));

      put("return", new StaticToken(RETURN));
      put("break", new StaticToken(BREAK));
      put("continue", new StaticToken(CONTINUE));

      put("to", new StaticToken(TO));
      put("by", new StaticToken(BY));
      put("in", new StaticToken(IN));
      put("with", new StaticToken(WITH));

      put("when", new StaticToken(WHEN, Flag.WHEN));
      put("void", new StaticToken(VOID, Flag.PROCEDURE_HEADER));
      put("ret", new StaticToken(RET, Flag.PROCEDURE_HEADER));
    }};
  }
}
