package de.jplag.reporting.csv;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Mapped data automatically based on the exposed fields and methods.
 * @param <T> The mapped type. Mark included methods and fields with @{@link CsvValue}
 */
public class ReflectiveCsvDataMapper<T> implements CsvDataMapper<T> {
    private final List<ValueGetter> values;
    private String[] titles;

    /**
     * @param type The mapped type.
     */
    public ReflectiveCsvDataMapper(Class<T> type) {
        this.values = new ArrayList<>();

        for (Field field : type.getFields()) {
            if (field.getAnnotation(CsvValue.class) != null) {
                this.values.add(new VariableValueGetter(field, field.getAnnotation(CsvValue.class).value()));
            }
        }

        for (Method method : type.getMethods()) {
            if (method.getAnnotation(CsvValue.class) != null) {
                if (method.getParameters().length != 0) {
                    throw new IllegalStateException(
                            String.format("Method %s in %s must not have parameters to be a csv value", method.getName(), type.getName()));
                }
                if (method.getReturnType().equals(Void.class)) {
                    throw new IllegalStateException(
                            String.format("Method %s in %s must not return void to be a csv value", method.getName(), type.getName()));
                }

                this.values.add(new MethodValueGetter(method, method.getAnnotation(CsvValue.class).value()));
            }
        }

        this.values.sort(Comparator.comparing(it -> it.index));
        this.titles = null;
    }

    /**
     * @param type The mapped type
     * @param titles The titles for the csv. Must be as many as @{@link CsvValue} annotation in the given type.
     */
    public ReflectiveCsvDataMapper(Class<T> type, String[] titles) {
        this(type);

        if (this.values.size() != titles.length) {
            throw new IllegalArgumentException("Csv data must have the same number of tiles and values per row.");
        }

        this.titles = titles;
    }

    @Override
    public String[] provideData(T value) {
        String[] data = new String[this.values.size()];

        for (int i = 0; i < data.length; i++) {
            try {
                data[i] = this.values.get(i).get(value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        return data;
    }

    @Override
    public Optional<String[]> getTitleRow() {
        return Optional.ofNullable(this.titles);
    }

    private abstract class ValueGetter {
        private final int index;

        public ValueGetter(int index) {
            this.index = index;
        }

        abstract String get(T instance) throws IllegalAccessException, InvocationTargetException;
    }

    private class VariableValueGetter extends ValueGetter {
        private final Field field;

        public VariableValueGetter(Field field, int index) {
            super(index);
            this.field = field;
        }

        @Override
        String get(T instance) throws IllegalAccessException {
            return String.valueOf(field.get(instance));
        }
    }

    private class MethodValueGetter extends ValueGetter {
        private final Method method;

        public MethodValueGetter(Method method, int index) {
            super(index);
            this.method = method;
        }

        @Override
        String get(T instance) throws IllegalAccessException, InvocationTargetException {
            return String.valueOf(method.invoke(instance));
        }
    }
}
