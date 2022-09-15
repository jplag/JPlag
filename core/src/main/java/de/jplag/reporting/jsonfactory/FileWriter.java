package de.jplag.reporting.jsonfactory;

public interface FileWriter {
    void saveAsJSON(Object fileToSave, String folderPath, String fileName);
}
