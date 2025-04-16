package me.ekita.mist.runtime.structs;

import org.jetbrains.annotations.NotNull;

public class RNumber implements Comparable<RNumber> {

  private final Number number;

  public RNumber(Number number) {
    this.number = number;
  }
  
  public long longValue() {
    return number.longValue();
  }
  
  public double doubleValue() {
    return number.doubleValue();
  }

  public RNumber negate() {
    if (number instanceof Long) return new RNumber(-number.longValue());
    return new RNumber(-number.doubleValue());
  }

  public RNumber add(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(number.longValue() + other.longValue());
    return new RNumber(number.doubleValue() + other.doubleValue());
  }

  public RNumber sub(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(number.longValue() - other.longValue());
    return new RNumber(number.doubleValue() - other.doubleValue());
  }

  public RNumber mul(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(number.longValue() * other.longValue());
    return new RNumber(number.doubleValue() * other.doubleValue());
  }

  public RNumber div(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(number.longValue() / other.longValue());
    return new RNumber(number.doubleValue() / other.doubleValue());
  }

  public RNumber pow(RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return new RNumber(Math.pow(number.longValue(), other.longValue()));
    return new RNumber(Math.pow(number.doubleValue(), other.doubleValue()));
  }

  public RNumber and(RNumber other) {
    return new RNumber(number.longValue() & other.longValue());
  }

  public RNumber or(RNumber other) {
    return new RNumber(number.longValue() | other.longValue());
  }

  public RNumber xor(RNumber other) {
    return new RNumber(number.longValue() ^ other.longValue());
  }

  public int compareTo(@NotNull RNumber other) {
    if (number instanceof Long && other.number instanceof Long)
      return Long.compare(number.longValue(), other.number.longValue());
    return Double.compare(number.doubleValue(), other.doubleValue());
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    RNumber rNumber = (RNumber) object;
    return number.equals(rNumber.number);
  }

  @Override
  public int hashCode() {
    return number.hashCode();
  }

  @Override
  public String toString() {
    return number.toString();
  }
}
