package de.jplag.options;

/**
 * A sealed abstract class representing available types for language-specific options. Each concrete subclass represents
 * a specific option type (String, Integer, Boolean).
 * @param <T> The Java type of the option.
 */
public abstract sealed class OptionType<T> {
    /**
     * The String option type implementation.
     */
    static final class StringType extends OptionType<String> {
        /**
         * Singleton instance of StringType.
         */
        public static final StringType INSTANCE = new StringType();

        private StringType() {
            super(String.class);
        }
    }

    /**
     * The Integer option type implementation.
     */
    static final class IntegerType extends OptionType<Integer> {
        /**
         * Singleton instance of IntegerType.
         */
        public static final IntegerType INSTANCE = new IntegerType();

        private IntegerType() {
            super(Integer.class);
        }
    }

    /**
     * The Boolean option type implementation.
     */
    static final class BooleanType extends OptionType<Boolean> {
        /**
         * Singleton instance of BooleanType.
         */
        public static final BooleanType INSTANCE = new BooleanType();

        private BooleanType() {
            super(Boolean.class);
        }
    }

    private final Class<T> javaType;

    private OptionType(Class<T> javaType) {
        this.javaType = javaType;
    }

    /**
     * Returns the String option type instance.
     * @return the singleton StringType instance
     */
    public static StringType string() {
        return StringType.INSTANCE;
    }

    /**
     * Returns the Integer option type instance.
     * @return the singleton IntegerType instance
     */
    public static IntegerType integer() {
        return IntegerType.INSTANCE;
    }

    /**
     * Returns the Boolean option type instance.
     * @return the singleton BooleanType instance
     */
    public static BooleanType bool() {
        return BooleanType.INSTANCE;
    }

    /**
     * Gets the Java Class object representing this option's type.
     * @return the Class object for the option's type
     */
    public Class<T> getJavaType() {
        return javaType;
    }
}