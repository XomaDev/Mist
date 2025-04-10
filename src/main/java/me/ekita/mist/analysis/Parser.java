package me.ekita.mist.analysis;

import me.ekita.mist.expr.*;
import me.ekita.mist.syntax.Flag;
import me.ekita.mist.syntax.Token;
import me.ekita.mist.syntax.Type;

import java.util.ArrayList;
import java.util.List;

public class Parser {

  private final List<Token> tokens;
  private int index = 0;
  private int size = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public Statements parse() {
    List<Expr> expressions = new ArrayList<>();
    while (notEOF()) {
      expressions.add(parseStatement());
    }
    return new Statements(expressions);
  }

  public Expr parseStatement() {
    Token token = eat();
    switch (token.type) {
      default:
        back();
        return parseExpr();
    }
  }

  public Expr parseExpr() {
    Expr left = parseElement();
    while (notEOF()) {
      Token op = peek();
      if (!op.hasFlag(Flag.OPERATOR)) return left;
      skip();
      Expr right = parseElement();
      left = new Binary(op, left, right);
    }
    return left;
  }

  public Expr parseElement() {
    Expr left = parseTerm();
    // for now, we don't have a lot to do here
    return left;
  }

  public Expr parseTerm() {
    Token token = eat();
    if (token.hasFlag(Flag.VALUE)) {
      Expr expr = parseValue(token);
      if (expr instanceof Name && notEOF() && isNext(Type.OPEN_CURVE)) {
        // a function call! wohoo!
        return new MethodCall(token, (String) token.data, arguments());
      }
      return expr;
    }
    return token.error("Unexpected token");
  }

  public Expr parseValue(Token token) {
    switch (token.type) {
      case M_TRUE:
      case M_FALSE:
        return new Bool(token, token.type == Type.M_TRUE);
      case M_NUM:
        return new Num(token, (String) token.data);
      case ALPHA:
        return new Name(token);
      default:
        return token.error("Unknown value type");
    }
  }

  private List<Expr> arguments() {
    expect(Type.OPEN_CURVE);
    if (isNext(Type.CLOSE_CURVE)) {
      skip();
      return new ArrayList<>();
    }
    List<Expr> arguments = new ArrayList<>();
    while (notEOF()) {
      arguments.add(parseStatement());
      if (!isNext(Type.COMMA)) break;
      skip();
    }
    expect(Type.CLOSE_CURVE);
    return arguments;
  }

  private String readAlpha() {
    Token token = tokens.get(index++);
    if (token.type == Type.ALPHA) return (String) token.data;
    return token.error("Expected type alpha but got " + token.type);
  }

  private Token expect(Type type) {
    Token token = tokens.get(index++);
    if (token.type == type) return token;
    return token.error("Expected token type " + type + " but got " + token.type);
  }

  private boolean isNext(Type type) {
    return tokens.get(index).type == type;
  }

  private void back() {
    index--;
  }

  private void skip() {
    index++;
  }

  private Token eat() {
    return tokens.get(index++);
  }

  private Token peek() {
    return tokens.get(index);
  }

  private boolean notEOF() {
    return index < tokens.size();
  }

  private boolean isEOF() {
    return index == size;
  }
}
