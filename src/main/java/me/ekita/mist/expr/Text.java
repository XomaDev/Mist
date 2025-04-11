package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public class Text extends Expr {

  public final String content;

  public Text(@Nullable Token token, String content) {
    super(token);
    this.content = content;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.text(this);
  }

  @Override
  public String toString() {
    return "Text(" + content + ")";
  }
}
