package me.ekita.mist.syntax;

public enum Flag {
  CONTEXT,
  ASSIGNMENT_TYPE,
  OPERATOR,

  LOGICAL_OR, LOGICAL_AND,
  BITWISE_OR, BITWISE_AND, BITWISE_XOR,

  EQUALITY, RELATIONAL, BINARY, UNARY, POSSIBLE_RIGHT_UNARY,

  M_BOOL,
  VALUE,

  WHEN,
  PROCEDURE_HEADER,

  // A new body starts from here e.g. `else` and `elif`
  NEW_BODY,
}
