package me.ekita.mist.syntax;

public class StaticToken {

  final Type type;
  final Flag[] flags;

  public StaticToken(Type type) {
    this.type = type;
    this.flags = new Flag[0];
  }

  public StaticToken(Type type, Flag... flags) {
    this.type = type;
    this.flags = flags;
  }

  public Token normal(int lineCount) {
    return new Token(lineCount, type, flags, null);
  }
}
