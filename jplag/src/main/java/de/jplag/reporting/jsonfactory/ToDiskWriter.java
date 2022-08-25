package de.jplag.reporting.jsonfactory;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ToDiskWriter implements FileWriter {
    private static final Logger logger = LoggerFactory.getLogger(ToDiskWriter.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Saves the provided object to the provided path under the provided name
     * @param fileToSave The object to save
     * @param folderPath The path to save the object to
     * @param fileName The name to save the object under
     */
    @Override
    public void saveAsJSON(Object fileToSave, String folderPath, String fileName) {
        try {
            objectMapper.writeValue(Path.of(folderPath, fileName).toFile(), fileToSave);
        } catch (IOException e) {
            logger.error("Failed to save json file " + fileName + ": " + e.getMessage(), e);
        }
    }
}
