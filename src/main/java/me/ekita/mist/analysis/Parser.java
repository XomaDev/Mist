package me.ekita.mist.analysis;

import me.ekita.mist.expr.*;
import me.ekita.mist.syntax.Flag;
import me.ekita.mist.syntax.Token;
import me.ekita.mist.syntax.Type;

import java.util.ArrayList;
import java.util.List;

public class Parser {

  private final ScopeManager manager = new ScopeManager();

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
    for (Expr expression : expressions) {
      System.out.println(expression);
    }
    return new Statements(expressions);
  }

  private Expr parseStatement() {
    Token token = eat();
    switch (token.type) {
      case FOR:
        return forExpr(token);
      default:
        back();
        return parseExpr();
    }
  }

  private For forExpr(Token token) {
    manager.enterScope(true);
    String name = readAlpha();
    manager.defineVr(name);
    expect(Type.COLON);
    Expr from = parseExpr();
    expect(Type.TO);
    Expr to = parseExpr();
    Expr by = null;
    if (peek().type == Type.BY) {
      skip();
      by = parseExpr();
    }
    Statements body = body();
    manager.leaveScope(true);
    return new For(token, name, from, to, by, body);
  }

  private Statements body() {
    List<Expr> expressions = new ArrayList<>();
    while (notEOF() && !isNext(Type.CLOSE_CURVE)) {
      expressions.add(parseExpr());
    }
    return new Statements(expressions);
  }

  private Expr parseExpr() {
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
    if (token.hasFlag(Flag.CONTEXT)) {
      return varAccess(token);
    } else if (token.type == Type.OPEN_CURVE) {
      Expr expr = parseStatement();
      expect(Type.CLOSE_CURVE);
      return expr;
    } else if (token.hasFlag(Flag.VALUE)) {
      Expr expr = parseValue(token);
      if (expr instanceof Name && ((Name) expr).index == -2) {
        if (!isNext(Type.OPEN_CURVE)) {
          // ehh, it's not even a function call
          ((Name) expr).invalidate(); // thi'll error out
        }
        // a function call! wohoo!
        return new FunctionCall(token, (String) token.data, arguments());
      }
      return expr;
    }
    return token.error("Unexpected token");
  }

  private Expr varAccess(Token token) {
    // token => `var` or `glob`
    if (isNext(Type.DOT)) {
      // it's a GetVr
      skip();
      String name = readAlpha();
      int vrIndex = manager.resolveVr(name);
      if (vrIndex == -1) throw new RuntimeException("Cannot find symbol '" + name + "'");
      return new GetVar(token, token.type == Type.GLOBAL, name, vrIndex);
    }
    String name = readAlpha();
    expect(Type.ASSIGNMENT);
    Expr expr = parseExpr();
    manager.defineVr(name);
    return new SetVar(token, token.type == Type.GLOBAL, name, expr);
  }

  public Expr parseValue(Token token) {
    switch (token.type) {
      case M_TRUE:
      case M_FALSE:
        return new Bool(token, token.type == Type.M_TRUE);
      case M_NUM:
        return new Num(token, (String) token.data);
      case M_TEXT:
        return new Text(token, (String) token.data);
      case ALPHA:
        String name = (String) token.data;
        // check for variable referencing
        int varIndex = manager.resolveVr(name);
        if (varIndex != -1) return new Name(token, varIndex);
        // maybe it's part of a function call
        return new Name(token, -2);
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
