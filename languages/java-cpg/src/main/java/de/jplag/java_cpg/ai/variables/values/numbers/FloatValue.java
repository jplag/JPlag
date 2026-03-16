package de.jplag.java_cpg.ai.variables.values.numbers;

import java.util.Set;

import org.checkerframework.dataflow.qual.Impure;
import org.jetbrains.annotations.NotNull;

import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.BooleanValue;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.string.IStringValue;

/**
 * Represents a floating point value with optional exact information.
 */
public class FloatValue extends Value implements INumberValue, IFloatNumber {

    private double value;
    private boolean information;    // whether exact information is available

    /**
     * a IntValue with no information.
     */
    public FloatValue() {
        super(new Type(Type.TypeEnum.FLOAT));
        information = false;
    }

    /**
     * Constructor for FloatValue with exact information.
     * @param value the float value.
     */
    public FloatValue(double value) {
        super(new Type(Type.TypeEnum.FLOAT));
        this.value = value;
        information = true;
    }

    /**
     * Constructor for FloatValue with a range.
     * @param lowerBound the lower bound of the range.
     * @param upperBound the upper bound of the range.
     */
    public FloatValue(double lowerBound, double upperBound) {
        super(new Type(Type.TypeEnum.FLOAT));
        assert lowerBound <= upperBound;
        if (lowerBound == upperBound) {
            this.value = lowerBound;
            this.information = true;
        } else {
            this.information = false;
        }
    }

    /**
     * Constructor for FloatValue with a set of possible values.
     * @param values the set of possible float values.
     */
    public FloatValue(@NotNull Set<Double> values) {
        super(new Type(Type.TypeEnum.FLOAT));
        if (values.size() == 1) {
            this.value = values.iterator().next();
            this.information = true;
        } else {
            this.information = false;
        }
    }

    private FloatValue(double value, boolean information) {
        super(new Type(Type.TypeEnum.FLOAT));
        this.value = value;
        this.information = information;
    }

    /**
     * @return whether exact information is available.
     */
    public boolean getInformation() {
        return information;
    }

    /**
     * @return the value. Only call if information is true.
     */
    public double getValue() {
        assert information;
        return value;
    }

    @Override
    public IValue binaryOperation(@NotNull String operator, @NotNull IValue other) {
        if (other instanceof VoidValue) {
            return new VoidValue();
        }
        if (other instanceof IStringValue) {
            return other.binaryOperation(operator, this);
        }
        assert other instanceof INumberValue : "Expected a number value for binary operation, but got " + other.getType();
        switch (operator) {
            case "+" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new FloatValue(this.value + ((INumberValue) other).getValue());
                } else {
                    return new FloatValue();
                }
            }
            case "<" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new BooleanValue(this.value < ((INumberValue) other).getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case ">" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new BooleanValue(this.value > ((INumberValue) other).getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case "-" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new FloatValue(this.value - ((INumberValue) other).getValue());
                } else {
                    return new FloatValue();
                }
            }
            case "==" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new BooleanValue(this.value == ((INumberValue) other).getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case "!=" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new BooleanValue(this.value != ((INumberValue) other).getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case "*" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new FloatValue(this.value * ((INumberValue) other).getValue());
                } else {
                    return new FloatValue();
                }
            }
            case "/" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new FloatValue(this.value / ((INumberValue) other).getValue());
                } else {
                    return new FloatValue();
                }
            }
            case "pow" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new FloatValue(Math.pow(this.value, ((INumberValue) other).getValue()));
                } else {
                    return new FloatValue();
                }
            }
            case "<=" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new BooleanValue(this.value <= ((INumberValue) other).getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case ">=" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new BooleanValue(this.value >= ((INumberValue) other).getValue());
                } else {
                    return new BooleanValue();
                }
            }
            case "%" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    return new FloatValue(this.value % ((INumberValue) other).getValue());
                } else {
                    return new FloatValue();
                }
            }
            case "compareTo" -> {
                if (information && ((INumberValue) other).getInformation()) {
                    double otherValue = ((INumberValue) other).getValue();
                    if (this.value < otherValue) {
                        return new FloatValue(-1);
                    } else if (this.value > otherValue) {
                        return new FloatValue(1);
                    } else {
                        return new FloatValue(0);
                    }
                } else {
                    return new FloatValue();
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
                    return new FloatValue(this.value);
                } else {
                    return new BooleanValue();
                }
            }
            case "-" -> {
                if (information) {
                    this.value = -this.value;
                    return new FloatValue(this.value);
                } else {
                    return new FloatValue();
                }
            }
            case "sqrt" -> {
                if (information) {
                    return new FloatValue(Math.sqrt(this.value));
                } else {
                    return new FloatValue();
                }
            }
            case "abs" -> {
                if (information) {
                    return new FloatValue(Math.abs(this.value));
                } else {
                    return new FloatValue();
                }
            }
            case "ceil" -> {
                if (information) {
                    return new FloatValue(Math.ceil(this.value));
                } else {
                    return new FloatValue();
                }
            }
            case "floor" -> {
                if (information) {
                    return new FloatValue(Math.floor(this.value));
                } else {
                    return new FloatValue();
                }
            }
            default -> throw new UnsupportedOperationException("Unary operation " + operator + " not supported for " + getType());
        }
    }

    @NotNull
    @Override
    public Value copy() {
        return new FloatValue(value, information);
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (other instanceof VoidValue) {
            this.information = false;
            return;
        }
        assert other instanceof FloatValue;
        FloatValue otherFloat = (FloatValue) other;
        if (this.information && otherFloat.information && this.value == otherFloat.value) {
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
        value = 0.0;
        information = true;
    }

}
