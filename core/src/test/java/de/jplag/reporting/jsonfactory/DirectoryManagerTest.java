package de.jplag.reporting.jsonfactory;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DirectoryManagerTest {
    String separator = File.separator;

    @Test
    void testCreateDirectoryWithBasecode() throws IOException {
        String path = "src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "output" + separator + "submissions";
        String name = "A";
        File file = new File("src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "basecode" + separator + "A" + separator + "TerrainType.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(new File("src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator
                + "samples" + separator + "output" + separator + "submissions" + separator + "A" + separator + "TerrainType.java"), directory);
    }

    @Test
    void testCreateDirectoryWithFilesAssubmissions() throws IOException {
        String path = "src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "output" + separator + "submissions";
        String name = "Submission1.java";
        File file = new File("src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "FilesAssubmissions" + separator + "Submission1.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(
                new File("src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                        + separator + "output" + separator + "submissions" + separator + "Submission1.java" + separator + "Submission1.java"),
                directory);
    }

    @Test
    void testCreateDirectoryWithOtherSeparator() throws IOException {
        String path = "src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "output" + separator + "submissions";
        String name = "Submission1.java";
        File file = new File("src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "FilesAssubmissions" + separator + "Submission1.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(
                new File("src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                        + separator + "output" + separator + "submissions" + separator + "Submission1.java" + separator + "Submission1.java"),
                directory);
    }

    @Test
    void testCreateDirectoryAndFilenameContainsSubfoldername() throws IOException {
        String path = "src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "output" + separator + "submissions";
        String name = "A";
        File file = new File("src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "basecode" + separator + "A" + separator + "ABCDEA.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(new File("src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator
                + "samples" + separator + "output" + separator + "submissions" + separator + "A" + separator + "ABCDEA.java"), directory);
    }

    @Test
    void testCreateDirectoryWithMultipleFolders() throws IOException {
        String path = "src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "output" + separator + "submissions";
        String name = "A";
        File file = new File("src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples"
                + separator + "basecode" + separator + "A" + separator + "B" + separator + "A" + separator + "ABCDEA.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(new File(
                "src" + separator + "test" + separator + "resources" + separator + "de" + separator + "jplag" + separator + "samples" + separator
                        + "output" + separator + "submissions" + separator + "A" + separator + "B" + separator + "A" + separator + "ABCDEA.java"),
                directory);
    }
}
