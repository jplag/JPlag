package de.jplag.reporting.reportobject.writer;

import java.io.File;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy writer, that does nothing.
 */
public class DummyResultWriter implements JPlagResultWriter {
    private static final Logger logger = LoggerFactory.getLogger(DummyResultWriter.class);
    private static final String MESSAGE_JSON = "DummyWriter writes object {} to path {} as JSON.";
    private static final String MESSAGE_FILE = "DummyWriter writes file {} to path {}.";
    private static final String MESSAGE_STRING = "DummyWriter writes String ({}) to path {}.";
    private static final String MESSAGE_CLOSE = "DummyWriter closed.";

    @Override
    public void addJsonEntry(Object jsonContent, Path path) {
        logger.info(MESSAGE_JSON, jsonContent, path);
    }

    @Override
    public void addFileContentEntry(Path path, File original) {
        logger.info(MESSAGE_FILE, original.getAbsolutePath(), path);
    }

    @Override
    public void writeStringEntry(String entry, Path path) {
        logger.info(MESSAGE_STRING, entry, path);
    }

    @Override
    public void close() {
        logger.info(MESSAGE_CLOSE);
    }
}
