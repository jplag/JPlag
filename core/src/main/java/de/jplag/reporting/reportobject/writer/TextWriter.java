package de.jplag.reporting.reportobject.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes plain text to a file.
 */
public class TextWriter implements FileWriter<String> {

    private static final Logger logger = LoggerFactory.getLogger(TextWriter.class);
    private static final String WRITE_ERROR = "Failed to write text file {}";

    @Override
    public void writeFile(String fileContent, String folderPath, String fileName) {
        String path = Path.of(folderPath, fileName).toString();
        try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(path))) {
            writer.write(fileContent);
        } catch (IOException e) {
            logger.error(WRITE_ERROR, e, path);
        }
    }
}
