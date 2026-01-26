package de.jplag.csv;

import java.util.Optional;

/**
 * Provides mappings for csv rows and optionally names for the columns. Needs to always return the same number of
 * columns.
 * @param <T> The type of data that is mapped.
 */
public interface CsvDataMapper<T> {
    /**
     * Provides the cell values for one row.
     * @param value The original object.
     * @return The cell values.
     */
    String[] provideData(T value);

    /**
     * @return The names of the columns if present.
     */
    Optional<String[]> getTitleRow();
}
