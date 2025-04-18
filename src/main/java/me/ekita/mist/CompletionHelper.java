package me.ekita.mist;

import me.ekita.mist.syntax.Lexer;
import me.ekita.mist.syntax.Token;

import java.util.List;

public class CompletionHelper {

  public interface Callback {
    void onReady(List<Token> tokens);
    void onLexError(String error);
  }

  private final StringBuilder buffer = new StringBuilder();

  public boolean addLine(Callback callback, String line) {
    buffer.append(line).append("\n");
    final List<Token> tokens;
    try {
      tokens = new Lexer(buffer.toString()).tokens;
    } catch (Exception e) {
      callback.onLexError(e.getMessage());
      return false;
    }
    int entitiesOpen = 0;
    for (Token token : tokens) {
      switch (token.type) {
        case OPEN_CURVE:
        case OPEN_CURLY:
        case OPEN_SQUARE:
          entitiesOpen++;
          break;
        case CLOSE_CURVE:
        case CLOSE_CURLY:
        case CLOSE_SQUARE:
          entitiesOpen--;
          break;
      }
    }
    if (entitiesOpen == 0) {
      buffer.setLength(0);
      callback.onReady(tokens);
      return true;
    }
    return false;
  }
}
