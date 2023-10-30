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
    private static final String WRITE_ERROR = "Failed to write JSON file {}";

    @Override
    public void writeFile(Object fileToSave, String folderPath, String fileName) {
        Path path = Path.of(folderPath, fileName);
        try {
            objectMapper.writeValue(path.toFile(), fileToSave);
        } catch (IOException e) {
            logger.error(WRITE_ERROR, e, path);
        }
    }

}
