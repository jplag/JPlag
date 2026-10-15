package de.jplag.regressiontest.model;

import java.io.IOException;
import java.nio.file.Path;
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
        public Set<Path> getSourceDirectories(DataSet dataSet) throws IOException {
            return Collections.singleton(dataSet.actualSourceDirectory().resolve("ACCEPTED"));
        }

        @Override
        public Path getBaseCodeDirectory(DataSet dataSet, String directoryName) throws IOException {
            return dataSet.actualSourceDirectory().resolve(directoryName);
        }
    },
    /**
     * Plain format where the submissions are flat within the source directory.
     */
    PLAIN {
        @Override
        public Set<Path> getSourceDirectories(DataSet dataSet) throws IOException {
            return Collections.singleton(dataSet.actualSourceDirectory());
        }

        @Override
        public Path getBaseCodeDirectory(DataSet dataSet, String directoryName) {
            throw new IllegalStateException("Plain formatted data sets cannot include base code.");
        }
    };

    /**
     * Resolved the source directories for jplag.
     * @param dataSet The data set
     * @return The source directories
     * @throws IOException if retrieving the sources fails.
     */
    public abstract Set<Path> getSourceDirectories(DataSet dataSet) throws IOException;

    /**
     * Resolves the base code directory.
     * @param dataSet The data set
     * @param directoryName The name of the base code directory
     * @return The base code directory
     * @throws IOException if retrieving the base code fails.
     */
    public abstract Path getBaseCodeDirectory(DataSet dataSet, String directoryName) throws IOException;
}
