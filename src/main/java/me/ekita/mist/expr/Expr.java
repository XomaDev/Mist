package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

public abstract class Expr {

  @Nullable
  public final Token token;

  public Expr(@Nullable Token token) {
    this.token = token;
  }

  public interface Visitor<R> {
    R number(Num num);
    R bool(Bool bool);
    R text(Text text);
    R name(Name name);
    R makeList(MakeList makeList);
    R makeDict(MakeDict makeDict);
    R pair(Pair pair);

    R unary(Unary unary);
    R binary(Binary binary);

    R varSmt(VarStatement smt);
    R varGet(VarGet get);
    R varSet(VarSet set);

    R statements(Statements statements);

    R ifExpr(IfExpr ifExpr);
    R whileLoop(While l);
    R forLoop(For f);

    R functionCall(FunctionCall call);
    R moduleCall(ModuleCall call);
    R objectCall(ObjectCall call);

    R function(Function func);
  }

  public abstract <R> R accept(Visitor<R> v);
}
