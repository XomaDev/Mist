package me.ekita.mist.syntax;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

  private int index = 0;
  private int line = 1;

  public final List<Token> tokens = new ArrayList<>();

  private final String source;

  public Lexer(String source) {
    this.source = source;
    while (notEOF()) {
      parseNext();
    }
  }

  private void parseNext() {
    char c = next();
    if (c == '\n') {
      line++;
      return;
    }
    if (Character.isWhitespace(c)) return;
    switch (c) {
      case '=':
        tokens.add(consume('=') ? createOp("==") : createOp("="));
        break;
      case '!':
        tokens.add(consume('=') ? createOp("!=") : createOp("!"));
        break;
      case '+':
        tokens.add(createOp("+"));
        break;
      case '-':
        tokens.add(createOp("-"));
        break;
      case '*':
        tokens.add(createOp("*"));
        break;
      case '/':
        tokens.add(createOp("/"));
        break;
      case '^':
        tokens.add(createOp("^"));
        break;
      case '>':
        tokens.add(consume('=') ? createOp(">=") : createOp(">"));
        break;
      case '<':
        tokens.add(consume('=') ? createOp("<=") : createOp("<"));
        break;
      case '(':
        tokens.add(createOp("("));
        break;
      case ')':
        tokens.add(createOp(")"));
        break;
      case '[':
        tokens.add(createOp("["));
        break;
      case ']':
        tokens.add(createOp("]"));
        break;
      case '{':
        tokens.add(createOp("{"));
        break;
      case '}':
        tokens.add(createOp("}"));
        break;
      default:
        if (isAlpha(c)) parseAlpha(c);
        else if (isDigit(c)) {
          index--;
          parseNumeric();
        } else throw new RuntimeException("Unknown character at line " + line + " '" + c + "'");
        break;
    }
  }

  private void parseAlpha(char c) {
    StringBuilder content = new StringBuilder();
    content.append(c);
    while (notEOF()) {
      char p = peek();
      if (isAlpha(p) || isDigit(p)) {
        content.append(p);
        index++;
      }
      else break;
    }
    String value = content.toString();
    StaticToken keyword = Type.KEYWORDS.get(value);

    Token token  = keyword == null
        ? new Token(line, Type.ALPHA, new Flag[]{ Flag.VALUE }, value)
        : keyword.normal(line);
    tokens.add(token);
  }

  private void parseNumeric() {
    StringBuilder content = new StringBuilder();
    while (notEOF() && isDigit(peek())) content.append(next());
    if (notEOF() && peek() == '.') {
      index++;
      content.append('.');
      if (notEOF() && isDigit(peek())) content.append(next());
    }
    Token token = new Token(line, Type.M_NUM, new Flag[] { Flag.VALUE }, content.toString());
    tokens.add(token);
  }

  private Token createOp(String op) {
    StaticToken token = Type.SYMBOLS.get(op);
    if (token == null) {
      throw new RuntimeException("Could not find operator '" + op + "'");
    }
    return token.normal(line);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAlpha(char c) {
    return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
  }

  private void reportError(String message) {
    System.out.println("[line " + line + "] " + message);
  }

  private boolean consume(char c) {
    if (isEOF()) return false;
    if (source.charAt(index) == c) {
      index++;
      return true;
    }
    return false;
  }

  private char next() {
    return source.charAt(index++);
  }

  private char peek() {
    return source.charAt(index);
  }

  private char peekNext() {
    return source.charAt(index + 1);
  }

  private boolean notEOF() {
    return index < source.length();
  }

  private boolean isEOF() {
    return index == source.length();
  }

}
