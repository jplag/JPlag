package de.jplag.reporting.jsonfactory;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.TestBase;

/**
 * Test for the directory manager that persists the results for the report viewer.
 */
class DirectoryManagerTest extends TestBase {
    private static final Path OUTPUT_PATH = Path.of(BASE_PATH, "output", "submissions");

    private static final String SUBMISSION_1 = "A";
    private static final String FILE_PATH_1 = "TerrainType.java";
    private static final String ROOT_1 = "basecode";

    private static final String SUBMISSION_2 = "Submission1.java";
    private static final String ROOT_2 = "FilesAsSubmissions";

    private static final String SUBMISSION_3 = "A";
    private static final Path FILE_PATH_3 = Path.of("B", "A", "TerrainType.java");
    private static final String ROOT_3 = "basecode-sameNameOfSubdirectoryAndRootdirectory";

    @Test
    @DisplayName("test normal submission with file in folder")
    void testCreateDirectoryBasecode() throws IOException {
        testDirectoryManager(ROOT_1, SUBMISSION_1, FILE_PATH_1);
    }

    @Test
    @DisplayName("test single file as submission")
    void testCreateDirectoryFileAsSubmission() throws IOException {
        testDirectoryManager(ROOT_2, SUBMISSION_2, "");
    }

    @Test
    @DisplayName("test same name of subdirectory and root directory")
    void testCreateDirectorySharedName() throws IOException {
        testDirectoryManager(ROOT_3, SUBMISSION_3, FILE_PATH_3.toString());
    }

    /**
     * Test the directory manager for a given scenario.
     * @param rootName is the name of the root folder.
     * @param submissionName is the name of the submission.
     * @param filePath is the path to the file relative to the submission. Empty for single file submissions.
     */
    private static void testDirectoryManager(String rootName, String submissionName, String filePath) {
        File submissionPath = Path.of(BASE_PATH, rootName, submissionName).toFile();
        File fullFilePath = new File(submissionPath, filePath);
        File expectation = new File(OUTPUT_PATH.toFile(), Path.of(submissionName, filePath.isEmpty() ? submissionName : filePath).toString());
        try {
            File directory = DirectoryManager.createDirectory(OUTPUT_PATH.toString(), submissionName, fullFilePath, submissionPath);
            Assertions.assertNotNull(directory);
            Assertions.assertEquals(expectation.getPath(), directory.getPath());
        } catch (IOException e) {
            fail("Directory manager threw an exception:", e);
        } finally {
            deleteDirectory(expectation);
        }
    }
}
