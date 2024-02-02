package de.jplag.reporting.reportobject.writer;

/**
 * Responsible for writing a specific file type to the disk.
 * @param <T> Object that the FileWriter writes.
 */
public interface FileWriter<T> {

    /**
     * Saves the provided object to the provided path under the provided name
     * @param fileContent The object to save
     * @param folderPath The path to save the object to
     * @param fileName The name to save the object under
     */
    void writeFile(T fileContent, String folderPath, String fileName);
}
