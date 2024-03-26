package de.jplag.options;

/**
 * A single language specific option.
 * @param <T> The type of the options value
 */
public interface LanguageOption<T> {
    /**
     * <p>
     * getType.
     * </p>
     * @return The type instance for the option.
     */
    OptionType<T> getType();

    /**
     * <p>
     * getName.
     * </p>
     * @return The name of the option.
     */
    String getName();

    /**
     * <p>
     * getNameAsUnixParameter.
     * </p>
     * @return The name as a unix parameter name. This should be "--" followed by the value of getName
     */
    default String getNameAsUnixParameter() {
        return "--" + this.getName();
    }

    /**
     * <p>
     * getValue.
     * </p>
     * @return The value of the option.
     */
    T getValue();

    /**
     * <p>
     * getDescription.
     * </p>
     * @return The description of the option.
     */
    String getDescription();

    /**
     * Updates the options value.
     * @param value The new value
     */
    void setValue(T value);

    /**
     * <p>
     * hasValue.
     * </p>
     * @return True, if the option has a value right now. The actual value might still be null.
     */
    boolean hasValue();
}
