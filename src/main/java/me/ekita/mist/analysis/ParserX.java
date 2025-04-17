package me.ekita.mist.analysis;

import me.ekita.mist.expr.*;
import me.ekita.mist.syntax.Flag;
import me.ekita.mist.syntax.Token;
import me.ekita.mist.syntax.Type;

import java.util.ArrayList;
import java.util.List;

public class ParserX {

  private final ScopeManager manager = new ScopeManager();

  private final List<Token> tokens;
  private int index = 0;
  private int size = 0;

  public ParserX(List<Token> tokens) {
    this.tokens = tokens;
    this.size = tokens.size();
  }

  public Statements parse() {
    final List<Expr> expressions = new ArrayList<>();
    while (notEOF()) {
      Expr expr = parseSmt();
      expressions.add(expr);
      System.out.println(expr);
    }
    System.out.println();
    return new Statements(expressions);
  }

  public Expr parseSmt() {
    Token token = next();
    switch (token.type) {
      case VAL: case GLOBAL: return varSmt(token);
      case IF: return ifSmt(token);
      case FOR: return forSmt(token);
      case EACH: return eachSmt(token);
      case WHILE: return whileSmt(token);
      case FUN: return funSmt(token);
      case ON: return onSmt(token);
      case RETURN: case BREAK: case CONTINUE: return interruptSmt(token);
      default:
        back();
        return parseExpr();
    }
  }

  private InterruptSmt interruptSmt(Token token) {
    if (token.type != Type.RETURN) {
      if (!manager.inIterativeScope()) return token.error("Unexpected interrupt statement!");
      return new InterruptSmt(token, token.type, null);
    }
    // return always has a value :)
    return new InterruptSmt(token, Type.RETURN, parseSmt());
  }

  private On onSmt(Token token) {
    // on Button1.Click() {}
    final boolean anyEvent = consume(Type.ANY);
    final String component = readAlpha();
    expect(Type.DOT);
    final String event = readAlpha();
    manager.enterScope(false);
    final List<String> params = parameters();
    final Expr content = bodyOrSmt();
    final boolean requireScope = manager.leaveScope(false);
    return new On(token, component, event, anyEvent, params, content, requireScope);
  }

  private Function funSmt(Token token) {
    // fun fib(n) {}
    final String name = readAlpha();
    manager.enterScope(false);
    final List<String> params = parameters();
    final Expr content = bodyOrSmt();
    final boolean requireScope = manager.leaveScope(false);
    return new Function(token, name, params, content, requireScope);
  }

  private List<String> parameters() {
    // (x, y, z)
    expect(Type.OPEN_CURVE);
    final List<String> params = new ArrayList<>();
    while (notEOF() && !isNext(Type.CLOSE_CURVE)) {
      final String aParam = readAlpha();
      manager.defineVr(aParam);
      params.add(aParam);
      if (!consume(Type.COMMA)) break;
    }
    expect(Type.CLOSE_CURVE);
    return params;
  }

  private While whileSmt(Token token) {
    // while (cond) {}
    expect(Type.OPEN_CURVE);
    final Expr condition = parseSmt();
    expect(Type.CLOSE_CURVE);
    manager.enterScope(true);
    final Expr content = bodyOrSmt();
    final boolean imaginaryBody = manager.leaveScope(true);
    return new While(token, condition, content, imaginaryBody);
  }

  private Expr eachSmt(Token token) {
    manager.enterScope(true);
    expect(Type.OPEN_CURVE);
    final String key = readAlpha();
    manager.defineVr(key);

    // each (key::value -> dict) {}
    if (consume(Type.DOUBLE_COLON)) {
      final String value = readAlpha();
      manager.defineVr(value);
      expect(Type.RIGHT_ARROW);
      final Expr entries = parseSmt();
      expect(Type.CLOSE_CURVE);
      final Expr body = body();
      manager.leaveScope(true);
      return new ForEachPair(token, entries, key, value, body);
    }
    // each (name -> names) {}
    expect(Type.RIGHT_ARROW);
    final Expr iterable = parseSmt();
    expect(Type.CLOSE_CURVE);
    final Expr body = body();
    manager.leaveScope(true);
    return new ForEach(token, iterable, key, body);
  }

  private For forSmt(Token token) {
    // for (n: 1 to 5 by 2) {}
    manager.enterScope(true);
    expect(Type.OPEN_CURVE);
    final String iterName = readAlpha();
    manager.defineVr(iterName);
    expect(Type.COLON);
    final Expr from = parseSmt();
    expect(Type.TO);
    final Expr to = parseSmt();
    expect(Type.BY);
    final Expr by = parseExpr();
    expect(Type.CLOSE_CURVE);
    Statements body = body();
    manager.leaveScope(true);
    return new For(token, iterName, from, to, by, body);
  }

  private IfExpr ifSmt(Token token) {
    // if (cond) {} elif (cond) {} else {}
    final List<Expr> conditions = new ArrayList<>();
    final List<Expr> bodies = new ArrayList<>();
    final List<Boolean> requiresScopes = new ArrayList<>();

    // if-clause followed by optional elif clauses
    do {
      conditions.add(parseSmt());
      manager.enterScope(false);
      bodies.add(bodyOrSmt());
      requiresScopes.add(manager.leaveScope(false));
    } while (notEOF() && consume(Type.ELIF));

    if (notEOF() && consume(Type.ELSE)) {
      manager.enterScope(false);
      bodies.add(bodyOrSmt());
      requiresScopes.add(manager.leaveScope(false));
    }
    return new IfExpr(token, conditions, bodies, requiresScopes);
  }

  private VarSmt varSmt(Token token) {
    // glob name = "Eki"
    // val age = 12
    String name = readAlpha();
    expect(Type.ASSIGNMENT);
    Expr value = parseSmt();
    manager.defineVr(name);
    return new VarSmt(token, token.type == Type.GLOBAL, name, value);
  }

  private Expr bodyOrSmt() {
    return isNext(Type.OPEN_CURLY) ? body() : parseSmt();
  }

  private Statements body() {
    expect(Type.OPEN_CURLY);
    List<Expr> exprs = new ArrayList<>();
    while (notEOF() && !isNext(Type.CLOSE_CURLY))
      exprs.add(parseSmt());
    expect(Type.CLOSE_CURLY);
    return new Statements(exprs);
  }

  private Expr parseExpr() {
    // simple left to right parsing
    Expr left = parseElement();
    while (notEOF()) {
      final Token op = peek();
      if (!op.hasFlag(Flag.OPERATOR)) return left;
      skip();
      left = new Binary(op, left, parseElement());
    }
    return left;
  }

  private Expr parseElement() {
    Expr left = parseTerm();
    while (notEOF() && consume(Type.DOT)) left = objectCall(left);
    return left;
  }

  private Expr objectCall(Expr object) {
    // " Hola! ".trim()
    final String methodName = readAlpha();
    final List<Expr> args = isNext(Type.OPEN_CURVE) ? arguments() : new ArrayList<Expr>();
    if (!isNext(Type.OPEN_CURLY)) return new ObjectCall(object.token, object, methodName, args);
    // It's an object transformer call!
    //
    // val indices = [1, 2, 3, 4]
    // indices = indices.map { n -> n * 2 }
    expect(Type.OPEN_CURLY);
    manager.enterScope(false);
    final List<String> params = new ArrayList<>();
    do {
      final String param = readAlpha();
      manager.defineVr(param);
      params.add(param);
    } while (notEOF() && consume(Type.COMMA));
    expect(Type.RIGHT_ARROW);
    final List<Expr> body = new ArrayList<>();
    while (notEOF() && !isNext(Type.CLOSE_CURLY)) body.add(parseSmt());
    expect(Type.CLOSE_CURLY);
    manager.leaveScope(false);
    return new TransformCall(object.token, object, methodName, args, params, new Statements(body));
  }

  private Expr parseTerm() {
    final Token token = next();
    switch (token.type) {
      case VAL: case GLOBAL: return valAccess(token);
      case OPEN_CURVE:
        final Expr content = parseSmt();
        expect(Type.CLOSE_CURVE);
        return content;
      case OPEN_SQUARE: return listExpr(token);
      case OPEN_CURLY: return dictExpr(token);
      case NEGATE: case EXCLAMATION: return new Unary(token, token.type, parseExpr());
      default:
        if (!token.hasFlag(Flag.VALUE)) return token.error("Cannot parse token");
    }
    final Expr value = parseValue(token);
    // might be var set (changing existing val)!
    // name = "Hola World!"
    if (isEOF()) return value;
    if (value instanceof VarGet && consume(Type.ASSIGNMENT)) {
      final VarGet get = (VarGet) value;
      return new VarSet(token, false, get.name, get.index, parseExpr());
    }
    if (!(value instanceof Name)) return value;
    final Name name = (Name) value;
    if (name.index >= 0) return value;
    // unresolved name, might be func call or module call
    final Token next = peek();
    switch (next.type) {
      case DOT: return moduleAccess(token);
      // function call or transform function call
      case OPEN_CURVE: return new FunctionCall(token, (String) token.data, arguments());
      default:
        name.invalidate(); // Raises Symbol Not Found
        throw new RuntimeException();
    }
  }

  private Expr moduleAccess(Token token) {
    // ModuleCall: `Math.randomFraction()`
    // PropSet: `Math.randomSeed = 123`
    // PropGet: `Color.red`
    final String module = (String) token.data;
    expect(Type.DOT);
    final String name = readAlpha();
    if (isNext(Type.OPEN_CURVE)) return new ModuleCall(token, module, name, arguments());
    if (consume(Type.ASSIGNMENT)) return new PropSet(token, module, name, parseSmt());
    return new PropGet(token, module, name);
  }

  private Expr valAccess(Token token) {
    expect(Type.DOT);
    final String name = readAlpha();
    final int vrIndex = manager.resolveVr(name);
    if (vrIndex == -1) return token.error("Cannot find symbol");
    return consume(Type.ASSIGNMENT)
        ? new VarSet(token, token.type == Type.GLOBAL, name, vrIndex, parseSmt())
        : new VarGet(token, token.type == Type.GLOBAL, name, vrIndex);
  }

  private MakeList listExpr(Token token) {
    // [1, 2, 3]
    final List<Expr> elements = new ArrayList<>();
    while (notEOF() && !isNext(Type.CLOSE_SQUARE)) {
      elements.add(parseSmt());
      if (!consume(Type.COMMA)) break;
    }
    expect(Type.CLOSE_SQUARE);
    return new MakeList(token, elements);
  }

  private MakeDict dictExpr(Token token) {
    // {"Japan":"Tokyo", "India":"New Delhi"}
    final List<Expr> entries = new ArrayList<>();
    while (notEOF() && !isNext(Type.CLOSE_CURLY)) {
      entries.add(parsePair());
      if (!consume(Type.COMMA)) break;
    }
    expect(Type.CLOSE_CURLY);
    return new MakeDict(token, entries);
  }

  private Pair parsePair() {
    // "Japan":"Tokyo"
    final Expr key = parseSmt();
    expect(Type.COLON);
    final Expr value = parseSmt();
    return new Pair(key.token, key, value);
  }

  private Expr parseValue(Token token) {
    switch (token.type) {
      case M_TRUE: case M_FALSE: return new Bool(token, token.type == Type.M_TRUE);
      case M_NUM: return new Num(token, (String) token.data);
      case M_TEXT: return new Text(token, (String) token.data);
      case ALPHA:
        final String name = (String) token.data;
        final int varIndex = manager.resolveVr(name);
        if (varIndex != -1) return new VarGet(token, false, name, varIndex);
        // might be func or module call
        return new Name(token, -1);
      default: return token.error("Unknown value type");
    }
  }

  private List<Expr> arguments() {
    expect(Type.OPEN_CURVE);
    final List<Expr> arguments = new ArrayList<>();
    while (notEOF() && !isNext(Type.CLOSE_CURVE)) {
      arguments.add(parseSmt());
      if (!consume(Type.COMMA)) break;
    }
    expect(Type.CLOSE_CURVE);
    return arguments;
  }

  private String readAlpha() {
    final Token token = next();
    if (token.type == Type.ALPHA) return (String) token.data;
    return token.error("Expected type alpha but got " + token.type);
  }

  private Token expect(Type type) {
    final Token token = next();
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
    return notEOF() && tokens.get(index).type == type;
  }

  private void back() {
    index--;
  }

  private void skip() {
    index++;
  }

  private Token next() {
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
