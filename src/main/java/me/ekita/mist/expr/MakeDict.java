package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MakeDict extends Expr {

  public final List<Expr> entries;

  public MakeDict(@Nullable Token token, List<Expr> entries) {
    super(token);
    this.entries = entries;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.makeDict(this);
  }

  @Override
  public String toString() {
    return "MakeDict{" + "map=" + entries + '}';
  }
}
