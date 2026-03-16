package de.jplag.java_cpg.ai.variables.values.numbers.helpers;

import org.checkerframework.dataflow.qual.Impure;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.NotNull;

import de.jplag.java_cpg.ai.variables.values.BooleanValue;

/**
 * Interval implementation for Integer values.
 * @author ujiqk
 * @version 1.0
 */
public class IntInterval extends Interval<Integer> {

    /**
     * The maximum value for Integer intervals.
     */
    public static final int MAX_VALUE = Integer.MAX_VALUE;
    /**
     * The minimum value for Integer intervals.
     */
    public static final int MIN_VALUE = Integer.MIN_VALUE;

    /**
     * Creates a new Integer interval representing the whole range of Integer values.
     */
    public IntInterval() {
        this.lowerBound = MIN_VALUE;
        this.upperBound = MAX_VALUE;
    }

    /**
     * Creates a new Integer interval representing a single number.
     * @param number the number
     */
    public IntInterval(int number) {
        this.lowerBound = number;
        this.upperBound = number;
    }

    /**
     * Creates a new Integer interval with the given bounds.
     * @param lowerBound the lower bound.
     * @param upperBound the upper bound.
     */
    public IntInterval(int lowerBound, int upperBound) {
        assert lowerBound <= upperBound : lowerBound + " is not <= " + upperBound;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Pure
    private static int safeAbs(int x) {
        if (x == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.abs(x);
    }

    /**
     * Safely extracts the lower bound as an int value. Handles both Double and Integer intervals due to type erasure.
     */
    private static int getLowerBound(@NotNull final Interval<Integer> interval) {
        return ((Number) interval.lowerBound).intValue();
    }

    /**
     * Safely extracts the upper bound as an int value. Handles both Double and Integer intervals due to type erasure.
     */
    private static int getUpperBound(@NotNull final Interval<Integer> interval) {
        return ((Number) interval.upperBound).intValue();
    }

    @Override
    public boolean getInformation() {
        return lowerBound.equals(upperBound);
    }

    @Override
    public Integer getValue() {
        assert lowerBound.equals(upperBound);
        return lowerBound;
    }

    @Override
    public IntInterval copy() {
        return new IntInterval(lowerBound, upperBound);
    }

    @Override
    public void setToUnknown() {
        this.lowerBound = MIN_VALUE;
        this.upperBound = MAX_VALUE;
    }

    @Pure
    @Override
    public IntInterval plus(@NotNull Interval<Integer> other) {
        long loSum = (long) lowerBound + (long) getLowerBound(other);
        long hiSum = (long) upperBound + (long) getUpperBound(other);
        int lo = loSum > MAX_VALUE ? MAX_VALUE : (loSum < MIN_VALUE ? MIN_VALUE : (int) loSum);
        int hi = hiSum > MAX_VALUE ? MAX_VALUE : (hiSum < MIN_VALUE ? MIN_VALUE : (int) hiSum);
        return new IntInterval(lo, hi);
    }

    @Pure
    @Override
    public IntInterval minus(@NotNull Interval<Integer> other) {
        long loSum = (long) lowerBound - (long) other.upperBound;
        long hiSum = (long) upperBound - (long) other.lowerBound;
        int lo = loSum > MAX_VALUE ? MAX_VALUE : (loSum < MIN_VALUE ? MIN_VALUE : (int) loSum);
        int hi = hiSum > MAX_VALUE ? MAX_VALUE : (hiSum < MIN_VALUE ? MIN_VALUE : (int) hiSum);
        assert lo <= hi;
        return new IntInterval(lo, hi);
    }

    @Pure
    @Override
    public IntInterval times(@NotNull Interval<Integer> other) {
        long p1 = (long) lowerBound * getLowerBound(other);
        long p2 = (long) lowerBound * getUpperBound(other);
        long p3 = (long) upperBound * getLowerBound(other);
        long p4 = (long) upperBound * getUpperBound(other);
        long loLong = Math.min(Math.min(p1, p2), Math.min(p3, p4));
        long hiLong = Math.max(Math.max(p1, p2), Math.max(p3, p4));
        int lo = loLong > MAX_VALUE ? MAX_VALUE : (loLong < MIN_VALUE ? MIN_VALUE : (int) loLong);
        int hi = hiLong > MAX_VALUE ? MAX_VALUE : (hiLong < MIN_VALUE ? MIN_VALUE : (int) hiLong);
        return new IntInterval(lo, hi);
    }

    @Pure
    @Override
    public IntInterval divided(@NotNull Interval<Integer> other) {
        if (getLowerBound(other) <= 0 && getUpperBound(other) >= 0) {
            return new IntInterval(MIN_VALUE, MAX_VALUE);
        }
        long p1 = (lowerBound == MIN_VALUE && other.lowerBound == -1) ? (long) MAX_VALUE : (long) lowerBound / (long) other.lowerBound;
        long p2 = (lowerBound == MIN_VALUE && other.upperBound == -1) ? (long) MAX_VALUE : (long) lowerBound / (long) other.upperBound;
        long p3 = (upperBound == MIN_VALUE && other.lowerBound == -1) ? (long) MAX_VALUE : (long) upperBound / (long) other.lowerBound;
        long p4 = (upperBound == MIN_VALUE && other.upperBound == -1) ? (long) MAX_VALUE : (long) upperBound / (long) other.upperBound;
        long loLong = Math.min(Math.min(p1, p2), Math.min(p3, p4));
        long hiLong = Math.max(Math.max(p1, p2), Math.max(p3, p4));
        int lo = loLong > MAX_VALUE ? MAX_VALUE : (loLong < MIN_VALUE ? MIN_VALUE : (int) loLong);
        int hi = hiLong > MAX_VALUE ? MAX_VALUE : (hiLong < MIN_VALUE ? MIN_VALUE : (int) hiLong);
        return new IntInterval(lo, hi);
    }

    @Pure
    @Override
    public BooleanValue equal(@NotNull Interval<Integer> other) {
        if (lowerBound.equals(upperBound) && getLowerBound(other) == getUpperBound(other) && lowerBound == getLowerBound(other)) {
            return new BooleanValue(true);
        } else if (upperBound < getLowerBound(other) || lowerBound > getUpperBound(other)) {
            return new BooleanValue(false);
        } else {
            return new BooleanValue();
        }
    }

    @Pure
    @Override
    public BooleanValue notEqual(@NotNull Interval<Integer> other) {
        if (upperBound < other.lowerBound || lowerBound > other.upperBound) {
            return new BooleanValue(true);
        } else if (lowerBound.equals(upperBound) && other.lowerBound.equals(other.upperBound) && lowerBound.equals(other.lowerBound)) {
            return new BooleanValue(false);
        } else {
            return new BooleanValue();
        }
    }

    @Pure
    @Override
    public BooleanValue smaller(@NotNull Interval<Integer> other) {
        if (upperBound < other.lowerBound) {
            return new BooleanValue(true);
        } else if (lowerBound >= other.upperBound) {
            return new BooleanValue(false);
        } else {
            return new BooleanValue();
        }
    }

    @Pure
    @Override
    public BooleanValue smallerEqual(@NotNull Interval<Integer> other) {
        if (upperBound <= other.lowerBound) {
            return new BooleanValue(true);
        } else if (lowerBound > other.upperBound) {
            return new BooleanValue(false);
        } else {
            return new BooleanValue();
        }
    }

    @Pure
    @Override
    public BooleanValue bigger(@NotNull Interval<Integer> other) {
        if (lowerBound > other.upperBound) {
            return new BooleanValue(true);
        } else if (upperBound <= other.lowerBound) {
            return new BooleanValue(false);
        } else {
            return new BooleanValue();
        }
    }

    @Pure
    @Override
    public BooleanValue biggerEqual(@NotNull Interval<Integer> other) {
        if (upperBound <= other.lowerBound) {
            return new BooleanValue(true);
        } else if (lowerBound > other.upperBound) {
            return new BooleanValue(false);
        } else {
            return new BooleanValue();
        }
    }

    @Impure
    @Override
    public IntInterval plusPlus() {
        long newLower = (long) lowerBound + 1;
        long newUpper = (long) upperBound + 1;
        lowerBound = newLower > MAX_VALUE ? MAX_VALUE : (int) newLower;
        upperBound = newUpper > MAX_VALUE ? MAX_VALUE : (int) newUpper;
        return this.copy();
    }

    @Impure
    @Override
    public IntInterval minusMinus() {
        long newLower = (long) lowerBound - 1;
        long newUpper = (long) upperBound - 1;
        lowerBound = newLower < MIN_VALUE ? MIN_VALUE : (int) newLower;
        upperBound = newUpper < MIN_VALUE ? MIN_VALUE : (int) newUpper;
        return this.copy();
    }

    @Pure
    @Override
    public IntInterval unaryMinus() {
        int newLower = (upperBound == Integer.MIN_VALUE) ? Integer.MAX_VALUE : -upperBound;
        int newUpper = (lowerBound == Integer.MIN_VALUE) ? Integer.MAX_VALUE : -lowerBound;
        return new IntInterval(newLower, newUpper);
    }

    @Pure
    @Override
    public IntInterval abs() {
        if (upperBound < 0) {
            return new IntInterval(safeAbs(upperBound), safeAbs(lowerBound));
        } else if (lowerBound >= 0) {
            return new IntInterval(lowerBound, upperBound);
        } else {
            int max = Math.max(safeAbs(lowerBound), safeAbs(upperBound));
            return new IntInterval(0, max);
        }
    }

    @Impure
    @Override
    public void merge(@NotNull Interval<Integer> other) {
        int smallerLowerBound = Math.min(this.lowerBound, getLowerBound(other));
        int largerUpperBound = Math.max(this.upperBound, getUpperBound(other));
        assert smallerLowerBound <= largerUpperBound;
        this.lowerBound = smallerLowerBound;
        this.upperBound = largerUpperBound;
    }

    @Override
    public int compareTo(@NotNull Interval<Integer> o) {
        if (!this.lowerBound.equals(getLowerBound(o))) {
            return Integer.compare(this.lowerBound, getLowerBound(o));
        } else {
            return Integer.compare(this.upperBound, getUpperBound(o));
        }
    }

}
