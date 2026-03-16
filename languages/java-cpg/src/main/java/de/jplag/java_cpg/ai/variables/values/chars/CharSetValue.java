package de.jplag.java_cpg.ai.variables.values.chars;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.BooleanValue;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.numbers.IFloatNumber;
import de.jplag.java_cpg.ai.variables.values.numbers.IIntNumber;
import de.jplag.java_cpg.ai.variables.values.string.IStringValue;

/**
 * Char value representation that can hold a set of possible char values or be unknown.
 * @author ujiqk
 * @version 1.0
 */
public class CharSetValue extends Value implements ICharValue {

    private Set<Character> maybeContained;
    private boolean information;

    /**
     * an unknown char value.
     */
    public CharSetValue() {
        super(new Type(Type.TypeEnum.CHAR));
        this.information = false;
    }

    /**
     * an exactly known char value.
     * @param character the known character
     */
    public CharSetValue(char character) {
        super(new Type(Type.TypeEnum.CHAR));
        this.information = true;
        this.maybeContained = new HashSet<>();
        this.maybeContained.add(character);
    }

    /**
     * a char value that can be one of the given characters.
     * @param characters the possible characters
     */
    public CharSetValue(@NotNull Set<Character> characters) {
        super(new Type(Type.TypeEnum.CHAR));
        this.information = true;
        this.maybeContained = characters;
    }

    /**
     * Copy constructor.
     */
    private CharSetValue(Set<Character> maybeContained, boolean information) {
        super(new Type(Type.TypeEnum.CHAR));
        this.maybeContained = maybeContained;
        this.information = information;
    }

    @Override
    public IValue binaryOperation(@NotNull String operator, @NotNull IValue other) {
        switch (operator) {
            case "!=" -> {
                CharSetValue otherCharValue = (CharSetValue) other;
                if (!this.information || !otherCharValue.information) {
                    return new BooleanValue();
                }
                for (char c1 : this.maybeContained) {     // true if disjoint
                    for (char c2 : otherCharValue.maybeContained) {
                        if (c1 == c2) {
                            return new BooleanValue(false);
                        }
                    }
                }
                return new BooleanValue(true);
            }
            case "==" -> {
                CharSetValue otherCharValue = (CharSetValue) other;
                if (!this.information || !otherCharValue.information) {
                    return new BooleanValue();
                }
                if (this.maybeContained.size() == 1 && otherCharValue.maybeContained.size() == 1) {
                    if (this.maybeContained.iterator().next().equals(otherCharValue.maybeContained.iterator().next())) {
                        return new BooleanValue(true);
                    }
                }
                return new BooleanValue(false);
            }
            case "-" -> {
                CharSetValue otherCharValue = (CharSetValue) other;
                if (this.information && otherCharValue.information) {
                    Set<Character> maybeContainedCopy = Set.copyOf(this.maybeContained);
                    Set<Character> otherMaybeContained = otherCharValue.maybeContained;
                    Set<Character> result = new HashSet<>();
                    for (char c1 : maybeContainedCopy) {
                        for (char c2 : otherMaybeContained) {
                            result.add((char) (c1 - c2));
                        }
                    }
                    return new CharSetValue(result);
                } else {
                    return new CharSetValue();
                }
            }
            case "+" -> {
                if (other instanceof IIntNumber || other instanceof IFloatNumber) {
                    return new CharSetValue();
                } else if (other instanceof IStringValue) {
                    return Value.valueFactory(new Type(Type.TypeEnum.STRING));
                }
                CharSetValue otherCharValue = (CharSetValue) other;
                if (this.information && otherCharValue.information) {
                    Set<Character> maybeContainedCopy = Set.copyOf(this.maybeContained);
                    Set<Character> otherMaybeContained = otherCharValue.maybeContained;
                    Set<Character> result = new HashSet<>();
                    for (char c1 : maybeContainedCopy) {
                        for (char c2 : otherMaybeContained) {
                            result.add((char) (c1 + c2));
                        }
                    }
                    return new CharSetValue(result);
                } else {
                    return new CharSetValue();
                }
            }
            default -> {
                return new VoidValue();
            }
        }
    }

    @Override
    public IValue unaryOperation(@NotNull String operator) {
        switch (operator) {
            default -> {
                return new VoidValue();
            }
        }
    }

    @NotNull
    @Override
    public Value copy() {
        return new CharSetValue(this.maybeContained, this.information);
    }

    @Override
    public void merge(@NotNull IValue other) {
        if (other instanceof VoidValue) {
            setToUnknown();
            return;
        }
        CharSetValue otherCharValue = (CharSetValue) other;
        if (!otherCharValue.information) {
            this.information = false;
        } else if (this.information) {
            this.maybeContained.addAll(otherCharValue.maybeContained);
        }
    }

    @Override
    public void setToUnknown() {
        this.information = false;
    }

    @Override
    public void setInitialValue() {
        this.information = true;
        this.maybeContained = new HashSet<>();
        this.maybeContained.add(DEFAULT_VALUE);
    }

    @Override
    public boolean getInformation() {
        return this.information && this.maybeContained.size() == 1;
    }

    @Override
    public double getValue() {
        assert this.information;
        return this.maybeContained.iterator().next();
    }

}
