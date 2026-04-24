package de.jplag.endtoend.model;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * The available formats for data sets.
 */
@SuppressWarnings("unused") // The formats only referred to from the data set configuration
public enum DataSetFormat {
    /**
     * The progpedia format.
     */
    PROGPEDIA {
        @Override
        public Set<File> getSourceDirectories(DataSet dataSet) throws IOException {
            return Collections.singleton(new File(dataSet.actualSourceDirectory(), "ACCEPTED"));
        }

        @Override
        public File getBaseCodeDirectory(DataSet dataSet, String directoryName) throws IOException {
            return new File(dataSet.actualSourceDirectory(), directoryName);
        }
    },
    /**
     * Plain format where the submissions are flat within the source directory.
     */
    PLAIN {
        @Override
        public Set<File> getSourceDirectories(DataSet dataSet) throws IOException {
            return Collections.singleton(dataSet.actualSourceDirectory());
        }

        @Override
        public File getBaseCodeDirectory(DataSet dataSet, String directoryName) {
            throw new IllegalStateException("Plain formatted data sets cannot include base code.");
        }
    };

    /**
     * Resolved the source directories for jplag.
     * @param dataSet The data set
     * @return The source directories
     * @throws IOException if retrieving the sources fails.
     */
    public abstract Set<File> getSourceDirectories(DataSet dataSet) throws IOException;

    /**
     * Resolves the base code directory.
     * @param dataSet The data set
     * @param directoryName The name of the base code directory
     * @return The base code directory
     * @throws IOException if retrieving the base code fails.
     */
    public abstract File getBaseCodeDirectory(DataSet dataSet, String directoryName) throws IOException;
}
