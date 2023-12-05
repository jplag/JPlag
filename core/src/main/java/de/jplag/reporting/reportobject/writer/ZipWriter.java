package de.jplag.reporting.reportobject.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Writes JPlag result data as a zip
 */
public class ZipWriter implements JPlagResultWriter {
    private static final Logger logger = LoggerFactory.getLogger(ZipWriter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String WRITE_JSON_ERROR = "Failed to write JSON entry %s";
    private static final String COPY_FILE_ERROR = "Failed to copy file (%s) to entry (%s)";
    private static final String WRITE_STRING_ERROR = "Failed to write string entry %s";
    private static final String CLOSE_FILE_ERROR = "Failed to close zip file properly";

    private final ZipOutputStream file;

    /**
     * The zip file to write to
     * @param zipFile The file
     * @throws FileNotFoundException If the file cannot be opened for writing
     */
    public ZipWriter(File zipFile) throws FileNotFoundException {
        zipFile.getAbsoluteFile().getParentFile().mkdirs();
        this.file = new ZipOutputStream(new FileOutputStream(zipFile));
    }

    @Override
    public void addJsonEntry(Object jsonContent, String path) {
        try {
            this.file.putNextEntry(new ZipEntry(path));
            this.file.write(objectMapper.writeValueAsBytes(jsonContent));
            this.file.closeEntry();
        } catch (IOException e) {
            logger.error(String.format(WRITE_JSON_ERROR, path), e);
        }
    }

    @Override
    public void addFileContentEntry(String path, File original) {
        try (FileInputStream inputStream = new FileInputStream(original)) {
            this.file.putNextEntry(new ZipEntry(path));
            inputStream.transferTo(this.file);
        } catch (IOException e) {
            logger.error(String.format(COPY_FILE_ERROR, original.getAbsolutePath(), path), e);
        }
    }

    @Override
    public void writeStringEntry(String entry, String path) {
        try {
            this.file.putNextEntry(new ZipEntry(path));
            this.file.write(entry.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(String.format(WRITE_STRING_ERROR, path), e);
        }
    }

    public void close() {
        try {
            this.file.close();
        } catch (IOException e) {
            logger.error(CLOSE_FILE_ERROR, e);
        }
    }
}
