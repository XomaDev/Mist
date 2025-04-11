package me.ekita.mist.expr;

import me.ekita.mist.syntax.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModuleCall extends Expr {

  public final String moduleName;
  public final String funcName;
  public final List<Expr> arguments;

  public ModuleCall(@Nullable Token token, String moduleName, String funcName, List<Expr> arguments) {
    super(token);
    this.moduleName = moduleName;
    this.funcName = funcName;
    this.arguments = arguments;
  }

  @Override
  public <R> R accept(Visitor<R> v) {
    return v.moduleCall(this);
  }

  @Override
  public String toString() {
    return "ModuleCall{" +
        "moduleName='" + moduleName + '\'' +
        ", funcName='" + funcName + '\'' +
        ", arguments=" + arguments +
        '}';
  }
}
