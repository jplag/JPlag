package de.jplag.java_cpg.ai.variables.values.string;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.IJavaObject;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.NullValue;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;
import de.jplag.java_cpg.ai.variables.values.string.regex.RegexChar;
import de.jplag.java_cpg.ai.variables.values.string.regex.RegexChars;
import de.jplag.java_cpg.ai.variables.values.string.regex.RegexItem;

/**
 * String representation using regex-like structures. Strings are objects with added functionality.
 * @author ujiqk
 * @version 1.0
 */
public class StringRegexValue extends JavaObject implements IStringValue {

    // String=null <--> contentRegex=null
    @Nullable
    private List<RegexItem> contentRegex;
    private boolean unknown;

    /**
     * A string value with no information.
     */
    public StringRegexValue() {
        super(new Type(Type.TypeEnum.STRING));
        unknown = true;
    }

    /**
     * A string value with exact information.
     * @param value The exact string value, null for null.
     */
    public StringRegexValue(@Nullable String value) {
        super(new Type(Type.TypeEnum.STRING));
        if (value == null) {
            contentRegex = null;
            unknown = false;
            return;
        }
        unknown = false;
        contentRegex = new ArrayList<>();
        for (char c : value.toCharArray()) {
            contentRegex.add(new RegexChar(c));
        }
    }

    /**
     * A string value with possible values.
     * @param possibleValues The possible string values.
     */
    public StringRegexValue(@NotNull Set<String> possibleValues) {
        super(new Type(Type.TypeEnum.STRING));
        unknown = false;
        contentRegex = new ArrayList<>();
        for (String possibleValue : possibleValues) {
            List<RegexItem> possibleValueRegex = new ArrayList<>();
            for (char c : possibleValue.toCharArray()) {
                possibleValueRegex.add(new RegexChar(c));
            }
            appendAtPos(contentRegex, possibleValueRegex, contentRegex.size() - 1);
        }
    }

    private StringRegexValue(@Nullable List<RegexItem> contentRegex, boolean unknown) {
        super(new Type(Type.TypeEnum.STRING));
        this.contentRegex = contentRegex;
        this.unknown = unknown;
    }

    @NotNull
    private static List<RegexItem> appendAtPos(@NotNull List<RegexItem> original, @NotNull List<RegexItem> other, int i) {
        int length = original.size();
        i++;
        for (int j = 0; j < other.size(); j++) {
            if (i < length) {
                original.get(i).merge(other.get(j));
            } else {
                original.add(other.get(j));
            }
            i++;
        }
        return original;
    }

    @Override
    public IValue callMethod(@NotNull String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "length" -> {
                assert paramVars == null || paramVars.isEmpty();
                if (unknown || contentRegex == null) {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                }
                if ((contentRegex.getLast() instanceof RegexChars chars && chars.canBeEmpty())) {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT)); // ToDo could return int interval
                }
                return Value.valueFactory(contentRegex.size());
            }
            case "parseInt" -> {
                assert paramVars.size() == 1;
                if (unknown || contentRegex == null) {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                }
                List<Character> possibleChars = new ArrayList<>();
                for (RegexItem item : contentRegex) {
                    if (item instanceof RegexChars) {
                        return Value.valueFactory(new Type(Type.TypeEnum.INT));
                    } else if (item instanceof RegexChar regexChar) {
                        possibleChars.add(regexChar.getContent());
                    }
                }
                int number = Integer.parseInt(possibleChars.toString());
                return Value.valueFactory(number);
            }
            case "parseBoolean" -> {
                assert paramVars.size() == 1;
                if (unknown || contentRegex == null) {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
                List<Character> possibleChars = new ArrayList<>();
                for (RegexItem item : contentRegex) {
                    if (item instanceof RegexChars) {
                        return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                    } else if (item instanceof RegexChar regexChar) {
                        possibleChars.add(regexChar.getContent());
                    }
                }
                String str = possibleChars.toString().toLowerCase();
                if (str.equals("true")) {
                    return Value.valueFactory(true);
                } else if (str.equals("false")) {
                    return Value.valueFactory(false);
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "parseDouble" -> {
                assert paramVars.size() == 1;
                if (unknown || contentRegex == null) {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                }
                List<Character> possibleChars = new ArrayList<>();
                for (RegexItem item : contentRegex) {
                    if (item instanceof RegexChars) {
                        return Value.valueFactory(new Type(Type.TypeEnum.INT));
                    } else if (item instanceof RegexChar regexChar) {
                        possibleChars.add(regexChar.getContent());
                    }
                }
                double number = Double.parseDouble(possibleChars.toString());
                return Value.valueFactory(number);
            }
            case "startsWith" -> {
                assert paramVars.size() == 1;
                StringRegexValue other = (StringRegexValue) paramVars.getFirst();
                if (this.unknown || other.unknown) {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
                assert this.contentRegex != null && other.contentRegex != null;
                if (this.contentRegex.size() < other.contentRegex.size()) {
                    return Value.valueFactory(false);
                }
                boolean unknownMatch = false;
                for (int i = 0; i < other.contentRegex.size(); i++) {
                    RegexItem thisItem = this.contentRegex.get(i);
                    RegexItem otherItem = other.contentRegex.get(i);
                    if (thisItem instanceof RegexChar thisChar && otherItem instanceof RegexChar otherChar) {
                        if (thisChar.getContent() == otherChar.getContent()) {
                            // match
                        } else {
                            return Value.valueFactory(false);
                        }
                    } else {
                        unknownMatch = true;
                    }
                }
                if (unknownMatch) {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                } else {
                    return Value.valueFactory(true);
                }
            }
            case "equals" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.valueFactory(new Type(Type.TypeEnum.STRING)));
                }
                StringRegexValue other = (StringRegexValue) paramVars.getFirst();
                if (this.unknown || other.unknown) {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
                assert this.contentRegex != null && other.contentRegex != null;
                if (this.contentRegex.size() != other.contentRegex.size()) {
                    return Value.valueFactory(false);
                }
                boolean unknownMatch = false;
                for (int i = 0; i < other.contentRegex.size(); i++) {
                    RegexItem thisItem = this.contentRegex.get(i);
                    RegexItem otherItem = other.contentRegex.get(i);
                    if (thisItem instanceof RegexChar thisChar && otherItem instanceof RegexChar otherChar) {
                        if (thisChar.getContent() == otherChar.getContent()) {
                            // match
                        } else {
                            return Value.valueFactory(false);
                        }
                    } else {
                        unknownMatch = true;
                    }
                }
                if (unknownMatch) {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                } else {
                    return Value.valueFactory(true);
                }
            }
            case "toUpperCase" -> {
                if (unknown) {
                    return new StringRegexValue(null, true);
                }
                if (contentRegex == null) {
                    return new StringRegexValue(null, false);
                }
                List<RegexItem> newContentRegex = new ArrayList<>();
                for (RegexItem item : contentRegex) {
                    if (item instanceof RegexChar regexChar) {
                        newContentRegex.add(new RegexChar(Character.toUpperCase(regexChar.getContent())));
                    } else if (item instanceof RegexChars regexChars) {
                        List<Character> upperChars = new ArrayList<>();
                        for (Character c : regexChars.getContent()) {
                            if (c != null) {
                                upperChars.add(Character.toUpperCase(c));
                            } else {
                                upperChars.add(null);
                            }
                        }
                        newContentRegex.add(new RegexChars(upperChars));
                    }
                }
                return new StringRegexValue(newContentRegex, false);
            }
            case "isBlank" -> { // all whitespace or empty or null
                if (unknown) {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
                if (contentRegex == null) {
                    return Value.valueFactory(true);
                }
                boolean unknownMatch = false;
                for (RegexItem item : contentRegex) {
                    if (item instanceof RegexChar regexChar) {
                        if (!Character.isWhitespace(regexChar.getContent())) {
                            return Value.valueFactory(false);
                        }
                    } else if (item instanceof RegexChars regexChars) {
                        for (Character c : regexChars.getContent()) {
                            if (c == null || !Character.isWhitespace(c)) {
                                unknownMatch = true;
                            }
                        }
                    }
                }
                if (unknownMatch) {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                } else {
                    return Value.valueFactory(true);
                }
            }
            case "indexOf" -> { // Returns the index within this string of the first occurrence of the specified character. -1 if not found.
                if (unknown) {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                }
                if (contentRegex == null) {
                    return Value.valueFactory(-1);
                }
                boolean unknownMatch = false;
                for (RegexItem item : contentRegex) {
                    if (item instanceof RegexChar regexChar) {
                        if (!Character.isWhitespace(regexChar.getContent())) {
                            return Value.valueFactory(false);
                        }
                    } else if (item instanceof RegexChars regexChars) {
                        for (Character c : regexChars.getContent()) {
                            if (c == null || !Character.isWhitespace(c)) {
                                unknownMatch = true;
                            }
                        }
                    }
                }
                if (unknownMatch) {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                } else {
                    return Value.valueFactory(true);
                }
            }
            case "substring" -> {   // (int begin) or (int begin, int end)
                assert paramVars != null && (paramVars.size() == 1 || paramVars.size() == 2);
                if (unknown) {
                    return new StringRegexValue(null, true);
                }
                if (contentRegex == null) {
                    return new StringRegexValue(null, false);
                }
                int begin = (int) ((INumberValue) paramVars.get(0)).getValue();
                int end = contentRegex.size();
                if (paramVars.size() == 2) {
                    end = (int) ((INumberValue) paramVars.get(1)).getValue();
                }
                begin = Math.clamp(begin, 0, contentRegex.size());
                end = Math.clamp(end, begin, contentRegex.size());
                List<RegexItem> sub = new ArrayList<>();
                for (int i = begin; i < end; i++) {
                    sub.add(contentRegex.get(i));
                }
                return new StringRegexValue(sub, false);
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @Override
    public Value accessField(@NotNull String fieldName, @NotNull Type expectedType) {
        throw new UnsupportedOperationException("Access field not supported in StringRegexValue");
    }

    @Override
    public IValue binaryOperation(@NotNull String operator, @NotNull IValue other) {
        if (other instanceof VoidValue) {
            return new StringRegexValue();
        }
        if (operator.equals("+") && other instanceof INumberValue inumbervalue) {
            if (this.unknown || !inumbervalue.getInformation()) {
                this.unknown = true;
                return new StringRegexValue(null, true);
            }
            assert this.contentRegex != null;
            List<RegexItem> newContentRegex = new ArrayList<>(contentRegex);
            appendAtPos(newContentRegex, doubleToRegex(inumbervalue.getValue()), contentRegex.size() - 1);
            for (int i = contentRegex.size() - 1; i >= 0; i--) {
                if (newContentRegex.get(i) instanceof RegexChars chars && chars.canBeEmpty()) {
                    appendAtPos(newContentRegex, doubleToRegex(inumbervalue.getValue()), i);
                } else {
                    break;
                }
            }
            return new StringRegexValue(newContentRegex, false);
        } else if (operator.equals("+") && other instanceof StringRegexValue stringValue) {
            if (this.unknown || stringValue.unknown) {
                this.unknown = true;
                return new StringRegexValue(null, true);
            }
            if (this.contentRegex == null && stringValue.contentRegex == null) {
                return new StringRegexValue(null, false);
            } else if (this.contentRegex == null || stringValue.contentRegex == null) {
                return new StringRegexValue(null, true);
            }
            // put at the end of each possible list end (without empty chars)
            List<RegexItem> newContentRegex = new ArrayList<>(contentRegex);
            appendAtPos(newContentRegex, stringValue.contentRegex, contentRegex.size() - 1);
            for (int i = contentRegex.size() - 1; i >= 0; i--) {
                if (newContentRegex.get(i) instanceof RegexChars chars && chars.canBeEmpty()) {
                    appendAtPos(newContentRegex, stringValue.contentRegex, i);
                } else {
                    break;
                }
            }
            return new StringRegexValue(newContentRegex, false);
        } else if (operator.equals("==") && other instanceof NullValue) {
            if (!unknown) {
                return Value.valueFactory(this.contentRegex == null);
            } else {
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
        }
        return new VoidValue();
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new StringRegexValue(contentRegex == null ? null : new ArrayList<>(contentRegex), unknown);
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        // StringValue doesn't have fields that could create cycles,
        // so we can just return a simple copy
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (other instanceof VoidValue || other instanceof IJavaObject) {
            contentRegex = new ArrayList<>();
            unknown = true;
            return;
        }
        if (other instanceof NullValue) {
            if (contentRegex == null) {
                // keep null
            } else {
                this.unknown = true;
            }
            return;
        }
        assert other instanceof StringRegexValue;
        StringRegexValue otherString = (StringRegexValue) other;
        if (this.contentRegex == null || otherString.contentRegex == null) {
            this.contentRegex = null;
            return;
        }
        if (this.unknown || otherString.unknown) {
            this.unknown = true;
            return;
        }
        int maxLength = Math.max(this.contentRegex.size(), otherString.contentRegex.size());
        int minLength = Math.min(this.contentRegex.size(), otherString.contentRegex.size());
        for (int i = 0; i < minLength; i++) {
            this.contentRegex.get(i).merge(otherString.contentRegex.get(i));
        }
        if (this.contentRegex.size() < otherString.contentRegex.size()) {
            for (int i = minLength; i < maxLength; i++) {
                RegexItem otherRegx = otherString.contentRegex.get(i);
                otherRegx.merge(null);
                this.contentRegex.add(otherRegx);
            }
        } else {
            for (int i = minLength; i < maxLength; i++) {
                this.contentRegex.get(i).merge(null);
            }
        }
    }

    @Override
    public void setToUnknown() {
        contentRegex = new ArrayList<>();
        unknown = true;
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
        this.contentRegex = null;
        unknown = false;
    }

    @NotNull
    private List<RegexItem> doubleToRegex(double value) {
        List<RegexItem> regexList = new ArrayList<>();
        String str = Double.toString(value);
        for (char c : str.toCharArray()) {
            regexList.add(new RegexChar(c));
        }
        return regexList;
    }

    /**
     * @return true if the string value has definite information (i.e., a known value), false otherwise.
     */
    public boolean getInformation() {
        if (unknown) {
            return false;
        }
        if (contentRegex == null) {
            return true; // null is a definite value
        }
        for (RegexItem item : contentRegex) {
            if (item instanceof RegexChars) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return if known, the string value.
     */
    public String getValue() {
        assert getInformation();
        if (contentRegex == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (RegexItem item : contentRegex) {
            if (item instanceof RegexChar regexChar) {
                sb.append(regexChar.getContent());
            }
        }
        return sb.toString();
    }

    /**
     * Should only be used in tests!
     * @return the regex representation of the string content. Null if the string is null.
     */
    @Nullable
    @TestOnly
    public List<RegexItem> getContentRegex() {
        return contentRegex;
    }

}
