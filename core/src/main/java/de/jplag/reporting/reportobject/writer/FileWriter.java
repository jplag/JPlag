package de.jplag.reporting.reportobject.writer;

/**
 * @param <T> Object that the FileWriter writes.
 */
public interface FileWriter<T> {
    void writeFile(T fileContent, String folderPath, String fileName);
}
