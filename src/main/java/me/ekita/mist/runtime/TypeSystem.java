package me.ekita.mist.runtime;

import me.ekita.mist.runtime.structs.RDict;
import me.ekita.mist.runtime.structs.RList;
import me.ekita.mist.runtime.structs.RNumber;

import java.util.Iterator;

public class TypeSystem {

  /**
   * Gets underlying value beneath any wrapper classes
   */
  public static Object nakedValue(Object value) {
    if (value instanceof RNumber) {
      return ((RNumber) value).number;
    }
    return value;
  }

  /**
   * Try to unwrap the underlying true value of an object.
   * This might be "true" but as a string.
   * Or "123" which is a string, but has numeric content.
   */
  public static Object trueValue(Object value) {
    if (value instanceof RNumber) {
      return ((RNumber) value).doubleValue();
    }
    if ("true".equals(value) || "false".equals(value)) {
      return Boolean.parseBoolean(value.toString());
    }
    try {
      return Double.parseDouble(value.toString());
    } catch (NumberFormatException ignored) {
    }
    return value;
  }

  public static boolean listEquals(RList left, RList right) {
    final int size = left.size();
    if (size != right.size()) return false;
    for (int i = 0; i < size; i++) {
      if (!contentEquals(left.get(i), right.get(i))) return false;
    }
    return true;
  }

  public static boolean dictEquals(RDict left, RDict right) {
    if (left.size() != right.size()) return false;
    Iterator<Object> leftKeys = left.keySet().iterator(), rightKeys = right.keySet().iterator();
    while (leftKeys.hasNext()) {
      Object leftKey = leftKeys.next(), rightKey = rightKeys.next();
      if (!contentEquals(leftKey, rightKey)) return false;
      Object leftValue = left.get(leftKey), rightValue = right.get(rightKey);
      if (!contentEquals(leftValue, rightValue)) return false;
    }
    return true;
  }

  /**
   * Checks if the underlying true value of both the objects are equal.
   * "true" == true
   * "123.0" == 123
   */

  public static boolean contentEquals(Object left, Object right) {
    if (left == null || right == null) return left == right;
    left = trueValue(left);
    right = trueValue(right);
    // now we do not worry about type discrepancies
    if (left.getClass() != right.getClass()) return false;
    if (left instanceof RList && right instanceof RList) return listEquals((RList) left, (RList) right);
    if (left instanceof RDict) return dictEquals((RDict) left, (RDict) right);
    return left.equals(right);
  }

  /**
   * Compares underlying true value of the objects.
   * "123" == 123
   * "8" > 7
   * "12" > "01"
   */

  public static Object compare(Object left, Object right) {
    if (left == null || right == null) return "Null values provided for comparison!";
    left = trueValue(left);
    right = trueValue(right);
    if (left.getClass() != right.getClass()) {
      return "Cannot compare values of different types! (" + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName() + ")";
    }
    if (left instanceof Double) return Double.compare((double) left, (double) right);
    if (left instanceof Boolean) return Boolean.compare((boolean) left, (boolean) right);
    // The user is wrong here. He should have instead used Text.compare(), anyway fallback
    if (left instanceof String) return ((String) left).compareTo((String) right);
    return "Cannot compare values of type " + left.getClass().getSimpleName();
  }
}
