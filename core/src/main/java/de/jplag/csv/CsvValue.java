package de.jplag.csv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used with {@link ReflectiveCsvDataMapper} to identify fields and methods, that should be used for the csv.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CsvValue {
    /**
     * The index of the csv field. Has to be used as the compiler sometimes changes the order of fields/methods.
     * @return the index.
     */
    int value();
}
