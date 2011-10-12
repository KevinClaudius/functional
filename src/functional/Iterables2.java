package functional;

import java.util.Comparator;
import java.util.Iterator;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

/**
 * A place for extra functionality not present in com.google.common.collect.Iterables
 */
@GwtCompatible
public final class Iterables2 {
  private Iterables2() {}
  
  /**
   * Lazy zip that applies {@code resultSelector} to tuples of elements (a[i],
   * b[i]) from {@code a} and {@code b}. Stops when either {@code a} or
   * {@code b} runs out of elements.
   * 
   * remove() is not supported by the returned {@code Iterable<R>}.
   * 
   * Inspired by Haskell's zipWith function
   * 
   * @throws {@code NullPointerException} if {@code resultSelector} is null
   */
  @GwtCompatible
  public static <A,B,R> Iterable<R> zipWith(final Function2<? super A,? super B,? extends R> resultSelector, final Iterable<A> a, final Iterable<B> b) {
    Preconditions.checkNotNull(resultSelector);
    if (null == a || null == b) {
      return ImmutableList.<R>of();
    }
    return new Iterable<R>(){
      @Override
      public Iterator<R> iterator() {
        final Iterator<A> aIterator = a.iterator();
        final Iterator<B> bIterator = b.iterator();
        return new Iterator<R>(){
          @Override
          public boolean hasNext() {
            return aIterator.hasNext() && bIterator.hasNext();
          }
          @Override
          public R next() {
            return resultSelector.apply(aIterator.next(), bIterator.next());
          }
          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
  
  /**
   * Lazy select that applies {@code selector} to each item of {@code source}
   * and returns an Iterable of the results.
   * 
   * Same as {@code Iterables.transform(source, selector)}, but allows a null
   * {@code source}. A null {@code source} returns empty.
   */
  // The parameter order here is intentionally switched vs Iterables.transform
  // to better follow the functional style that probably arose from currying
  @GwtCompatible
  public static <T,R> Iterable<R> select(final Function<? super T,? extends R> selector, final Iterable<T> source) {
    Preconditions.checkNotNull(selector);
    if (null == source) {
      return ImmutableList.<R>of();
    }
    return Iterables.transform(source, selector);
  }
  
  /**
   * Lazy sort that uses the comparator {@code c} to sort items in {@code source}. Lazy version of 
   * {@code Ordering.from(c).sortedCopy(source)} that allows a null {@code source}. 
   * A null {@code source} returns empty. 
   */
  @GwtCompatible
  public static <T> Iterable<T> sort(final Iterable<T> source, final Comparator<? super T> c) {
    // Ordering.from(c) would eventually throw an exception if c was null, 
    // but I want this to fail at declaration site instead of use site
    Preconditions.checkNotNull(c); 
    if (null == source) {
      return ImmutableList.<T>of();
    }
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return Ordering.from(c).sortedCopy(source).iterator();
      }
    };
  }
  
  /**
   * Same as sort with the explicit Comparator, but this version delegates 
   * comparison to the Comparables themselves.
   */
  @GwtCompatible
  public static <T extends Comparable> Iterable<T> sort(final Iterable<T> source) {
    return sort(source, Comparators.<T>fromComparable());
  }
  
  /**
   * Higher-order reduce function (also known as fold, accumulate, compress, or
   * inject)
   * 
   * From http://code.google.com/p/guava-libraries/issues/detail?id=218
   * 
   * Similar to .NET's Enumerable.Aggregate
   * 
   * In the conceptual example below, (a, b, c, d) is {@code source} and f is
   * {@code accumulator}.
   * 
   * (a, b, c, d) -> f(f(f(a, b), c), d)
   * 
   * @throws NullPointerException if {@code accumulator} or {@code source} are null
   */
  @GwtCompatible
  public static <T> T reduce(final Function2<? super T, ? super T, ? extends T> accumulator, final Iterable<? extends T> source) {
    return Iterables2.<T,T>reduce(accumulator, Iterables.skip(source, 1), firstOrNull(source)); // TODO: remove "Iterables2.<T,R>" when we upgrade java compiler on CI to post-1.6.0.11
  }
  
  /**
   * Higher-order reduce function (also known as left fold, accumulate,
   * compress, or inject)
   * 
   * From http://code.google.com/p/guava-libraries/issues/detail?id=218
   * 
   * Similar to .NET's Enumerable.Aggregate
   * 
   * In the conceptual example below, (a, b, c, d) is {@code source}, f is
   * {@code accumulator} and initial is {@code initialValue}.
   * 
   * (a, b, c, d), initial -> f(f(f(f(initial,a), b), c), d)
   * 
   * If source is null, returns {@code initialValue}.
   * 
   * @throws NullPointerException if {@code accumulator} is null
   */
  @GwtCompatible
  public static <T,R> R reduce(final Function2<? super R, ? super T, ? extends R> accumulator, final Iterable<? extends T> source, final R initialValue) {
    Preconditions.checkNotNull(accumulator);
    if (null == source) {
      return initialValue;
    }
    final Iterator<? extends T> it = source.iterator();
    if (!it.hasNext()) {
      return initialValue;
    }
    R acc = initialValue;
    while (it.hasNext()) {
      acc = accumulator.apply(acc, it.next());
    }
    return acc;
  }

  /**
   * Returns the first item returned by the iterator of {@code source}, or null
   * if {@code source} is null or empty.
   */
  @GwtCompatible
  public static <T> T firstOrNull(Iterable<T> source) {
    if (null == source) {
      return null;
    }
    return Iterables.get(source, 0, null); 
  }
  
  /**
   * Assumes "null"s are small.
   * 
   * @param <T>
   * @param source
   * @return
   */
  @GwtCompatible
  public static <T extends Comparable> T min(final Iterable<T> source) {
    return min(source, Comparators.<T>fromComparable());
  }
  
  @GwtCompatible
  public static <T> T min(final Iterable<T> source, final Comparator<? super T> c) {
    if (null == source) {
      return null;
    }
    // I wanted to use guava's Ordering.from(c) here, but Ordering does not
    // allow a comparator that compares a supertype of T. For instance, if
    // Employee extends Person, you would not be able to find the min Employee
    // in a list of Employee using a Comparator<Person>. I thought that was
    // limiting, because you might just want to compare by name or age, for
    // instance.
    Function2<T, T, T> f = new Function2<T, T, T>() {
      @Override
      public T apply(T a, T b) {
        if (c.compare(a, b) <= 0) {
          return a;
        }
        return b;
      }};
    return Iterables2.<T>reduce(f, source); // TODO: remove "Iterables2.<T>" when we upgrade java compiler on CI to post-1.6.0.11
  }
  
  /**
   * Assumes "null"s are small.
   * 
   * @param <T>
   * @param source
   * @return
   */
  @GwtCompatible
  public static <T extends Comparable> T max(final Iterable<T> source) {
    return max(source, Comparators.<T>fromComparable());
  }
  
  @GwtCompatible
  public static <T> T max(final Iterable<T> source, final Comparator<? super T> c) {
    if (null == source) {
      return null;
    }
    Function2<T,T,T> f = new Function2<T,T,T>(){
      @Override
      public T apply(T a, T b) {
        if (c.compare(a, b) > 0) {
          return a;
        }
        return b;
      }};
    return Iterables2.<T>reduce(f, source); // TODO: remove "Iterables2.<T>" when we upgrade java compiler on CI to post-1.6.0.11
  }
  
  // Same as com.google.common.base.Function, but with arity 2
  @GwtCompatible
  public interface Function2<A,B,R> {
    /**
     * Returns the result of applying this function to ({@code input1},
     * {@code input2}). This method is <i>generally expected</i>, but not
     * absolutely required, to have the following property:
     * 
     * <ul>
     * <li>Its execution does not cause any observable side effects.
     * </ul>
     * 
     * @throws NullPointerException
     *           if {@code input1} or {@code input2} is null and this function
     *           does not accept null arguments
     */
    R apply(A input1, B input2);

    /**
     * Indicates whether another object is equal to this function.
     * 
     * <p>
     * Most implementations will have no reason to override the behavior of
     * {@link Object#equals}. However, an implementation may also choose to
     * return {@code true} whenever {@code object} is a {@link Function} that it
     * considers <i>interchangeable</i> with this one. "Interchangeable"
     * <i>typically</i> means that
     * {@code Objects.equal(this.apply(a, b), that.apply(a, b))} is true for all
     * ({@code a}, {@code b}) of types {@code A} and {@code B}. Note that a
     * {@code false} result from this method does not imply that the functions
     * are known <i>not</i> to be interchangeable.
     */
    @Override
    boolean equals(Object object);
  }
}