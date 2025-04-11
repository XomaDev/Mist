package me.ekita.mist.analysis;

public class ScopeManager {

  private int iterativeScopes = 0;

  public boolean inIterativeScope() {
    return iterativeScopes > 0;
  }

  private final Scope headScope = new Scope(null);
  private Scope currentScope = headScope;

  public void enterScope(boolean isIter) {
    currentScope = new Scope(currentScope);
    if (isIter) iterativeScopes++;
  }

  public boolean leaveScope(boolean isIter) {
    boolean imaginaryScope = currentScope.variables.isEmpty() && currentScope.functions.isEmpty();
    if (currentScope.parent == null) throw new RuntimeException("Reached super scope");
    currentScope = currentScope.parent;
    if (isIter) iterativeScopes--;
    return imaginaryScope;
  }

  public void defineVr(String name) {
    currentScope.defineVr(name);
  }

  public void defineFn(String name, int paramCount) {
    currentScope.defineFn(name, paramCount);
  }

  public int resolveVr(String name) {
    return currentScope.resolveVr(name);
  }

  public UniqueFunction resolveFn(String name) {
    return currentScope.resolveFn(name);
  }

}
