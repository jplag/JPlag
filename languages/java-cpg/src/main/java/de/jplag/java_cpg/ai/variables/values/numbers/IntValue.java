package de.jplag.java_cpg.ai.variables.values.numbers;

import java.util.Set;

import org.checkerframework.dataflow.qual.Impure;
import org.jetbrains.annotations.NotNull;

import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.BooleanValue;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.chars.CharValue;
import de.jplag.java_cpg.ai.variables.values.string.IStringValue;

/**
 * Represents an integer value with optional exact information.
 * @author ujiqk
 * @version 1.0
 */
public class IntValue extends Value implements INumberValue, IIntNumber {

    private int value;
    private boolean information;    // whether exact information is available

    /**
     * a IntValue with no information.
     */
    public IntValue() {
        super(new Type(Type.TypeEnum.INT));
        information = false;
    }

    /**
     * Constructor for IntValue with exact information.
     * @param value the integer value.
     */
    public IntValue(int value) {
        super(new Type(Type.TypeEnum.INT));
        this.value = value;
        information = true;
    }

    /**
     * Constructor for IntValue with exact information from a double value.
     * @param value the integer value as double.
     */
    public IntValue(double value) {
        super(new Type(Type.TypeEnum.INT));
        this.value = (int) value;
        information = true;
    }

    /**
     * Constructor for IntValue with a set of possible values.
     * @param possibleValues the set of possible integer values.
     */
    public IntValue(@NotNull Set<Integer> possibleValues) {
        super(new Type(Type.TypeEnum.INT));
        if (possibleValues.size() == 1) {
            this.value = possibleValues.iterator().next();
            this.information = true;
        } else {
            this.information = false;
        }
    }

    /**
     * Constructor for IntValue with a range.
     * @param lowerBound the lower bound of the range.
     * @param upperBound the upper bound of the range.
     */
    public IntValue(int lowerBound, int upperBound) {
        super(new Type(Type.TypeEnum.INT));
        assert lowerBound <= upperBound;
        if (lowerBound == upperBound) {
            this.value = lowerBound;
            this.information = true;
        } else {
            this.information = false;
        }
    }

    private IntValue(int value, boolean information) {
        super(new Type(Type.TypeEnum.INT));
        this.value = value;
        this.information = information;
    }

    /**
     * @return whether exact information is available
     */
    public boolean getInformation() {
        return information;
    }

    /**
     * @return the exact value. Only valid if getInformation() returns true.
     */
    public double getValue() {
        assert information;
        return value;
    }

    @Override
    public IValue binaryOperation(@NotNull String operator, @NotNull IValue other) {
        if (other instanceof IStringValue) {
            return other.binaryOperation(operator, this);
        }
        if (!(other instanceof INumberValue)) {
            other = new IntValue();
        }
        INumberValue otherNumber = (INumberValue) other;
        switch (operator) {
            case "+" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(this.value + otherNumber.getValue());
                } else {
                    return new IntValue();
                }
            }
            case "<" -> {
                if (information && otherNumber.getInformation()) {
                    return new BooleanValue(this.value < otherNumber.getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case ">" -> {
                if (information && otherNumber.getInformation()) {
                    return new BooleanValue(this.value > otherNumber.getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case "-" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(this.value - otherNumber.getValue());
                } else {
                    return new IntValue();
                }
            }
            case "!=" -> {
                if (information && otherNumber.getInformation()) {
                    return new BooleanValue(this.value != otherNumber.getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case "==" -> {
                if (information && otherNumber.getInformation()) {
                    return new BooleanValue(this.value == otherNumber.getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case "*" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(this.value * otherNumber.getValue());
                } else {
                    return new IntValue();
                }
            }
            case "/" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(this.value / otherNumber.getValue());
                } else {
                    return new IntValue();
                }
            }
            case "<=" -> {
                if (information && otherNumber.getInformation()) {
                    return new BooleanValue(this.value <= otherNumber.getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case ">=" -> {
                if (information && otherNumber.getInformation()) {
                    return new BooleanValue(this.value >= otherNumber.getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case "max" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(Math.max(this.value, otherNumber.getValue()));
                } else {
                    return new IntValue();
                }
            }
            case "min" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(Math.min(this.value, otherNumber.getValue()));
                } else {
                    return new IntValue();
                }
            }
            case "%" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(this.value % otherNumber.getValue());
                } else {
                    return new IntValue();
                }
            }
            case ">>" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(this.value >> (int) otherNumber.getValue());
                } else {
                    return new IntValue();
                }
            }
            case "<<" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(this.value << (int) otherNumber.getValue());
                } else {
                    return new IntValue();
                }
            }
            case "pow" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue((int) Math.pow(this.value, otherNumber.getValue()));
                } else {
                    return new IntValue();
                }
            }
            case "^" -> {
                if (information && otherNumber.getInformation()) {
                    return new IntValue(this.value ^ (int) otherNumber.getValue());
                } else {
                    return new IntValue();
                }
            }
            default -> throw new UnsupportedOperationException(
                    "Binary operation " + operator + " not supported between " + getType() + " and " + other.getType());
        }
    }

    @Override
    @Impure
    public IValue unaryOperation(@NotNull String operator) {
        switch (operator) {
            case "++" -> {
                if (information) {
                    this.value += 1;
                    return new IntValue(this.value);
                } else {
                    return new IntValue();
                }
            }
            case "--" -> {
                if (information) {
                    this.value -= 1;
                    return new IntValue(this.value);
                } else {
                    return new IntValue();
                }
            }
            case "-" -> {
                if (information) {
                    this.value = -this.value;
                    return new IntValue(this.value);
                } else {
                    return new IntValue();
                }
            }
            case "+" -> {
                if (information) {
                    return new IntValue(this.value);
                } else {
                    return new IntValue();
                }
            }
            case "abs" -> {
                if (information) {
                    return new IntValue(Math.abs(this.value));
                } else {
                    return new IntValue();
                }
            }
            case "sin" -> {
                if (information) {
                    return Value.valueFactory(Math.sin(this.value));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                }
            }
            case "sqrt" -> {
                if (information) {
                    return Value.valueFactory(Math.sqrt(this.value));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                }
            }
            case "ceil", "floor" -> {
                if (information) {
                    return Value.valueFactory((int) (double) this.value);
                } else {
                    return new IntValue();
                }
            }
            default -> throw new UnsupportedOperationException("Unary operation " + operator + " not supported for " + getType());
        }
    }

    @NotNull
    @Override
    public Value copy() {
        return new IntValue(value, information);
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (other instanceof VoidValue) {
            this.information = false;
            return;
        }
        if (other instanceof JavaObject javaObject) {   // could be an Integer object
            if (javaObject.accessField("value", new Type(Type.TypeEnum.UNKNOWN)) instanceof IntValue intValue) {
                other = intValue;
            } else {
                this.information = false;
                return;
            }
        }
        if (other instanceof CharValue charValue) {   // cannot merge different types
            if (information && charValue.getInformation() && this.value == charValue.getValue()) {
                // keep information
            } else {
                this.information = false;
            }
            return;
        }
        assert other instanceof INumberValue;
        INumberValue otherInt = (INumberValue) other;
        if (this.information && otherInt.getInformation() && this.value == otherInt.getValue()) {
            // keep information
        } else {
            this.information = false;
        }
    }

    @Override
    public void setToUnknown() {
        this.information = false;
    }

    @Override
    public void setInitialValue() {
        value = 0;
        information = true;
    }

}
