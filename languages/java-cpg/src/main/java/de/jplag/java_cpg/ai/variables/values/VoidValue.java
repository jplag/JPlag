package de.jplag.java_cpg.ai.variables.values;

import org.jetbrains.annotations.NotNull;

import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.numbers.IFloatNumber;
import de.jplag.java_cpg.ai.variables.values.numbers.IIntNumber;
import de.jplag.java_cpg.ai.variables.values.string.IStringValue;

/**
 * Void typed value. Represents no value or completely unknown value.
 * @author ujiqk
 * @version 1.0
 */
public class VoidValue extends Value {

    /**
     * Creates a new Void typed value. Represents no value or completely unknown value.
     */
    public VoidValue() {
        super(new Type(Type.TypeEnum.VOID));
    }

    @Override
    public IValue binaryOperation(@NotNull String operator, @NotNull IValue other) {
        switch (operator) {
            case "==", ">", "<", ">=", "<=", "!=", "instanceof" -> {
                return new BooleanValue();
            }
            case "+", "-", "*", "/", "%" -> {
                return switch (other) {
                    case IIntNumber _ -> Value.valueFactory(new Type(Type.TypeEnum.INT));
                    case IFloatNumber _ -> Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                    case IStringValue _ -> Value.valueFactory(new Type(Type.TypeEnum.STRING));
                    default -> new VoidValue();
                };
            }
            case "&", "^" -> {
                return new VoidValue();
            }
            default -> throw new UnsupportedOperationException("Operator " + operator + " not supported for VoidValue.");
        }
    }

    @Override
    public IValue unaryOperation(@NotNull String operator) {
        switch (operator) {
            case "!" -> {
                return new BooleanValue();
            }
            case "--", "++", "abs", "+", "-", "throw" -> {
                return new VoidValue();
            }
            default -> throw new IllegalArgumentException("Unary operation " + operator + " not supported for " + getType());
        }
    }

    @NotNull
    @Override
    public Value copy() {
        return new VoidValue();
    }

    @Override
    public void merge(@NotNull IValue other) {
        // do nothing
    }

    @Override
    public void setToUnknown() {
        // do nothing
    }

    @Override
    public void setInitialValue() {
        // nothing
    }

}
