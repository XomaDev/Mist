package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MakeList extends Expr {

  public final List<Expr> items;

  public MakeList(@Nullable Token token, List<Expr> items) {
    super(token);
    this.items = items;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.makeList(this);
  }

  @Override
  public String toString() {
    return "MakeList{" +
        "items=" + items +
        '}';
  }
}
