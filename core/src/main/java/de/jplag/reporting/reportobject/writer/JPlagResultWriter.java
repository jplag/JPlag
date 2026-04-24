package de.jplag.reporting.reportobject.writer;

import java.io.File;
import java.nio.file.Path;

/**
 * Writer for JPlag result data. The way paths are resolved depends on the implementation.
 */
public interface JPlagResultWriter {
    /**
     * Writes data as json.
     * @param jsonContent The json content
     * @param path The path to write to
     */
    void addJsonEntry(Object jsonContent, Path path);

    /**
     * Writes data from a file.
     * @param path The path to write to
     * @param original The original file
     */
    void addFileContentEntry(Path path, File original);

    /**
     * Writes data from a string.
     * @param entry The string to write
     * @param path The path to write to
     */
    void writeStringEntry(String entry, Path path);

    /**
     * Closes the writer.
     */
    void close();
}
