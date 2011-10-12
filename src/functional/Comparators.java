package functional;

import java.util.Comparator;

import com.google.common.annotations.GwtCompatible;

/**
 * A place for common comparators of Java types. Note that these are different
 * from DatatypeComparators and should not be used to compare data that is
 * displayed directly to a user. The rules for null sort in reports, for
 * instance, are opposite here.
 */
@GwtCompatible
public class Comparators {
  /**
   * Returns a {@code Comparator} that delegates comparison to the Comparable's
   * compareTo method.
   * 
   * "null" values are treated as very small values.
   */
  @GwtCompatible
  public static <T extends Comparable> Comparator<T> fromComparable() {
    return new Comparator<T>(){
      @Override
      public int compare(T a, T b) {
        if (a == b) {
          return 0;
        }
        if (null == a) {
          return -1;
        }
        if (null == b) {
          return 1;
        }
        return a.compareTo(b);
      }
    };
  }
}