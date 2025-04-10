package me.ekita.mist.syntax;

import org.jetbrains.annotations.Nullable;

public class Token {

  public final int lineCount;
  public final Type type;
  public final Flag[] flags;
  @Nullable
  public final Object data;

  public Token(int lineCount, Type type, Flag[] flags, @Nullable Object data) {
    this.lineCount = lineCount;
    this.type = type;
    this.flags = flags;
    this.data = data;
  }

  public boolean hasFlag(Flag flag) {
    for (Flag aFlag : flags)
      if (aFlag == flag) return true;
    return false;
  }

  public <T> T error(String message) {
    throw new RuntimeException(prepareErrorMessage(message));
  }

  public String prepareErrorMessage(String message) {
    StringBuilder messageBuilder = new StringBuilder()
        .append("[line ")
        .append(lineCount)
        .append("] [")
        .append(type);
    if (data != null) messageBuilder.append(" '").append(data).append("'");
    messageBuilder.append("] ").append(message);
    return messageBuilder.toString();
  }
}
