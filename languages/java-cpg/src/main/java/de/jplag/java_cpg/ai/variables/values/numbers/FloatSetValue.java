package de.jplag.java_cpg.ai.variables.values.numbers;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.numbers.helpers.DoubleInterval;

/**
 * Float value represented as a set of intervals.
 * @author ujiqk
 * @version 1.0
 */
public class FloatSetValue extends NumberSetValue<Double, DoubleInterval> implements IFloatNumber {

    /**
     * Default constructor c Float value represented as a set of intervals with no information.
     */
    public FloatSetValue() {
        super(new Type(Type.TypeEnum.FLOAT));
        values.add(new DoubleInterval());
    }

    private FloatSetValue(TreeSet<DoubleInterval> values) {
        super(new Type(Type.TypeEnum.FLOAT), values);
    }

    /**
     * Constructor for FloatSetValue that is known to be a single number.
     * @param number the single float number
     */
    public FloatSetValue(double number) {
        super(new Type(Type.TypeEnum.FLOAT));
        values.add(new DoubleInterval(number));
    }

    /**
     * Constructor for FloatSetValue that is known to be one of the possible numbers.
     * @param possibleNumbers the possible float numbers
     */
    public FloatSetValue(@NotNull Set<Double> possibleNumbers) {
        super(new Type(Type.TypeEnum.INT));
        values = new TreeSet<>();
        // ToDo: slice into intervals
        values.add(new DoubleInterval());
    }

    /**
     * Constructor for FloatSetValue that is known to be within a certain range.
     * @param lowerBound the lower bound of the range
     * @param upperBound the upper bound of the range
     */
    public FloatSetValue(double lowerBound, double upperBound) {
        super(new Type(Type.TypeEnum.INT));
        values.add(new DoubleInterval(lowerBound, upperBound));
    }

    @Override
    protected DoubleInterval createFullInterval() {
        return new DoubleInterval();
    }

    @Override
    protected DoubleInterval createInterval(Double lowerBound, Double upperBound) {
        return new DoubleInterval(lowerBound, upperBound);
    }

    @Override
    protected NumberSetValue<Double, DoubleInterval> createInstance(TreeSet<DoubleInterval> values) {
        return new FloatSetValue(values);
    }

    @NotNull
    @Override
    public Value copy() {
        return new FloatSetValue(new TreeSet<>(values));
    }

    @Override
    public void setInitialValue() {
        values = new TreeSet<>();
        values.add(createInterval(0d, 0d));
    }

    /**
     * Use for testing purposes only.
     * @return the set of intervals representing the float value.
     */
    @TestOnly
    public SortedSet<DoubleInterval> getIntervals() {
        return values;
    }

}
