package de.jplag.reporting.reportobject.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This writer is used as a mock for testing purposes only.
 */
public class DummyWriter extends JsonWriter {
    private static final Logger logger = LoggerFactory.getLogger(DummyWriter.class);
    private static final String MESSAGE = "DummyWriter writes object {} to path {} with name {} as JSON.";

    @Override
    public void writeFile(Object fileToSave, String folderPath, String fileName) {
        logger.info(MESSAGE, fileToSave, folderPath, fileName);
    }
}
