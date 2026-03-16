package de.jplag.java_cpg.ai.variables.values;

import org.jetbrains.annotations.NotNull;

import de.jplag.java_cpg.ai.variables.Type;

/**
 * Represents the Java null value.
 * @author ujiqk
 * @version 1.0
 */
public class NullValue extends Value {

    /**
     * Constructs a new NullValue.
     */
    public NullValue() {
        super(new Type(Type.TypeEnum.NULL));
    }

    @Override
    public IValue binaryOperation(@NotNull String operator, @NotNull IValue other) {
        switch (operator) {
            case "==" -> {
                return new BooleanValue(other instanceof NullValue);
            }
            case "!=" -> {
                return new BooleanValue(!(other instanceof NullValue));
            }
            default -> throw new UnsupportedOperationException("Operator " + operator + " not supported for NullValue.");
        }
    }

    @NotNull
    @Override
    public Value copy() {
        return new NullValue();
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (!(other instanceof NullValue)) {
            throw new IllegalStateException();
        }
        assert other instanceof NullValue;  // other cases are handled in Variable.merge()
    }

    @Override
    public void setToUnknown() {
        // ToDo: replace with JavaObject?
    }

    @Override
    public void setInitialValue() {
        // do nothing
    }

}
