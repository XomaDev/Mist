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
    this.size = tokens.size();
  }

  public Statements parse() {
    List<Expr> expressions = new ArrayList<>();
    while (notEOF()) {
      expressions.add(parseStatement());
    }
    for (Expr expression : expressions) {
      System.out.println(expression);
    }
    System.out.println();
    return new Statements(expressions);
  }

  private Expr parseStatement() {
    Token token = eat();
    switch (token.type) {
      case VAR:
      case GLOBAL:
        return varStatement(token);
      case IF: return ifExpr(token);
      case FOR: return forExpr(token);
      case WHILE: return whileExpr(token);
      case VOID:
      case RET:
        return fnExpr(token);
      default:
        back();
        return parseExpr();
    }
  }

  private VarStatement varStatement(Token token) {
    String name = readAlpha();
    expect(Type.ASSIGNMENT);
    Expr value = parseExpr();
    manager.defineVr(name);
    return new VarStatement(token, token.type == Type.GLOBAL, name, value);
  }

  private Expr fnExpr(Token token) {
    boolean returning = token.type == Type.RET;
    String name = readAlpha();
    List<String> paramNames = isNext(Type.OPEN_CURVE) ? paramNames() : new ArrayList<String>();
    expect(Type.COLON);

    manager.enterScope(false);
    for (String param : paramNames) manager.defineVr(param);
    Expr body = body();
    manager.leaveScope(false);
    return new Function(token, name, returning, paramNames, body);
  }

  private List<String> paramNames() {
    expect(Type.OPEN_CURVE);
    List<String> paramNames = new ArrayList<>();
    while (notEOF()) {
      paramNames.add(readAlpha());
      if (!isNext(Type.COMMA)) break;
      skip();
    }
    expect(Type.CLOSE_CURVE);
    return paramNames;
  }

  private While whileExpr(Token token) {
    Expr condition = parseExpr();
    expect(Type.COLON);
    manager.enterScope(true);
    Statements body = body();
    manager.leaveScope(true);
    return new While(token, condition, body);
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

  private Expr ifExpr(Token token) {
    Expr condition = parseExpr();
    expect(Type.COLON);
    manager.enterScope(false);
    Expr thenBody = body();
    manager.leaveScope(false);

    Expr elseBody = null;
    if (isNext(Type.ELIF)) elseBody = ifExpr(eat());
    else if (isNext(Type.ELSE)) {
      skip();
      expect(Type.COLON);
      manager.enterScope(false);
      elseBody = body();
      manager.leaveScope(false);
    }
    return new IfExpr(token, condition, thenBody, elseBody);
  }

  private Statements body() {
    List<Expr> expressions = new ArrayList<>();
    while (notEOF()) {
      Token p = peek();
      if (p.type == Type.CLOSE_CURVE || p.hasFlag(Flag.NEW_BODY)) break;
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

  private Expr parseElement() {
    Expr left = parseTerm();
    while (notEOF()) {
      Token nextOp = peek();
      if (nextOp.type != Type.DOT) break;
      left = objectCall(left);
    }
    return left;
  }

  private Expr objectCall(Expr object) {
    expect(Type.DOT);
    String methodName = readAlpha();
    List<Expr> args = arguments();
    return new ObjectCall(object.token, object, methodName, args);
  }

  private Expr parseTerm() {
    Token token = eat();
    if (token.hasFlag(Flag.CONTEXT)) {
      return varAccess(token);
    } else if (token.type == Type.OPEN_CURVE) {
      Expr expr = parseStatement();
      expect(Type.CLOSE_CURVE);
      return expr;
    } else if (token.type == Type.OPEN_SQUARE) {
      return makeList(token);
    } else if (token.type == Type.OPEN_CURLY) {
      return makeDict(token);
    } else if (token.hasFlag(Flag.VALUE)) {
      Expr expr = parseValue(token);
      if (expr instanceof VarGet && consume(Type.ASSIGNMENT))
        return new VarSet(token, false, ((VarGet) expr).name, ((VarGet) expr).index, parseExpr());
      if (!(expr instanceof Name)) return expr;
      // check if it's some kind of function invocation!
      int nameIndex = ((Name) expr).index;
      if (nameIndex < 0) {
        // either a function call or a module cal
        if (isNext(Type.DOT)) return moduleCall(token);
        else if (isNext(Type.OPEN_CURVE)) return new FunctionCall(token, (String) token.data, arguments());
        else ((Name) expr).invalidate();
      }
      return expr;
    } else if (token.hasFlag(Flag.UNARY)) {
      return new Unary(token, token.type, parseExpr());
    }
    return token.error("Unexpected token");
  }

  private Expr varAccess(Token token) {
    // it's either a get or a set, with explicit scope
    expect(Type.DOT);
    String name = readAlpha();
    int index = manager.resolveVr(name);
    if (index < 0)
      return token.error("Symbol not found");
    if (consume(Type.ASSIGNMENT)) {
      // it's a set var access
      Expr value = parseExpr();
      return new VarSet(token, token.type == Type.GLOBAL, name, index, value);
    }
    // a simple get
    return new VarGet(token, token.type == Type.GLOBAL, name, index);
  }

  private MakeDict makeDict(Token token) {
    if (isNext(Type.CLOSE_CURLY)) return new MakeDict(token, new ArrayList<Expr>());
    List<Expr> entries = new ArrayList<>();
    while (notEOF()) {
      entries.add(parseExpr());
      if (!isNext(Type.COMMA)) break;
      skip();
    }
    expect(Type.CLOSE_CURLY);
    return new MakeDict(token, entries);
  }

  private MakeList makeList(Token token) {
    if (isNext(Type.CLOSE_SQUARE)) return new MakeList(token, new ArrayList<Expr>());
    List<Expr> items = new ArrayList<>();
    while (notEOF()) {
      items.add(parseExpr());
      if (!isNext(Type.COMMA)) break;
      skip();
    }
    expect(Type.CLOSE_SQUARE);
    return new MakeList(token, items);
  }

  private ModuleCall moduleCall(Token token) {
    expect(Type.DOT);
    String funcName = readAlpha();
    String moduleName = (String) token.data;
    return new ModuleCall(token, moduleName, funcName, arguments());
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
        if (varIndex != -1) return new VarGet(token, false, name, varIndex);
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

  private boolean consume(Type type) {
    Token token = tokens.get(index);
    if (token.type == type) {
      skip();
      return true;
    }
    return false;
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
    return index < size;
  }

  private boolean isEOF() {
    return index == size;
  }
}
