package de.jplag.options;

/**
 * Default implementation for {@link LanguageOption}.
 * @param <T> The type of the option
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

    DefaultLanguageOption(OptionType<T> type, String name, String description) {
        this(type, name, description, null);
        this.hasValue = false;
    }

    @Override
    public OptionType<T> getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
        this.hasValue = true;
    }

    @Override
    public boolean hasValue() {
        return this.hasValue;
    }
}
