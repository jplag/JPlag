package de.jplag.csv;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Pair;

/**
 * Maps data automatically based on the exposed fields and methods.
 * @param <T> The mapped type. Mark included methods and fields with @{@link CsvValue}
 */
public class ReflectiveCsvDataMapper<T> implements CsvDataMapper<T> {
    private final List<Pair<Integer, GetterFunction<T>>> values;
    private final String[] titles;

    /**
     * @param type The mapped type.
     */
    public ReflectiveCsvDataMapper(Class<T> type) {
        this(type, null);
    }

    /**
     * @param type is the mapped type.
     * @param titles are the titles for the csv. Must be as many as @{@link CsvValue} annotation in the given type.
     * @throws IllegalArgumentException if the csv data is invalid.
     * @throws IllegalStateException if the data can not be mapped.
     */
    public ReflectiveCsvDataMapper(Class<T> type, String[] titles) {
        this.values = new ArrayList<>();

        for (Field field : type.getFields()) {
            if (field.getAnnotation(CsvValue.class) != null) {
                this.values.add(new Pair<>(field.getAnnotation(CsvValue.class).value(), field::get));
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

                this.values.add(new Pair<>(method.getAnnotation(CsvValue.class).value(), method::invoke));
            }
        }

        this.values.sort(Comparator.comparing(Pair::getKey));

        if (titles != null && this.values.size() != titles.length) {
            throw new IllegalArgumentException("Csv data must have the same number of tiles and values per row.");
        }

        this.titles = titles;
    }

    @Override
    public String[] provideData(T value) {
        String[] data = new String[this.values.size()];

        for (int i = 0; i < data.length; i++) {
            try {
                data[i] = String.valueOf(this.values.get(i).getValue().get(value));
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

    private interface GetterFunction<T> {
        Object get(T instance) throws IllegalAccessException, InvocationTargetException;
    }
}
