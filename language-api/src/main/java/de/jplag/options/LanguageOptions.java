package de.jplag.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for a languages options. Should be implemented per language.
 */
public abstract class LanguageOptions {
    private final List<LanguageOption<?>> options;

    /**
     * New instance
     */
    public LanguageOptions() {
        this.options = new ArrayList<>();
    }

    /**
     * Creates a new option with default value.
     * @param type The type
     * @param name The name
     * @param description the description
     * @param defaultValue The default value
     * @param <T> The java type
     * @return The new option
     */
    protected <T> LanguageOption<T> createDefaultOption(OptionType<T> type, String name, String description, T defaultValue) {
        LanguageOption<T> option = new LanguageOptionImpl<>(type, name, description, defaultValue);
        this.options.add(option);
        return option;
    }

    /**
     * Creates a new option with default value and empty description.
     * @param type The type
     * @param name The name
     * @param defaultValue The default value
     * @param <T> The java type
     * @return The new option
     */
    protected <T> LanguageOption<T> createDefaultOption(OptionType<T> type, String name, T defaultValue) {
        return createDefaultOption(type, name, "", defaultValue);
    }

    /**
     * Creates a new option
     * @param type The type
     * @param name The name
     * @param description The description
     * @param <T> The java type
     * @return The new option
     */
    protected <T> LanguageOption<T> createOption(OptionType<T> type, String name, String description) {
        LanguageOption<T> option = new LanguageOptionImpl<>(type, name, description);
        this.options.add(option);
        return option;
    }

    /**
     * Creates a new option with empty description
     * @param type The type
     * @param name The name
     * @param <T> The java type
     * @return The new option
     */
    protected <T> LanguageOption<T> createOption(OptionType<T> type, String name) {
        return createOption(type, name, "");
    }

    /**
     * @return The list of all options
     */
    public List<LanguageOption<?>> getOptions() {
        return Collections.unmodifiableList(this.options);
    }
}
