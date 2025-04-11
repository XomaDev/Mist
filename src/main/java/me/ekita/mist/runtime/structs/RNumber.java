package me.ekita.mist.runtime.structs;

public class RNumber {

  private final Number number;

  public RNumber(Number number) {
    this.number = number;
  }

  public RNumber add(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(number.longValue() + other.number.longValue());
    return new RNumber(number.doubleValue() + other.number.doubleValue());
  }

  public RNumber sub(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(number.longValue() - other.number.longValue());
    return new RNumber(number.doubleValue() - other.number.doubleValue());
  }

  public RNumber mul(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(number.longValue() * other.number.longValue());
    return new RNumber(number.doubleValue() * other.number.doubleValue());
  }

  public RNumber div(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(number.longValue() / other.number.longValue());
    return new RNumber(number.doubleValue() / other.number.doubleValue());
  }

  public RNumber pow(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(Math.pow(number.longValue(), other.number.longValue()));
    return new RNumber(Math.pow(number.doubleValue(), other.number.doubleValue()));
  }

  public int compareTo(RNumber other) {
    return Double.compare(number.doubleValue(), other.number.doubleValue());
  }

  @Override
  public String toString() {
    return number.toString();
  }
}
