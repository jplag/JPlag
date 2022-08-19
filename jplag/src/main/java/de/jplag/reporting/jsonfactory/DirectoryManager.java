package de.jplag.reporting.jsonfactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryManager {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryManager.class);

    /**
     * Creates a directory.
     * @param path The path under which the new directory ought to be created
     * @param name The name of the new directory
     * @return The created directory
     */
    public static File createDirectory(String path, String name) throws IOException {
        File directory = new File(path.concat("/").concat(name));
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create dir.");
        }
        return directory;
    }

    /**
     * Create a directory with the given path
     * @param path The path of the new directory
     */
    public static void createDirectory(String path) throws IOException {
        createDirectory(path, "");
    }

    /**
     * Delete the directory and all of its contents, identified by the given path
     * @param path The path that identifies the directory to delete
     */
    public static void deleteDirectory(String path) {
        try (var f = Files.walk(Path.of(path))) {
            f.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            logger.error("Could not delete folder " + path, e);
        }
    }

    /**
     * Zip the directory identified by the given path
     * @param path The path that identifies the directory to zip
     * @return True if zip was successful, false otherwise
     */
    public static boolean zipDirectory(String path) {
        Path p = Path.of(path);
        String zipName = path + ".zip";

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipName))) {

            Files.walkFileTree(p, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = p.relativize(file);
                    zos.putNextEntry(new ZipEntry(targetFile.toString()));

                    byte[] bytes = Files.readAllBytes(file);
                    zos.write(bytes, 0, bytes.length);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    logger.error("Unable to zip " + file, exc);
                    throw exc;
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            deleteDirectory(zipName);
            return false;
        }
        return true;
    }
}
