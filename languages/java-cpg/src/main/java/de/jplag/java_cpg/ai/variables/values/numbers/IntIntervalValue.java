package de.jplag.java_cpg.ai.variables.values.numbers;

import java.util.Set;

import org.checkerframework.dataflow.qual.Impure;
import org.jetbrains.annotations.NotNull;

import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.chars.ICharValue;
import de.jplag.java_cpg.ai.variables.values.numbers.helpers.IntInterval;

/**
 * Represents integer values as intervals.
 * @author ujiqk
 * @version 1.0
 */
public class IntIntervalValue extends Value implements INumberValue, IIntNumber {

    private final IntInterval interval;

    /**
     * a IntIntervalValue with no information.
     */
    public IntIntervalValue() {
        super(new Type(Type.TypeEnum.INT));
        interval = new IntInterval();
    }

    /**
     * Constructor for IntIntervalValue which value is between given bounds.
     * @param lowerBound the lower bound.
     * @param upperBound the upper bound.
     */
    public IntIntervalValue(int lowerBound, int upperBound) {
        super(new Type(Type.TypeEnum.INT));
        interval = new IntInterval(lowerBound, upperBound);
    }

    /**
     * Constructor for IntIntervalValue with exact information.
     * @param number the integer value.
     */
    public IntIntervalValue(int number) {
        super(new Type(Type.TypeEnum.INT));
        interval = new IntInterval(number);
    }

    /**
     * Constructor for IntIntervalValue which value is a set of possible values.
     * @param possibleValues the set of possible integer values.
     */
    public IntIntervalValue(@NotNull Set<Integer> possibleValues) {
        super(new Type(Type.TypeEnum.INT));
        java.util.List<Integer> values = possibleValues.stream().toList();
        interval = new IntInterval(values.getFirst(), values.getLast());
    }

    private IntIntervalValue(IntInterval interval) {
        super(new Type(Type.TypeEnum.INT));
        this.interval = interval;
    }

    @Override
    public boolean getInformation() {
        return interval.getInformation();
    }

    @Override
    public double getValue() {
        return interval.getValue();
    }

    @Override
    public IValue binaryOperation(@NotNull String operator, @NotNull IValue other) {
        if (!(other instanceof INumberValue)) {
            other = new IntIntervalValue();
        }
        if (other instanceof IFloatNumber || other instanceof ICharValue) {    // This could be better
            INumberValue number = (INumberValue) other;
            if (number.getInformation() && number.getValue() == (int) number.getValue()) {
                other = new IntIntervalValue((int) number.getValue());
            } else {
                other = new IntIntervalValue();
            }
        }
        IntIntervalValue otherValue = (IntIntervalValue) other;
        switch (operator) {
            case "+" -> {
                IntInterval newInterval = this.interval.copy().plus(otherValue.interval);
                return new IntIntervalValue(newInterval);
            }
            case "-" -> {
                IntInterval newInterval = this.interval.copy().minus(otherValue.interval);
                return new IntIntervalValue(newInterval);
            }
            case "<" -> {
                return this.interval.copy().smaller(otherValue.interval);
            }
            case ">" -> {
                return this.interval.copy().bigger(otherValue.interval);
            }
            case "<=" -> {
                return this.interval.copy().smallerEqual(otherValue.interval);
            }
            case ">=" -> {
                return this.interval.copy().biggerEqual(otherValue.interval);
            }
            case "==" -> {
                return this.interval.copy().equal(otherValue.interval);
            }
            case "!=" -> {
                return this.interval.copy().notEqual(otherValue.interval);
            }
            case "*" -> {
                IntInterval newInterval = this.interval.copy().times(otherValue.interval);
                return new IntIntervalValue(newInterval);
            }
            case "/" -> {
                IntInterval newInterval = this.interval.copy().divided(otherValue.interval);
                return new IntIntervalValue(newInterval);
            }
            default -> {
                return new VoidValue();
            }
        }
    }

    @Override
    @Impure
    public IValue unaryOperation(@NotNull String operator) {
        switch (operator) {
            case "++" -> {
                this.interval.plusPlus();
                return this.copy();
            }
            case "--" -> {
                this.interval.minusMinus();
                return this.copy();
            }
            case "-" -> {
                IntInterval newInterval = this.interval.copy().unaryMinus();
                return new IntIntervalValue(newInterval);
            }
            case "abs" -> {
                IntInterval newInterval = this.interval.copy().abs();
                return new IntIntervalValue(newInterval);
            }
            default -> {
                return new VoidValue();
            }
        }
    }

    @NotNull
    @Override
    public IValue copy() {
        return new IntIntervalValue(interval.copy());
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (other instanceof VoidValue) {
            other = new IntIntervalValue();
        }
        if (other instanceof IFloatNumber floatNumber) {    // can happen because some casts are not explicit in eog
            if (floatNumber.getInformation()) {
                other = new IntIntervalValue((int) floatNumber.getValue());
            } else {
                other = new IntIntervalValue();
            }
        }
        assert other instanceof IntIntervalValue : "Cannot merge " + this.getClass() + " with " + other.getClass();
        this.interval.merge(((IntIntervalValue) other).interval);
    }

    @Override
    public void setToUnknown() {
        interval.setToUnknown();
    }

    @Override
    public void setInitialValue() {
        interval.setUpperBound(0);
        interval.setLowerBound(0);
    }

}
