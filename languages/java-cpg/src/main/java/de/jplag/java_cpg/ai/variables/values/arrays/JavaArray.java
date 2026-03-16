package de.jplag.java_cpg.ai.variables.values.arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.jplag.java_cpg.ai.JavaLanguageFeatureNotSupportedException;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.BooleanValue;
import de.jplag.java_cpg.ai.variables.values.IJavaObject;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;

/**
 * A Java Array representation. Java arrays are objects. Lists are modeled as Java arrays.
 * @author ujiqk
 * @version 1.0
 */
public class JavaArray extends JavaObject implements IJavaArray {

    private Type innerType;
    @Nullable
    private List<IValue> values;     // values = null: no information about the array
    // ToDo: add explicit information boolean

    /**
     * a Java Array with no information and undefined size.
     * @param innerType the type of the array elements.
     */
    public JavaArray(Type innerType) {
        super(new Type(Type.TypeEnum.ARRAY, innerType));
        this.innerType = innerType;
    }

    /**
     * a Java Array with exact information.
     * @param values the values of the array in the correct order.
     * @throws UnsupportedOperationException if the inner type is not supported.
     */
    public JavaArray(@NotNull List<IValue> values) {
        super(new Type(Type.TypeEnum.ARRAY, new Type(Type.TypeEnum.UNKNOWN)));
        if (values.isEmpty()) {
            this.innerType = null;
            this.values = values;
            return;
        }
        this.innerType = new Type(Type.TypeEnum.VOID);
        for (IValue value : values) {
            if (this.innerType.getTypeEnum() != Type.TypeEnum.VOID) {
                assert value.getType().equals(this.innerType)
                        || value.getType().getTypeEnum() == Type.TypeEnum.VOID : "Inconsistent types in array initialization: " + this.innerType
                                + " and " + value.getType();
                continue;
            }
            if (value.getType().getTypeEnum() != Type.TypeEnum.VOID) {
                this.innerType = value.getType();
            }
        }
        List<IValue> newValues = new ArrayList<>();
        for (IValue value : values) {   // exchange void values with unknown values of the inner type
            if (value.getType().getTypeEnum() == Type.TypeEnum.VOID) {
                switch (this.innerType.getTypeEnum()) {
                    case INT -> newValues.add(Value.valueFactory(new Type(Type.TypeEnum.INT)));
                    case BOOLEAN -> newValues.add(new BooleanValue());
                    case STRING -> newValues.add(Value.valueFactory(new Type(Type.TypeEnum.STRING)));
                    case OBJECT -> newValues.add(new JavaObject(innerType));
                    case ARRAY, LIST -> newValues.add(new JavaArray(innerType.getInnerType()));
                    case FLOAT -> newValues.add(Value.valueFactory(new Type(Type.TypeEnum.FLOAT)));
                    case CHAR -> newValues.add(Value.valueFactory(new Type(Type.TypeEnum.CHAR)));
                    case VOID -> {
                    }
                    default -> throw new UnsupportedOperationException("Array of type " + this.innerType + " not supported");
                }
            } else {
                newValues.add(value);
            }

        }
        this.values = newValues;
    }

    /**
     * a Java Array with exact length and type information.
     * @param length the length of the array; must contain information.
     * @param innerType the type of the array elements.
     */
    public JavaArray(@NotNull INumberValue length, Type innerType) {
        super(new Type(Type.TypeEnum.ARRAY, innerType));
        this.innerType = innerType;
        if (length.getInformation()) {
            int len = Math.max(0, (int) length.getValue());
            values = new ArrayList<>(len);
            Value placeholder = new VoidValue();
            for (int i = 0; i < len; i++) {
                values.add(placeholder.copy());
            }
        }
    }

    private JavaArray(@Nullable Type innerType, @Nullable List<IValue> values) {
        super(new Type(Type.TypeEnum.ARRAY, innerType));
        this.innerType = innerType;
        this.values = values;
    }

    /**
     * Access an element of the array.
     * @param index the index to access; does not have to contain information.
     * @return the superset of possible values at the given indexes.
     * @throws UnsupportedOperationException if the inner type is not supported.
     */
    public IValue arrayAccess(INumberValue index) {
        if (values != null && index.getInformation()) {
            int idx = (int) index.getValue();
            if (idx >= 0 && idx < values.size()) {
                return values.get(idx);
            }
        }
        // if no information, return an unknown value of the inner type
        if (innerType == null) {
            return new VoidValue();
        }
        return switch (innerType.getTypeEnum()) {
            case INT -> Value.valueFactory(new Type(Type.TypeEnum.INT));
            case BOOLEAN -> new BooleanValue();
            case STRING -> Value.valueFactory(new Type(Type.TypeEnum.STRING));
            case OBJECT -> new JavaObject(innerType);
            case ARRAY, LIST -> new JavaArray(innerType.getInnerType());
            case FLOAT -> Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
            case CHAR -> Value.valueFactory(new Type(Type.TypeEnum.CHAR));
            case VOID, UNKNOWN -> new VoidValue();
            default -> throw new UnsupportedOperationException("Array of type " + innerType + " not supported");
        };
    }

    /**
     * Assign a value to a position in the array.
     * @param index the index to assign to; does not have to contain information.
     * @param value the value to assign.
     */
    public void arrayAssign(INumberValue index, IValue value) {
        if (values != null && index.getInformation()) {
            int idx = (int) index.getValue();
            if (idx >= 0 && idx < values.size()) {
                values.set(idx, value);
            }
        } else {
            // no information about the array, set to unknown
            values = null;
        }
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
                    if (values != null) {
                        assert paramVars.getFirst().getType().equals(innerType);
                        values.add(paramVars.getFirst());
                    }
                    return new VoidValue();
                } else if (paramVars.size() == 2) { // index, element
                    if (values != null) {
                        assert paramVars.getFirst() instanceof INumberValue;
                        INumberValue index = (INumberValue) paramVars.getFirst();
                        if (index.getInformation()) {
                            int idx = (int) index.getValue();
                            if (idx >= 0 && idx <= values.size()) {
                                assert paramVars.getLast().getType().equals(innerType);
                                values.add(idx, paramVars.getLast());
                            } else {
                                values = null; // no information
                            }
                        } else {
                            values = null; // no information
                        }
                    }
                    return new VoidValue();
                } else {
                    throw new UnsupportedOperationException("add with " + paramVars.size() + " parameters is not supported");
                }
            }
            case "stream", "toArray" -> {
                if (paramVars != null && paramVars.size() == 1) {
                    return paramVars.getFirst();
                }
                assert paramVars == null || paramVars.isEmpty() : "Method " + methodName + " does not take parameters but " + paramVars.size()
                        + " were given";
                return this;
            }
            case "size" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (values != null) {
                    return Value.valueFactory(values.size());
                }
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "map" -> {
                // ToDo
                return this;
            }
            case "max" -> {
                // ToDo
                if (innerType.getTypeEnum() == Type.TypeEnum.INT) {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                } else if (innerType.getTypeEnum() == Type.TypeEnum.FLOAT) {
                    return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                } else {
                    return new VoidValue();
                }
            }
            case "indexOf" -> {
                assert paramVars.size() == 1;
                if (values != null) {
                    for (int i = 0; i < values.size(); i++) {
                        if (values.get(i).equals(paramVars.getFirst())) {
                            return Value.valueFactory(i);
                        }
                    }
                    return Value.valueFactory(-1);
                }
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "remove" -> {
                if (paramVars == null || paramVars.isEmpty()) {    // remove head
                    return this.callMethod("removeFirst", null, method, expectedType);
                }
                assert paramVars.size() == 1;
                if (values == null) {
                    return new VoidValue();
                }
                // either remove(int index) or remove(Object o) -> ToDo: cannot distinguish with Integer parameter
                if (paramVars.getFirst() instanceof INumberValue number) {
                    if (number.getInformation()) {
                        return values.remove((int) number.getValue());
                    }
                    return new VoidValue();
                } else {
                    for (int i = 0; i < values.size(); i++) {
                        if (values.get(i).equals(paramVars.getFirst())) {
                            values.remove(i);
                            return Value.valueFactory(true);
                        }
                    }
                    return Value.valueFactory(false);
                }
            }
            case "get", "elementAt" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.valueFactory(new Type(Type.TypeEnum.INT)));
                }
                assert paramVars.getFirst() instanceof INumberValue : "Method " + methodName + " requires a number as parameter but "
                        + paramVars.getFirst().getType() + " was given";
                return arrayAccess((INumberValue) paramVars.getFirst());
            }
            case "contains" -> {
                assert paramVars.size() == 1;
                if (values != null) {
                    for (IValue value : values) {
                        if (value.equals(paramVars.getFirst())) {
                            return Value.valueFactory(true);
                        }
                    }
                    return Value.valueFactory(false);
                }
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            case "lastIndexOf" -> {
                assert paramVars.size() == 1;
                if (values != null) {
                    for (int i = values.size() - 1; i >= 0; i--) {
                        if (values.get(i).equals(paramVars.getFirst())) {
                            return Value.valueFactory(i);
                        }
                    }
                    return Value.valueFactory(-1);
                }
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "getLast", "peek" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (values != null && !values.isEmpty()) {
                    return values.getLast();
                }
                // no information
                if (innerType == null) {
                    return new VoidValue();
                }
                return arrayAccess((INumberValue) Value.valueFactory(1));
            }
            case "removeLast", "pop" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (values != null && !values.isEmpty()) {
                    return values.removeLast();
                }
                // no information
                values = null;
                return Value.valueFactory(innerType);
            }
            case "addLast", "push" -> {
                assert paramVars.size() == 1;
                if (values != null) {
                    assert paramVars.getFirst().getType().equals(innerType);
                    values.add(paramVars.getFirst());
                }
                return Value.valueFactory(innerType);
            }
            case "removeFirst", "poll" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (values != null && !values.isEmpty()) {
                    return values.removeFirst();
                }
                // no information
                values = null;
                return Value.valueFactory(innerType);
            }
            case "isEmpty" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (values != null) {
                    return Value.valueFactory(values.isEmpty());
                }
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            case "fill" -> {        // void fill(int[] a, int val) or void fill(int[] a, int fromIndex, int toIndex, int val)
                assert paramVars.size() == 1 || paramVars.size() == 3;
                if (values != null) {
                    if (paramVars.size() == 1) {
                        IValue val = paramVars.getFirst();
                        for (int i = 0; i < values.size(); i++) {
                            values.set(i, val);
                        }
                    } else {
                        assert paramVars.getFirst() instanceof INumberValue;
                        assert paramVars.get(1) instanceof INumberValue;
                        INumberValue fromIndex = (INumberValue) paramVars.getFirst();
                        INumberValue toIndex = (INumberValue) paramVars.get(1);
                        if (fromIndex.getInformation() && toIndex.getInformation()) {
                            int fromIdx = (int) fromIndex.getValue();
                            int toIdx = (int) toIndex.getValue();
                            if (fromIdx >= 0 && toIdx <= values.size() && fromIdx <= toIdx) {
                                IValue val = paramVars.get(2);
                                for (int i = fromIdx; i < toIdx; i++) {
                                    values.set(i, val);
                                }
                            } else {
                                values = null; // no information
                            }
                        } else {
                            values = null; // no information
                        }
                    }
                }
                return new VoidValue();
            }
            case "sort" -> {        // void// sort(int[] a) or void sort(int[] a, int fromIndex, int toIndex)
                if (paramVars.size() == 1) {    // with Comparator
                    this.values = null; // ToDo
                    return new VoidValue();
                }
                this.values = null; // no information after sorting
                return new VoidValue();
            }
            case "copyOfRange" -> { // int[] copyOfRange(int[] original, int from, int to)
                assert paramVars.size() == 2;
                if (values != null) {
                    assert paramVars.getFirst() instanceof INumberValue;
                    assert paramVars.get(1) instanceof INumberValue;
                    INumberValue fromIndex = (INumberValue) paramVars.getFirst();
                    INumberValue toIndex = (INumberValue) paramVars.get(1);
                    if (fromIndex.getInformation() && toIndex.getInformation()) {
                        int fromIdx = (int) fromIndex.getValue();
                        int toIdx = (int) toIndex.getValue();
                        if (fromIdx >= 0 && toIdx <= values.size() && fromIdx <= toIdx) {
                            List<IValue> sublist = new ArrayList<>(values.subList(fromIdx, toIdx));
                            return new JavaArray(sublist);
                        }
                    }
                }
                return new JavaArray(innerType);
            }
            case "clear" -> {
                if (values != null) {
                    values.clear();
                }
                return new VoidValue();
            }
            case "getFirst", "peekFirst" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (values != null && !values.isEmpty()) {
                    return values.getFirst();
                }
                // no information
                if (innerType == null) {
                    return new VoidValue();
                }
                return arrayAccess((INumberValue) Value.valueFactory(0));
            }
            case "removeFirstOccurrence" -> {
                assert paramVars.size() == 1;
                if (values == null) {
                    return Value.valueFactory(false);
                }
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i).equals(paramVars.getFirst())) {
                        values.remove(i);
                        return Value.valueFactory(true);
                    }
                }
                return Value.valueFactory(false);
            }
            case "addAll" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof JavaArray otherArray) {
                    if (this.values != null && otherArray.values != null) {
                        for (IValue val : otherArray.values) {
                            assert val.getType().equals(this.innerType);
                            this.values.add(val);
                        }
                    } else {
                        this.values = null;
                    }
                } else {
                    this.values = null;
                }
                return new VoidValue();
            }
            case "addFirst" -> {
                assert paramVars.size() == 1;
                if (values != null) {
                    assert paramVars.getFirst().getType().equals(innerType);
                    values.addFirst(paramVars.getFirst());
                }
                return new VoidValue();
            }
            case "iterator" -> {
                throw new JavaLanguageFeatureNotSupportedException("Iterators are not supported");
            }
            case "clone" -> {
                assert paramVars == null || paramVars.isEmpty();
                return this.copy();
            }
            case "filter", "collect" -> {
                this.values = null;
                return Value.valueFactory(new Type(Type.TypeEnum.LIST, this.innerType));
            }
            case "findFirst" -> {
                if (values != null) {
                    if (!values.isEmpty()) {
                        return values.getFirst();
                    }
                }
                return new VoidValue();
            }
            case "forEach" -> {
                this.values = null;
                this.innerType = new Type(Type.TypeEnum.VOID);
                return Value.valueFactory(new Type(Type.TypeEnum.LIST, new Type(Type.TypeEnum.UNKNOWN)));
            }
            case "set" -> {
                assert paramVars.size() == 2;
                if (values != null) {
                    assert paramVars.getFirst() instanceof INumberValue;
                    INumberValue index = (INumberValue) paramVars.getFirst();
                    if (index.getInformation()) {
                        int idx = (int) index.getValue();
                        if (idx >= 0 && idx < values.size()) {
                            assert paramVars.getLast().getType().equals(innerType);
                            values.set(idx, paramVars.getLast());
                        } else {
                            values = null; // no information
                        }
                    } else {
                        values = null; // no information
                    }
                }
                return new VoidValue();
            }
            case "offer" -> {
                assert paramVars.size() == 1;
                setToUnknown();
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            case "containsAll" -> {
                assert paramVars.size() == 1;
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            case "removeAll", "retainAll" -> {
                assert paramVars.size() == 1;
                setToUnknown();
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            case "hasNext", "hasPrevious" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            case "next", "previous" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (innerType == null) {
                    return new VoidValue();
                }
                return Value.valueFactory(innerType);
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
                if (values != null) {
                    return Value.valueFactory(values.size());
                }
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            default -> throw new UnsupportedOperationException("Field " + fieldName + " is not supported for JavaArray");
        }
    }

    @NotNull
    @Override
    public JavaArray copy() {
        List<IValue> newValues = new ArrayList<>();
        if (values == null) {
            return new JavaArray(innerType);
        }
        for (IValue value : values) {
            newValues.add(value.copy());
        }
        return new JavaArray(innerType, newValues);
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (other instanceof VoidValue || other instanceof IJavaObject) {   // cannot merge different types
            other = new JavaArray(this.innerType);
        }
        assert other instanceof JavaArray;
        JavaArray otherArray = (JavaArray) other;
        if (this.innerType == null && otherArray.innerType != null) {
            this.innerType = otherArray.innerType;
        }
        if (!(Objects.equals(this.innerType, otherArray.innerType))) {
            this.values = null;
            return;
        }
        assert Objects.equals(this.innerType, otherArray.innerType);
        if (this.values == null || otherArray.values == null || this.values.size() != otherArray.values.size()) {
            this.values = null;
        } else {
            for (int i = 0; i < this.values.size(); i++) {
                this.values.get(i).merge(otherArray.values.get(i));
            }
        }
    }

    @Override
    public void setToUnknown() {
        values = null;
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
        values = null;
    }

}
