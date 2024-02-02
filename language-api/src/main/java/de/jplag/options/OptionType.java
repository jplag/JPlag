package de.jplag.options;

/**
 * The available types for language specific options.
 * @param <T> The java type of the option.
 */
public abstract sealed class OptionType<T> {
    static final class StringType extends OptionType<String> {
        public static final StringType INSTANCE = new StringType();

        private StringType() {
            super(String.class);
        }
    }

    static final class IntegerType extends OptionType<Integer> {
        public static final IntegerType INSTANCE = new IntegerType();

        private IntegerType() {
            super(Integer.class);
        }
    }

    static final class BooleanType extends OptionType<Boolean> {
        public static final BooleanType INSTANCE = new BooleanType();

        private BooleanType() {
            super(Boolean.class);
        }
    }

    public static StringType string() {
        return StringType.INSTANCE;
    }

    public static IntegerType integer() {
        return IntegerType.INSTANCE;
    }

    public static BooleanType bool() {
        return BooleanType.INSTANCE;
    }

    private final Class<T> javaType;

    private OptionType(Class<T> javaType) {
        this.javaType = javaType;
    }

    public Class<T> getJavaType() {
        return javaType;
    }
}
