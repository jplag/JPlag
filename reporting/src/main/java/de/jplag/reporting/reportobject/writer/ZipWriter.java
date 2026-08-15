package de.jplag.reporting.reportobject.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.jplag.util.FileUtils;
import de.jplag.util.RelativePath;
import javax.print.attribute.standard.OutputBin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.FilePathUtil;
import de.jplag.reporting.serialization.JacksonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Writes JPlag result data as a zip.
 */
public class ZipWriter implements JPlagResultWriter {
    private static final Logger logger = LoggerFactory.getLogger(ZipWriter.class);
    private static final ObjectMapper objectMapper = JacksonUtils.createNewObjectMapper();

    private static final String WRITE_JSON_ERROR = "Failed to write JSON entry %s";
    private static final String COPY_FILE_ERROR = "Failed to copy file (%s) to entry (%s)";
    private static final String WRITE_STRING_ERROR = "Failed to write string entry %s";
    private static final String CLOSE_FILE_ERROR = "Failed to close zip file properly";

    private Path root;

    /**
     * The zip file to write to.
     *
     * @param zipFile The file
     * @throws IOException If the file cannot be opened for writing
     */
    public ZipWriter(Path zipFile) throws IOException {
        Files.createDirectories(zipFile.getParent());
        FileSystem fileSystem = FileSystems.newFileSystem(zipFile);
        root = fileSystem.getRootDirectories().iterator().next();
    }

    @Override
    public void addJsonEntry(Object jsonContent, RelativePath path) {
        try {
            FileUtils.write(path.resolveAgainst(root), objectMapper.writeValueAsString(jsonContent));
        } catch (IOException e) {
            logger.error(String.format(WRITE_JSON_ERROR, path), e);
        }
    }

    @Override
    public void addFileContentEntry(RelativePath path, Path original) {
        try {
            Files.copy(original, path.resolveAgainst(root));
        } catch (IOException e) {
            logger.error(String.format(COPY_FILE_ERROR, original, path), e);
        }
    }

    @Override
    public void writeStringEntry(String entry, RelativePath path) {
        try {
            FileUtils.write(path.resolveAgainst(root), entry);
        } catch (IOException e) {
            logger.error(String.format(WRITE_STRING_ERROR, path), e);
        }
    }

    @Override
    public void close() {
        try {
            root.getFileSystem().close();
        } catch (IOException e) {
            logger.error(CLOSE_FILE_ERROR, e);
        }
    }
}
