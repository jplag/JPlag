package de.jplag.reporting.jsonfactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyWriter implements FileWriter {
    private static final Logger logger = LoggerFactory.getLogger(DummyWriter.class);

    @Override
    public void saveAsJSON(Object fileToSave, String folderPath, String fileName) {
        logger.info("DummyWriter writes object " + fileToSave + " to path " + folderPath + " with name " + fileName + " as JSON.");
    }
}
