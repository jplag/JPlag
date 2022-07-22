package de.jplag.reporting.jsonfactory;

import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonWriter {
    private static final Logger logger = LoggerFactory.getLogger(JsonWriter.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void saveFile(Object fileToSave, String folderPath, String fileName) {
        try {
            objectMapper.writeValue(Path.of(folderPath, fileName).toFile(), fileToSave);
        } catch (IOException e) {
            logger.error("Failed to save json file " + fileName + ": " + e.getMessage(), e);
        }
    }

}
