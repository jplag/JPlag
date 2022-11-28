package de.jplag.reporting.jsonfactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.TestBase;

class DirectoryManagerTest extends TestBase {
    private static final String OUTPUT_PATH = Path.of(BASE_PATH, "output", "submissions").toString();

    // info of basecode-sample(in abbreviation BC)
    private static final String SAMPLE_NAME_BC = "basecode";
    private static final String ROOT_DIRECTORY_BC = "A";
    private static final String FILE_NAME_BC = "TerrainType.java";
    private static final File FILE_BC = new File(Path.of(BASE_PATH, SAMPLE_NAME_BC, ROOT_DIRECTORY_BC, FILE_NAME_BC).toString());
    private static final File SUBMISSION_ROOT_BC = new File(Path.of(BASE_PATH, SAMPLE_NAME_BC, ROOT_DIRECTORY_BC).toString());

    // info of FilesAsSubmissions-sample(in abbreviation FAS)
    private static final String SAMPLE_NAME_FAS = "FilesAsSubmissions";
    private static final String ROOT_DIRECTORY_FAS = "Submission1.java";
    private static final String FILE_NAME_FAS = "Submission1.java";
    private static final File FILE_FAS = new File(Path.of(BASE_PATH, SAMPLE_NAME_FAS, FILE_NAME_FAS).toString());
    private static final File SUBMISSION_ROOT_FAS = new File(Path.of(BASE_PATH, SAMPLE_NAME_FAS, ROOT_DIRECTORY_FAS).toString());

    // info of basecode-sameNameOfSubdirectoryAndRootdirectory-sample(in abbreviation BC-SOSAR)
    private static final String SAMPLE_NAME_BC_SOSAR = "basecode-sameNameOfSubdirectoryAndRootdirectory";
    private static final String ROOT_DIRECTORY_BC_SOSAR = "A";
    private static final String SUB_DIRECTORY_BC_SOSAR = Path.of("B", "A").toString();
    private static final String FILE_NAME_BC_SOSAR = "TerrainType.java";
    private static final File FILE_BC_SOSAR = new File(
            Path.of(BASE_PATH, SAMPLE_NAME_BC_SOSAR, ROOT_DIRECTORY_BC_SOSAR, SUB_DIRECTORY_BC_SOSAR, FILE_NAME_BC_SOSAR).toString());
    private static final File SUBMISSION_ROOT_BC_SOSAR = new File(Path.of(BASE_PATH, SAMPLE_NAME_BC_SOSAR, ROOT_DIRECTORY_BC_SOSAR).toString());

    @Test
    void testCreateDirectoryBC() throws IOException {
        File expectedFile = new File(Path.of(OUTPUT_PATH, ROOT_DIRECTORY_BC, FILE_NAME_BC).toString());
        test(expectedFile, OUTPUT_PATH, ROOT_DIRECTORY_BC, FILE_BC, SUBMISSION_ROOT_BC);
    }

    @Test
    void testCreateDirectoryFAS() throws IOException {
        File expectedFile = new File(Path.of(OUTPUT_PATH, ROOT_DIRECTORY_FAS, FILE_NAME_FAS).toString());
        test(expectedFile, OUTPUT_PATH, ROOT_DIRECTORY_FAS, FILE_FAS, SUBMISSION_ROOT_FAS);
    }

    @Test
    void testCreateDirectoryBC_SOSAR() throws IOException {
        File expectedFile = new File(Path.of(OUTPUT_PATH, ROOT_DIRECTORY_BC_SOSAR, SUB_DIRECTORY_BC_SOSAR, FILE_NAME_BC_SOSAR).toString());
        test(expectedFile, OUTPUT_PATH, ROOT_DIRECTORY_BC_SOSAR, FILE_BC_SOSAR, SUBMISSION_ROOT_BC_SOSAR);
    }

    void test(File expectedFile, String output, String rootDirectory, File file, File submissionRoot) throws IOException {
        File directory = DirectoryManager.createDirectory(output, rootDirectory, file, submissionRoot);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(expectedFile.getPath(), directory.getPath());
    }
}
