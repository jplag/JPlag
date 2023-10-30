package de.jplag.reporting.reportobject.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyWriter implements FileWriter<Object> {
    private static final Logger logger = LoggerFactory.getLogger(DummyWriter.class);

    @Override
    public void writeFile(Object fileToSave, String folderPath, String fileName) {
        logger.info("DummyWriter writes object " + fileToSave + " to path " + folderPath + " with name " + fileName + " as JSON.");
    }
}
