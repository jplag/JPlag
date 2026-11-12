package de.jplag.reporting.reportobject.writer;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.reporting.serialization.JacksonUtils;
import de.jplag.util.FileUtils;
import de.jplag.util.RelativePath;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Writes JPlag result data as a zip.
 */
public class ZipWriter implements JPlagResultWriter {
    private static final Logger logger = LoggerFactory.getLogger(ZipWriter.class);
    private static final ObjectMapper objectMapper = JacksonUtils.createNewObjectMapper();

    private static final String WRITE_JSON_ERROR = "Failed to write JSON entry {}";
    private static final String COPY_FILE_ERROR = "Failed to copy file ({}) to entry ({})";
    private static final String WRITE_STRING_ERROR = "Failed to write string entry {}";
    private static final String CLOSE_FILE_ERROR = "Failed to close zip file properly";

    private final Path root;
    private final FileSystem fileSystem;

    /**
     * The zip file to write to.
     * @param zipFile The file
     * @throws IOException If the file cannot be opened for writing
     */
    public ZipWriter(Path zipFile) throws IOException {
        Files.createDirectories(zipFile.getParent());
        if (Files.exists(zipFile)) {
            try (ZipOutputStream _ = new ZipOutputStream(Files.newOutputStream(zipFile))) {
                // This creates a new zip in the place of an existing one using ZipOutputStream as java nio is incapable of doing that
            }
        }
        fileSystem = FileSystems.newFileSystem(URI.create("jar:" + zipFile.toUri()), Map.of("create", true));
        root = fileSystem.getRootDirectories().iterator().next();
    }

    @Override
    public void addJsonEntry(Object jsonContent, RelativePath path) {
        try {
            FileUtils.write(path.resolveAgainst(root), objectMapper.writeValueAsString(jsonContent));
        } catch (IOException _) {
            logger.error(WRITE_JSON_ERROR, path);
        }
    }

    @Override
    public void addFileContentEntry(RelativePath path, Path original) {
        try {
            Files.createDirectories(path.resolveAgainst(root).getParent());
            Files.copy(original, path.resolveAgainst(root));
        } catch (IOException _) {
            logger.error(COPY_FILE_ERROR, original, path);
        }
    }

    @Override
    public void writeStringEntry(String entry, RelativePath path) {
        try {
            FileUtils.write(path.resolveAgainst(root), entry);
        } catch (IOException _) {
            logger.error(WRITE_STRING_ERROR, path);
        }
    }

    @Override
    public void close() {
        try {
            fileSystem.close();
        } catch (IOException e) {
            logger.error(CLOSE_FILE_ERROR, e);
        }
    }
}
