package de.jplag.options;

/**
 * A single language specific option.
 * @param <T> The type of the options value
 */
public interface LanguageOption<T> {
    /**
     * @return The type instance for the option.
     */
    OptionType<T> getType();

    /**
     * @return The name of the option.
     */
    String getName();

    /**
     * @return The name as a unix parameter name. This should be "--" followed by the value of getName
     */
    default String getNameAsUnixParameter() {
        return "--" + this.getName();
    }

    /**
     * @return The value of the option.
     */
    T getValue();

    /**
     * @return The description of the option.
     */
    String getDescription();

    /**
     * Updates the options value.
     * @param value The new value
     */
    void setValue(T value);

    /**
     * @return True, if the option has a value right now. The actual value might still be null.
     */
    boolean hasValue();
}
