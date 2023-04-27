package de.jplag.options;

/**
 * Dummy implementation for {@link LanguageOptions}
 */
public class EmptyOptions extends LanguageOptions {
    /**
     * Static instance, since multiple instances don't make sense.
     */
    public static final EmptyOptions instance = new EmptyOptions();

    private EmptyOptions() {

    }
}
