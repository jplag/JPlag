package de.jplag.reporting.reportobject.writer;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Writes an object with {@link com.fasterxml.jackson.annotation.JsonProperty}s to the disk.
 */
public class JsonWriter implements FileWriter<Object> {
    private static final Logger logger = LoggerFactory.getLogger(JsonWriter.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void writeFile(Object fileToSave, String folderPath, String fileName) {
        try {
            objectMapper.writeValue(Path.of(folderPath, fileName).toFile(), fileToSave);
        } catch (IOException e) {
            logger.error("Failed to save json file " + fileName + ": " + e.getMessage(), e);
        }
    }

}
