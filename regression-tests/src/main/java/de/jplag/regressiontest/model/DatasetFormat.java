package de.jplag.regressiontest.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

/**
 * The available formats for datasets.
 */
@SuppressWarnings("unused") // The formats only referred to from the dataset configuration
public enum DatasetFormat {
    /**
     * The progpedia format.
     */
    PROGPEDIA {
        @Override
        public Set<Path> getSourceDirectories(Dataset dataset) throws IOException {
            return Collections.singleton(dataset.actualSourceDirectory().resolve("ACCEPTED"));
        }

        @Override
        public Path getBaseCodeDirectory(Dataset dataset, String directoryName) throws IOException {
            return dataset.actualSourceDirectory().resolve(directoryName);
        }
    },
    /**
     * Plain format where the submissions are flat within the source directory.
     */
    PLAIN {
        @Override
        public Set<Path> getSourceDirectories(Dataset dataset) throws IOException {
            return Collections.singleton(dataset.actualSourceDirectory());
        }

        @Override
        public Path getBaseCodeDirectory(Dataset dataset, String directoryName) {
            throw new IllegalStateException("Plain formatted data sets cannot include base code.");
        }
    };

    /**
     * Resolved the source directories for jplag.
     * @param dataset The data set
     * @return The source directories
     * @throws IOException if retrieving the sources fails.
     */
    public abstract Set<Path> getSourceDirectories(Dataset dataset) throws IOException;

    /**
     * Resolves the base code directory.
     * @param dataset The data set
     * @param directoryName The name of the base code directory
     * @return The base code directory
     * @throws IOException if retrieving the base code fails.
     */
    public abstract Path getBaseCodeDirectory(Dataset dataset, String directoryName) throws IOException;
}
