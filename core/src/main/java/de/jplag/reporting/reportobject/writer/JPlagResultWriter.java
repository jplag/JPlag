package de.jplag.reporting.reportobject.writer;

import java.io.File;

/**
 * Writer for JPlag result data. The way paths are resolved depends on the implementation
 */
public interface JPlagResultWriter {
    /**
     * Writes data as json
     * @param jsonContent The json content
     * @param path The path to write to
     */
    void addJsonEntry(Object jsonContent, String path);

    /**
     * Writes data from a file
     * @param path The path to write to
     * @param original The original file
     */
    void addFileContentEntry(String path, File original);

    /**
     * Writes data from a string
     * @param entry The string to write
     * @param path The path to write to
     */
    void writeStringEntry(String entry, String path);

    /**
     * Closes the writer
     */
    void close();
}
