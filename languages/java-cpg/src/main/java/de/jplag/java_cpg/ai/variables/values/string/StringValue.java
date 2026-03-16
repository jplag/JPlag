package de.jplag.java_cpg.ai.variables.values.string;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.jplag.java_cpg.ai.JavaLanguageFeatureNotSupportedException;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.BooleanValue;
import de.jplag.java_cpg.ai.variables.values.IJavaObject;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.NullValue;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.arrays.IJavaArray;
import de.jplag.java_cpg.ai.variables.values.chars.ICharValue;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;

/**
 * String representation. Strings are objects with added functionality.
 * @author ujiqk
 * @version 1.0
 */
public class StringValue extends JavaObject implements IStringValue {

    private boolean information;
    private String value;

    /**
     * A string value with no information.
     */
    public StringValue() {
        super(new Type(Type.TypeEnum.STRING));
        information = false;
    }

    /**
     * A string value with exact information.
     * @param value the string value
     */
    public StringValue(String value) {
        super(new Type(Type.TypeEnum.STRING));
        this.value = value;
        information = true;
    }

    private StringValue(String value, boolean information) {
        super(new Type(Type.TypeEnum.STRING));
        this.value = value;
        this.information = information;
    }

    @Override
    public IValue callMethod(@NotNull String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "length" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (information) {
                    return Value.valueFactory(value.length());
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                }
            }
            case "parseInt" -> {
                assert paramVars.size() == 1;
                StringValue str = (StringValue) paramVars.getFirst();
                if (str.getInformation()) {
                    return Value.valueFactory(Integer.parseInt(Objects.requireNonNull(str.getValue())));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                }
            }
            case "parseDouble" -> {
                assert paramVars.size() == 1;
                StringValue str = (StringValue) paramVars.getFirst();
                if (str.getInformation()) {
                    return Value.valueFactory(Double.parseDouble(Objects.requireNonNull(str.getValue())));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                }
            }
            case "startsWith" -> {
                assert paramVars.size() == 1;
                StringValue prefix = (StringValue) paramVars.getFirst();
                if (information && prefix.getInformation()) {
                    return Value.valueFactory(value.startsWith(Objects.requireNonNull(prefix.getValue())));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "equals" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.valueFactory(new Type(Type.TypeEnum.STRING)));
                }
                StringValue other = (StringValue) paramVars.getFirst();
                if (information && other.getInformation()) {
                    return Value.valueFactory(value.equals(other.getValue()));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "split" -> {   // public String[] split(String regex)
                assert paramVars.size() == 1 || paramVars.size() == 2;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.valueFactory(new Type(Type.TypeEnum.STRING)));
                }
                StringValue regexValue = (StringValue) paramVars.getFirst();
                if (!information || !regexValue.getInformation()) {
                    return Value.getNewArayValue(new Type(Type.TypeEnum.STRING));
                }
                assert regexValue.getValue() != null && value != null;
                String[] parts;
                if (paramVars.size() == 1) {
                    parts = value.split(regexValue.getValue());
                } else {
                    INumberValue limitValue = (INumberValue) paramVars.get(1);
                    if (!limitValue.getInformation()) {
                        return Value.getNewArayValue(new Type(Type.TypeEnum.STRING));
                    }
                    parts = value.split(regexValue.getValue(), (int) limitValue.getValue());
                }
                IJavaArray array = Value.getNewArayValue(new Type(Type.TypeEnum.STRING));
                for (int i = 0; i < parts.length; i++) {
                    array.arrayAssign((INumberValue) Value.valueFactory(i), new StringValue(parts[i]));
                }
                return array;
            }
            case "charAt" -> {   // public char charAt(int index)
                assert paramVars.size() == 1;
                INumberValue indexValue = (INumberValue) paramVars.getFirst();
                if (!information || !indexValue.getInformation()) {
                    return Value.valueFactory(new Type(Type.TypeEnum.CHAR));
                }
                assert value != null;
                double index = indexValue.getValue();
                if (index < 0 || index >= value.length()) {
                    return new VoidValue();
                }
                return Value.valueFactory(value.charAt((int) index));
            }
            case "toCharArray" -> {   // public char[] toCharArray()
                assert paramVars == null || paramVars.isEmpty();
                if (!information) {
                    return Value.getNewArayValue(new Type(Type.TypeEnum.CHAR));
                }
                assert value != null;
                char[] chars = value.toCharArray();
                IJavaArray array = Value.getNewArayValue(new Type(Type.TypeEnum.CHAR));
                for (int i = 0; i < chars.length; i++) {
                    array.arrayAssign((INumberValue) Value.valueFactory(i), Value.valueFactory(chars[i]));
                }
                return array;
            }
            case "concat" -> {   // public String concat(String str)
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.valueFactory(new Type(Type.TypeEnum.STRING)));
                }
                StringValue str = (StringValue) paramVars.getFirst();
                if (information && str.getInformation()) {
                    return new StringValue(this.value + str.getValue());
                } else {
                    return new StringValue();
                }
            }
            case "substring" -> {   // public String substring(int beginIndex, int endIndex) || public String substring(int beginIndex)
                assert paramVars.size() == 2 || paramVars.size() == 1;
                INumberValue beginIndexValue = (INumberValue) paramVars.get(0);
                INumberValue endIndexValue;
                if (paramVars.size() == 1) {
                    if (value == null) {
                        endIndexValue = Value.getNewIntValue();
                    } else {
                        endIndexValue = Value.getNewIntValue(value.length());
                    }
                } else {
                    endIndexValue = (INumberValue) paramVars.get(1);
                }
                if (information && beginIndexValue.getInformation() && endIndexValue.getInformation()) {
                    int beginIndex = (int) beginIndexValue.getValue();
                    int endIndex = (int) endIndexValue.getValue();
                    if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
                        assert false;
                        return new VoidValue();
                    }
                    return new StringValue(this.value.substring(beginIndex, endIndex));
                } else {
                    return new StringValue();
                }
            }
            case "trim" -> {   // public String trim()
                assert paramVars == null || paramVars.isEmpty();
                if (information) {
                    return new StringValue(this.value.trim());
                } else {
                    return new StringValue();
                }
            }
            case "matches" -> {   // public boolean matches(String regex)
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.valueFactory(new Type(Type.TypeEnum.STRING)));
                }
                StringValue regexValue = (StringValue) paramVars.getFirst();
                if (information && regexValue.getInformation()) {
                    return Value.valueFactory(this.value.matches(Objects.requireNonNull(regexValue.getValue())));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "repeat" -> {   // public String repeat(int count)
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.valueFactory(new Type(Type.TypeEnum.STRING)));
                }
                INumberValue countValue = (INumberValue) paramVars.getFirst();
                if (information && countValue.getInformation()) {
                    int count = (int) countValue.getValue();
                    assert count >= 0;
                    return new StringValue(this.value.repeat(count));
                } else {
                    return new StringValue();
                }
            }
            case "toUpperCase" -> {   // public String toUpperCase()
                assert paramVars == null || paramVars.isEmpty();
                if (information) {
                    return new StringValue(this.value.toUpperCase());
                } else {
                    return new StringValue();
                }
            }
            case "stripTrailing" -> {   // public String stripTrailing()
                assert paramVars == null || paramVars.isEmpty();
                if (information) {
                    return new StringValue(this.value.stripTrailing());
                } else {
                    return new StringValue();
                }
            }
            case "equalsIgnoreCase" -> {   // public boolean equalsIgnoreCase(String anotherString)
                assert paramVars.size() == 1;
                StringValue other = (StringValue) paramVars.getFirst();
                if (information && other.getInformation()) {
                    return Value.valueFactory(this.value.equalsIgnoreCase(other.getValue()));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "replace" -> {   // public String replace(CharSequence target, CharSequence replacement)
                assert paramVars.size() == 2;
                StringValue target = (StringValue) paramVars.getFirst();
                StringValue replacement = (StringValue) paramVars.getLast();
                if (information && target.getInformation() && replacement.getInformation()) {
                    return new StringValue(
                            this.value.replace(Objects.requireNonNull(target.getValue()), Objects.requireNonNull(replacement.getValue())));
                } else {
                    return new StringValue();
                }
            }
            case "format", "formatted", "replaceAll" -> {
                assert paramVars.size() >= 1;
                return new StringValue();
            }
            case "contains" -> {   // public boolean contains(CharSequence s)
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.valueFactory(new Type(Type.TypeEnum.STRING)));
                }
                StringValue s = (StringValue) paramVars.getFirst();
                if (information && s.getInformation()) {
                    return Value.valueFactory(this.value.contains(Objects.requireNonNull(s.getValue())));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "isBlank" -> {   // public boolean isBlank()
                assert paramVars == null || paramVars.isEmpty();
                if (information) {
                    return Value.valueFactory(this.value.isBlank());
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "isEmpty" -> {   // public boolean isEmpty()
                assert paramVars == null || paramVars.isEmpty();
                if (information) {
                    return Value.valueFactory(this.value.isEmpty());
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "indexOf" -> {   // public int indexOf(String str) || public int indexOf(String str, int fromIndex)
                assert paramVars.size() == 1 || paramVars.size() == 2;
                StringValue str = (StringValue) paramVars.getFirst();
                INumberValue fromIndexValue;
                if (paramVars.size() == 1) {
                    fromIndexValue = Value.getNewIntValue(0);
                } else {
                    fromIndexValue = (INumberValue) paramVars.get(1);
                }
                if (information && str.getInformation() && fromIndexValue.getInformation()) {
                    int fromIndex = (int) fromIndexValue.getValue();
                    if (fromIndex < 0 || value == null || fromIndex > value.length()) {
                        return new VoidValue();
                    }
                    return Value.valueFactory(this.value.indexOf(Objects.requireNonNull(str.getValue()), fromIndex));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                }
            }
            case "endsWith" -> {   // public boolean endsWith(String suffix)
                assert paramVars.size() == 1;
                StringValue suffix = (StringValue) paramVars.getFirst();
                if (information && suffix.getInformation()) {
                    return Value.valueFactory(this.value.endsWith(Objects.requireNonNull(suffix.getValue())));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "valueOf" -> {   // public static String valueOf(Object obj)
                assert paramVars.size() == 1;
                return new StringValue();
            }
            case "chars" -> {   // public IntStream chars()
                assert paramVars == null || paramVars.isEmpty();
                throw new JavaLanguageFeatureNotSupportedException("IntStream not supported");
            }
            case "contentsEquals" -> {   // public boolean contentsEquals(CharSequence cs)
                assert paramVars.size() == 1;
                StringValue cs = (StringValue) paramVars.getFirst();
                if (information && cs.getInformation()) {
                    return Value.valueFactory(this.value.contentEquals(Objects.requireNonNull(cs.getValue())));
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "toLowerCase" -> {   // public String toLowerCase()
                assert paramVars == null || paramVars.isEmpty();
                if (information) {
                    return new StringValue(this.value.toLowerCase());
                } else {
                    return new StringValue();
                }
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @Override
    public Value accessField(@NotNull String fieldName, @NotNull Type expectedType) {
        throw new UnsupportedOperationException("Access field not supported in StringValue (" + fieldName + ")");
    }

    @Override
    public IValue binaryOperation(@NotNull String operator, @NotNull IValue other) {
        if (other instanceof VoidValue) {
            return new VoidValue();
        }
        if (operator.equals("+") && other instanceof INumberValue inumbervalue) {
            if (information && inumbervalue.getInformation()) {
                return new StringValue(this.value + inumbervalue.getValue());
            } else {
                return new StringValue();
            }
        } else if (operator.equals("+") && other instanceof IStringValue stringValue) {
            if (information && stringValue.getInformation()) {
                return new StringValue(this.value + stringValue.getValue());
            } else {
                return new StringValue();
            }
        } else if (operator.equals("+") && other instanceof ICharValue charValue) {
            if (information && charValue.getInformation()) {
                return new StringValue(this.value + charValue.getValue());
            } else {
                return new StringValue();
            }
        } else if (operator.equals("+") && other instanceof BooleanValue boolValue) {
            if (information && boolValue.getInformation()) {
                return new StringValue(this.value + boolValue.getValue());
            } else {
                return new StringValue();
            }
        } else if (operator.equals("==") && other instanceof NullValue) {
            if (information) {
                return Value.valueFactory(this.value == null);
            } else {
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
        } else if (operator.equals("!=") && other instanceof NullValue) {
            if (information) {
                return Value.valueFactory(this.value != null);
            } else {
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
        } else if (operator.equals("==") && other instanceof IStringValue otherString) {
            if (information && otherString.getInformation()) {
                return Value.valueFactory(java.util.Objects.equals(this.value, otherString.getValue()));
            } else {
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
        } else if (operator.equals("+") && other instanceof IJavaObject javaObject) {
            // case: JavaObject with toString method
            IValue toStringResult = javaObject.callMethod("toString", List.of(), null, new Type(Type.TypeEnum.STRING));
            if (toStringResult instanceof IStringValue otherStringFromObject && information && otherStringFromObject.getInformation()) {
                return new StringValue(this.value + otherStringFromObject.getValue());
            }
            return new StringValue();
        }
        return new VoidValue();
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new StringValue(value, information);
    }

    @NotNull
    @Override
    public StringValue copy(Map<JavaObject, JavaObject> copiedObjects) {
        // StringValue doesn't have fields that could create cycles,
        // so we can just return a simple copy
        return new StringValue(value, information);
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (other instanceof VoidValue || other instanceof NullValue) {
            this.information = false;
            this.value = null;
            return;
        }
        if (other instanceof IJavaObject javaObject && javaObject.isNull()) {
            other = new StringValue(null);
        }
        assert other instanceof StringValue : "Cannot merge " + this.getClass() + " with " + other.getClass();
        StringValue otherString = (StringValue) other;
        if (this.information && otherString.information && java.util.Objects.equals(this.value, otherString.value)) {
            // keep value
        } else {
            this.information = false;
            this.value = null;
        }
    }

    @Override
    public void setToUnknown() {
        this.information = false;
        this.value = null;
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
        information = true;
        value = null;
    }

    /**
     * @return true if the string value has definite information (i.e., a known value), false otherwise.
     */
    public boolean getInformation() {
        return information;
    }

    /**
     * @return if known, the string value.
     */
    public String getValue() {
        assert information;
        return value;
    }

}
