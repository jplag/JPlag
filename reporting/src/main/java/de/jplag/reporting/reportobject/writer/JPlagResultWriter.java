package de.jplag.reporting.reportobject.writer;

import de.jplag.util.RelativePath;

import java.io.IOException;
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
    void addJsonEntry(Object jsonContent, RelativePath path);

    /**
     * Writes data from a file.
     * @param path The path to write to
     * @param original The original file
     * @throws IOException If the file handling fails
     */
    void addFileContentEntry(RelativePath path, Path original) throws IOException;

    /**
     * Writes data from a string.
     * @param entry The string to write
     * @param path The path to write to
     */
    void writeStringEntry(String entry, RelativePath path);

    /**
     * Closes the writer.
     */
    void close();
}
