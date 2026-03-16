package de.jplag.java_cpg.ai.variables.values.string;

import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.IJavaObject;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;

/**
 * String representation using character inclusion sets. Uses BitSet for efficient character set representation and
 * copying.
 * @author ujiqk
 * @version 1.0
 */
public class StringCharInclValue extends JavaObject implements IStringValue {

    // String=null <--> certainContained=null
    /**
     * Characters that are definitely contained in the string. Null if string is null.
     */
    @Nullable
    private BitSet certainContained;
    /**
     * Characters that may be contained in the string.
     */
    private BitSet maybeContained;

    /**
     * A string value with no information.
     */
    public StringCharInclValue() {
        super(new Type(Type.TypeEnum.STRING));
        certainContained = new BitSet();
        maybeContained = allCharactersBitSet();
    }

    /**
     * Constructor for StringCharInclValue with exact information.
     * @param value the string value.
     */
    public StringCharInclValue(@Nullable String value) {
        super(new Type(Type.TypeEnum.STRING));
        maybeContained = new BitSet();
        if (value == null) {
            certainContained = null;
            return;
        }
        certainContained = new BitSet();
        for (char c : value.toCharArray()) {
            certainContained.set(c);
        }
    }

    /**
     * Constructor for StringCharInclValue with possible values.
     * @param possibleValues the set of possible string values.
     */
    public StringCharInclValue(@NotNull Set<String> possibleValues) {
        super(new Type(Type.TypeEnum.STRING));
        certainContained = new BitSet();
        maybeContained = new BitSet();
        for (String value : possibleValues) {
            for (char c : value.toCharArray()) {
                maybeContained.set(c);
            }
        }
    }

    private StringCharInclValue(@Nullable BitSet certainContained, BitSet maybeContained) {
        super(new Type(Type.TypeEnum.STRING));
        this.certainContained = certainContained;
        this.maybeContained = maybeContained;
    }

    @Override
    public IValue callMethod(@NotNull String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "length" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "parseInt" -> {
                assert paramVars.size() == 1;
                assert paramVars.getFirst() instanceof IStringValue;
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "parseDouble" -> {
                assert paramVars.size() == 1;
                assert paramVars.getFirst() instanceof IStringValue;
                return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
            }
            case "startsWith" -> {
                assert paramVars.size() == 1;
                assert paramVars.getFirst() instanceof IStringValue;
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            case "equals" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, new StringCharInclValue());
                }
                StringCharInclValue other = (StringCharInclValue) paramVars.getFirst();
                if (!Objects.equals(this.certainContained, other.certainContained) && this.maybeContained.isEmpty()
                        && other.maybeContained.isEmpty()) {
                    return Value.valueFactory(false);
                } else {
                    return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
                }
            }
            case "toUpperCase" -> {
                BitSet newCertain = new BitSet();
                if (this.certainContained != null) {
                    for (int c = certainContained.nextSetBit(0); c >= 0; c = certainContained.nextSetBit(c + 1)) {
                        newCertain.set(Character.toUpperCase((char) c));
                    }
                }
                BitSet newMaybe = new BitSet();
                for (int c = maybeContained.nextSetBit(0); c >= 0; c = maybeContained.nextSetBit(c + 1)) {
                    newMaybe.set(Character.toUpperCase((char) c));
                }
                return new StringCharInclValue(newCertain, newMaybe);
            }
            case "charAt" -> {
                assert paramVars.size() == 1;
                assert paramVars.getFirst() instanceof INumberValue : "charAt parameter must be a number value but was "
                        + paramVars.getFirst().getType();
                return Value.valueFactory(new Type(Type.TypeEnum.CHAR));
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @Override
    public Value accessField(@NotNull String fieldName, @NotNull Type expectedType) {
        throw new UnsupportedOperationException("Access field not supported in StringValue");
    }

    @Override
    public IValue binaryOperation(@NotNull String operator, @NotNull IValue other) {
        if (other instanceof VoidValue) {
            return new StringCharInclValue();
        }
        if (operator.equals("+") && other instanceof INumberValue inumbervalue) {
            if (inumbervalue.getInformation()) {
                BitSet newCertain = certainContained == null ? null : (BitSet) certainContained.clone();
                assert newCertain != null;
                newCertain.or(doubleToCharBitSet(inumbervalue.getValue()));
                return new StringCharInclValue(newCertain, (BitSet) maybeContained.clone());
            } else {
                return new StringCharInclValue();
            }
        } else if (operator.equals("+") && other instanceof StringCharInclValue stringValue) {
            assert !(this.certainContained == null || stringValue.certainContained == null);
            BitSet newCertain = (BitSet) this.certainContained.clone();
            newCertain.or(stringValue.certainContained);
            BitSet newMaybe = (BitSet) this.maybeContained.clone();
            newMaybe.or(stringValue.maybeContained);
            return new StringCharInclValue(newCertain, newMaybe);
        }
        return new VoidValue();
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new StringCharInclValue(certainContained == null ? null : (BitSet) certainContained.clone(), (BitSet) maybeContained.clone());
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other, Set<JavaObject> visited) {
        merge(other);
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (other instanceof VoidValue) {
            this.certainContained = new BitSet();
            this.maybeContained = allCharactersBitSet();
            return;
        }
        assert other instanceof StringCharInclValue : "Cannot merge " + getType() + " with " + other.getType();
        StringCharInclValue otherString = (StringCharInclValue) other;
        if (this.certainContained == null || otherString.certainContained == null) {
            this.certainContained = null;
        } else {
            // Characters that were certain in this but not in both -> move to maybe
            BitSet removed = (BitSet) this.certainContained.clone();
            this.certainContained.and(otherString.certainContained); // Keep only common certain
            removed.andNot(this.certainContained); // Characters removed from certain
            this.maybeContained.or(removed);
            // Characters certain in other but not in this -> add to maybe
            BitSet otherCertainNotInThis = (BitSet) otherString.certainContained.clone();
            otherCertainNotInThis.andNot(this.certainContained);
            this.maybeContained.or(otherCertainNotInThis);
            this.maybeContained.or(otherString.maybeContained);
        }
    }

    @Override
    public void setToUnknown() {
        this.certainContained = new BitSet();
        this.maybeContained = allCharactersBitSet();
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
        this.certainContained = null;
        this.maybeContained = new BitSet();
    }

    @NotNull
    private BitSet allCharactersBitSet() {
        BitSet allChars = new BitSet(Character.MAX_VALUE + 1);
        allChars.set(0, Character.MAX_VALUE + 1);
        return allChars;
    }

    @NotNull
    private BitSet doubleToCharBitSet(double value) {
        BitSet charBitSet = new BitSet();
        String str = Double.toString(value);
        for (char c : str.toCharArray()) {
            charBitSet.set(c);
        }
        return charBitSet;
    }

    /**
     * @return true if the string value has definite information (i.e., a known value), false otherwise.
     */
    public boolean getInformation() {
        // always false since order is never known
        return false;
    }

    /**
     * @return if known, the string value.
     */
    public String getValue() {
        assert getInformation();
        return null;
    }

    /**
     * Only for testing purposes.
     * @return set of certainly contained characters.
     */
    @Nullable
    @TestOnly
    public Set<Character> getCertainContained() {
        if (this.certainContained == null) {
            return null;
        }
        return bitSetToCharSet(this.certainContained);
    }

    /**
     * Only for testing purposes.
     * @return set of maybe contained characters.
     */
    @TestOnly
    public Set<Character> getMaybeContained() {
        return bitSetToCharSet(this.maybeContained);
    }

    @NotNull
    @TestOnly
    private Set<Character> bitSetToCharSet(@NotNull BitSet bitSet) {
        Set<Character> charSet = new HashSet<>();
        for (int c = bitSet.nextSetBit(0); c >= 0; c = bitSet.nextSetBit(c + 1)) {
            charSet.add((char) c);
        }
        return charSet;
    }

    @Override
    public boolean isNull() {
        return certainContained == null;
    }

}
