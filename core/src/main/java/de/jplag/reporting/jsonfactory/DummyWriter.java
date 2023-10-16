package de.jplag.reporting.jsonfactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.reporting.reportobject.writer.FileWriter;

public class DummyWriter implements FileWriter<Object> {
    private static final Logger logger = LoggerFactory.getLogger(DummyWriter.class);

    @Override
    public void writeFile(Object fileToSave, String folderPath, String fileName) {
        logger.info("DummyWriter writes object " + fileToSave + " to path " + folderPath + " with name " + fileName + " as JSON.");
    }
}
