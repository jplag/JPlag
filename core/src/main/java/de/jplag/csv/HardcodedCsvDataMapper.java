package de.jplag.csv;

import java.util.Optional;
import java.util.function.Function;

/**
 * Can be used to hardcode mappings to csv. Uses the given function to map values.
 * @param <T> The mapped type.
 */
public class HardcodedCsvDataMapper<T> implements CsvDataMapper<T> {
    private final Function<T, Object[]> mappingFunction;
    private final int columnCount;

    private final String[] titles;

    /**
     * @param columnCount The number of columns.
     * @param mappingFunction The function returning the column values. Must return as many values as specified in
     * columnCount.
     */
    public HardcodedCsvDataMapper(int columnCount, Function<T, Object[]> mappingFunction) {
        this(columnCount, mappingFunction, null);
    }

    /**
     * @param columnCount The number of columns.
     * @param mappingFunction The function returning the column values. Must return as many values as specified in
     * columnCount.
     * @param titles The titles for the csv.
     */
    public HardcodedCsvDataMapper(int columnCount, Function<T, Object[]> mappingFunction, String[] titles) {
        this.mappingFunction = mappingFunction;
        this.columnCount = columnCount;
        this.titles = titles;
    }

    @Override
    public String[] provideData(T value) {
        Object[] values = this.mappingFunction.apply(value);

        if (values.length != this.columnCount) {
            throw new IllegalStateException("You need to return the appropriate number of columns");
        }

        String[] data = new String[this.columnCount];
        for (int i = 0; i < this.columnCount; i++) {
            data[i] = String.valueOf(values[i]);
        }

        return data;
    }

    @Override
    public Optional<String[]> getTitleRow() {
        return Optional.ofNullable(this.titles);
    }
}
