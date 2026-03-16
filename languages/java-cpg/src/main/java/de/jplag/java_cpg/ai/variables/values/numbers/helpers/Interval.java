package de.jplag.java_cpg.ai.variables.values.numbers.helpers;

import org.checkerframework.dataflow.qual.Impure;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.NotNull;

import de.jplag.java_cpg.ai.variables.values.BooleanValue;

/**
 * Abstract class representing an interval of numbers.
 * @author ujiqk
 * @version 1.0
 * @param <T> the type of number (e.g., Integer, Double)
 */
public abstract class Interval<T extends Number & Comparable<T>> implements Comparable<Interval<T>> {

    protected T lowerBound;
    protected T upperBound;

    /**
     * @return if exact information is available.
     */
    public abstract boolean getInformation();

    /**
     * @return the exact value. Only valid if getInformation() returns true.
     */
    public abstract T getValue();

    /**
     * Creates a deep copy of this interval.
     * @return a new Interval instance with the same bounds.
     */
    public abstract Interval<T> copy();

    /**
     * Sets this interval to represent an unknown value.
     */
    public abstract void setToUnknown();

    /**
     * @param other the other interval to add.
     * @return the resulting interval
     */
    @Pure
    public abstract Interval<T> plus(@NotNull Interval<T> other);

    /**
     * @param other the other interval to subtract.
     * @return the resulting interval
     */
    @Pure
    public abstract Interval<T> minus(@NotNull Interval<T> other);

    /**
     * @param other the other interval to multiply.
     * @return the resulting interval
     */
    @Pure
    public abstract Interval<T> times(@NotNull Interval<T> other);

    /**
     * @param other the other interval to divide.
     * @return the resulting interval
     */
    @Pure
    public abstract Interval<T> divided(@NotNull Interval<T> other);

    /**
     * @param other the other interval to check equality.
     * @return if the intervals are equal
     */
    @Pure
    public abstract BooleanValue equal(@NotNull Interval<T> other);

    /**
     * @param other the other interval to check inequality.
     * @return if the intervals are not equal
     */
    @Pure
    public abstract BooleanValue notEqual(@NotNull Interval<T> other);

    /**
     * @param other the other interval to compare.
     * @return if this interval is smaller than the other
     */
    @Pure
    public abstract BooleanValue smaller(@NotNull Interval<T> other);

    /**
     * @param other the other interval to compare.
     * @return if this interval is smaller than or equal to the other
     */
    @Pure
    public abstract BooleanValue smallerEqual(@NotNull Interval<T> other);

    /**
     * @param other the other interval to compare.
     * @return if this interval is bigger than the other
     */
    @Pure
    public abstract BooleanValue bigger(@NotNull Interval<T> other);

    /**
     * @param other the other interval to compare.
     * @return if this interval is bigger than or equal to the other
     */
    @Pure
    public abstract BooleanValue biggerEqual(@NotNull Interval<T> other);

    /**
     * @return the interval with all values incremented by one
     */
    @Impure
    public abstract Interval<T> plusPlus();

    /**
     * @return the interval with all values decremented by one
     */
    @Impure
    public abstract Interval<T> minusMinus();

    /**
     * @return the negated interval
     */
    @Pure
    public abstract Interval<T> unaryMinus();

    /**
     * @return the absolute value of the interval
     */
    @Pure
    public abstract Interval<T> abs();

    /**
     * Merges another interval into this one.
     * @param other the other interval to merge
     */
    @Impure
    public abstract void merge(@NotNull Interval<T> other);

    /**
     * @return the lower bound of this interval
     */
    public T getLowerBound() {
        return lowerBound;
    }

    /**
     * @param lowerBound the new lower bound
     */
    public void setLowerBound(@NotNull T lowerBound) {
        assert lowerBound.compareTo(upperBound) <= 0;
        this.lowerBound = lowerBound;
    }

    /**
     * @return the upper bound of this interval
     */
    public T getUpperBound() {
        return upperBound;
    }

    /**
     * @param upperBound the new upper bound
     */
    public void setUpperBound(T upperBound) {
        assert lowerBound.compareTo(upperBound) <= 0;
        this.upperBound = upperBound;
    }

}
