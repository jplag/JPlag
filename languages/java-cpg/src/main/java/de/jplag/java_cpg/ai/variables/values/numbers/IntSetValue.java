package de.jplag.java_cpg.ai.variables.values.numbers;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.numbers.helpers.IntInterval;

/**
 * Integer value represented as a set of intervals.
 * @author ujiqk
 * @version 1.0
 */
public class IntSetValue extends NumberSetValue<Integer, IntInterval> implements IIntNumber {

    /**
     * Integer value represented as a set of intervals with no information.
     */
    public IntSetValue() {
        super(new Type(Type.TypeEnum.INT));
        values.add(new IntInterval());
    }

    private IntSetValue(TreeSet<IntInterval> values) {
        super(new Type(Type.TypeEnum.INT), values);
    }

    /**
     * Constructor for IntSetValue that is known to be a single number.
     * @param number the single integer number
     */
    public IntSetValue(int number) {
        super(new Type(Type.TypeEnum.INT));
        values.add(new IntInterval(number));
    }

    /**
     * Constructor for IntSetValue that is known to be one of the possible numbers.
     * @param possibleNumbers the possible integer numbers
     */
    public IntSetValue(@NotNull Set<Integer> possibleNumbers) {
        super(new Type(Type.TypeEnum.INT));
        values = new TreeSet<>();
        // ToDo: slice into intervals
        values.add(new IntInterval());
    }

    /**
     * Constructor for IntSetValue that is known to be within a certain range.
     * @param lowerBound the lower bound of the range
     * @param upperBound the upper bound of the range
     */
    public IntSetValue(int lowerBound, int upperBound) {
        super(new Type(Type.TypeEnum.INT));
        values.add(new IntInterval(lowerBound, upperBound));
    }

    @Override
    protected IntInterval createFullInterval() {
        return new IntInterval();
    }

    @Override
    protected IntInterval createInterval(Integer lowerBound, Integer upperBound) {
        return new IntInterval(lowerBound, upperBound);
    }

    @Override
    protected NumberSetValue<Integer, IntInterval> createInstance(TreeSet<IntInterval> values) {
        return new IntSetValue(values);
    }

    @NotNull
    @Override
    public Value copy() {
        return new IntSetValue(new TreeSet<>(values));
    }

    @Override
    public void setInitialValue() {
        values = new TreeSet<>();
        values.add(createInterval(0, 0));
    }

    /**
     * Use for testing only!
     * @return the set of intervals representing the integer value.
     */
    @TestOnly
    public SortedSet<IntInterval> getIntervals() {
        return values;
    }

}
