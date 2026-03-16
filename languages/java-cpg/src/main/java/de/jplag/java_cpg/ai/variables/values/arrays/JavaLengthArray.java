package de.jplag.java_cpg.ai.variables.values.arrays;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.BooleanValue;
import de.jplag.java_cpg.ai.variables.values.IJavaObject;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;

/**
 * Represents a Java array by its length and inner type.
 * @author ujiqk
 */
public class JavaLengthArray extends JavaObject implements IJavaArray {

    private Type innerType;
    private INumberValue length;        // array == null -> length == null

    /**
     * Creates a new JavaLengthArray with the given inner type and unknown length.
     * @param innerType The inner type of the array.
     */
    public JavaLengthArray(@NotNull Type innerType) {
        super(new Type(Type.TypeEnum.ARRAY, innerType));
        this.innerType = innerType;
        this.length = Value.getNewIntValue();
    }

    /**
     * Creates a new JavaLengthArray with the given inner type and length.
     * @param innerType The inner type of the array.
     * @param length The length of the array.
     */
    public JavaLengthArray(Type innerType, @NotNull INumberValue length) {
        super(new Type(Type.TypeEnum.ARRAY, innerType));
        this.length = length;
        this.innerType = Objects.requireNonNullElse(innerType, new Type(Type.TypeEnum.UNKNOWN));
    }

    /**
     * Creates a new JavaLengthArray with the inner type and length derived from the given values.
     * @param values The values to derive the inner type and length from.
     */
    public JavaLengthArray(@NotNull List<IValue> values) {
        super(new Type(Type.TypeEnum.ARRAY, values.isEmpty() ? new Type(Type.TypeEnum.UNKNOWN) : values.getFirst().getType()));
        if (values.isEmpty()) {
            this.innerType = new Type(Type.TypeEnum.UNKNOWN);
        } else {
            this.innerType = values.getFirst().getType();
        }
        this.length = (INumberValue) Value.valueFactory(values.size());
    }

    @Override
    public IValue arrayAccess(INumberValue index) {
        // if no information, return an unknown value of the inner type
        if (innerType == null || innerType.getTypeEnum() == Type.TypeEnum.UNKNOWN) {
            return new VoidValue();
        }
        return switch (innerType.getTypeEnum()) {
            case INT -> Value.valueFactory(new Type(Type.TypeEnum.INT));
            case BOOLEAN -> new BooleanValue();
            case STRING -> Value.valueFactory(new Type(Type.TypeEnum.STRING));
            case OBJECT -> new JavaObject(innerType);
            case ARRAY -> Value.valueFactory(new Type(Type.TypeEnum.ARRAY, innerType.getInnerType()));
            case LIST -> Value.valueFactory(new Type(Type.TypeEnum.LIST, innerType.getInnerType()));
            case FLOAT -> Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
            case CHAR -> Value.valueFactory(new Type(Type.TypeEnum.CHAR));
            default -> throw new UnsupportedOperationException("Array of type " + innerType + " not supported");
        };
    }

    @Override
    public void arrayAssign(INumberValue index, IValue value) {
        // do nothing
    }

    @Override
    public IValue callMethod(@NotNull String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "toString" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.getNewStringValue();
            }
            case "add" -> {
                if (paramVars.size() == 1) {
                    length.unaryOperation("++");
                } else if (paramVars.size() == 2) { // index, element
                    // do nothing, length stays the same
                } else {
                    throw new UnsupportedOperationException("add with " + paramVars.size() + " parameters is not supported");
                }
                return new VoidValue();
            }
            case "stream" -> {
                assert paramVars == null || paramVars.isEmpty();
                return this;
            }
            case "size" -> {
                assert paramVars == null || paramVars.isEmpty();
                return this.length;
            }
            case "map" -> {
                // ToDo
                return this;
            }
            case "max" -> {
                return arrayAccess((INumberValue) Value.valueFactory(1));
            }
            case "indexOf", "lastIndexOf" -> {
                assert paramVars.size() == 1;
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "remove" -> {
                assert paramVars == null || paramVars.size() == 1 || paramVars.isEmpty();
                // information lost
                length.setToUnknown();
                return new VoidValue();
            }
            case "get", "elementAt" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.getNewIntValue());
                }
                assert paramVars.getFirst() instanceof INumberValue : "Parameter for get/elementAt must be a number value but was "
                        + paramVars.getFirst().getClass();
                return arrayAccess((INumberValue) paramVars.getFirst());
            }
            case "contains" -> {
                assert paramVars.size() == 1;
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            case "getLast", "findFirst", "findAny" -> {
                assert paramVars == null || paramVars.isEmpty();
                return arrayAccess((INumberValue) Value.valueFactory(1));
            }
            case "removeLast", "removeFirst" -> {
                assert paramVars == null || paramVars.isEmpty();
                this.length.unaryOperation("--");
                return new VoidValue();
            }
            case "addLast" -> {
                assert paramVars.size() == 1;
                this.length.unaryOperation("++");
                return new VoidValue();
            }
            case "isEmpty" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (length.getInformation()) {
                    return Value.valueFactory(length.getValue() == 0);
                }
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @Override
    public IValue accessField(@NotNull String fieldName, @NotNull Type expectedType) {
        switch (fieldName) {
            case "length" -> {
                return this.length;
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        if (this.length == null) {
            return new JavaLengthArray(this.innerType);
        }
        INumberValue newLength = (INumberValue) this.length.copy();
        return new JavaLengthArray(this.innerType, newLength);
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (other instanceof VoidValue) {
            other = new JavaLengthArray(this.innerType);
        }
        assert other instanceof JavaLengthArray : "Can only merge with another JavaLengthArray but got " + other.getClass();
        final JavaLengthArray otherArray = (JavaLengthArray) other;
        if (this.innerType.getTypeEnum() == Type.TypeEnum.UNKNOWN) {
            this.innerType = otherArray.innerType;
        }
        if (otherArray.innerType.getTypeEnum() != Type.TypeEnum.UNKNOWN) {
            assert Objects.equals(this.innerType, otherArray.innerType) : "Cannot merge arrays of different inner types: " + this.innerType + " and "
                    + ((JavaLengthArray) other).innerType;
        }
        if (this.length == null || otherArray.length == null) {
            this.length = null;
        } else {
            this.length.merge(otherArray.length);
        }
    }

    @Override
    public void setToUnknown() {
        length = Value.getNewIntValue();
    }

    @Override
    public void setToUnknown(Set<IJavaObject> visited) {
        setToUnknown();
    }

    @Override
    public void setInitialValue(Set<IJavaObject> visited) {
        setInitialValue();
    }

    @Override
    public void setInitialValue() {
        length = null;
    }

}
