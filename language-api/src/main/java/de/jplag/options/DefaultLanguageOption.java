package de.jplag.options;

/**
 * Default implementation for {@link de.jplag.options.LanguageOption}
 * @param <T> The type of the option
 * @author robin
 * @version $Id: $Id
 */
public class DefaultLanguageOption<T> implements LanguageOption<T> {
    private final OptionType<T> type;
    private final String name;
    private final String description;

    private T value;
    private boolean hasValue;

    DefaultLanguageOption(OptionType<T> type, String name, String description, T defaultValue) {
        this.type = type;
        this.name = name;
        this.value = defaultValue;
        this.description = description;
        this.hasValue = true;
    }

    DefaultLanguageOption(OptionType<T> type, String description, String name) {
        this(type, name, description, null);
        this.hasValue = false;
    }

    /** {@inheritDoc} */
    @Override
    public OptionType<T> getType() {
        return this.type;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public T getValue() {
        return this.value;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return this.description;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(T value) {
        this.value = value;
        this.hasValue = true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasValue() {
        return this.hasValue;
    }
}
