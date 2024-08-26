package de.jplag;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;

/**
 * Test class for the multi-root feature and the old-new feature.
 */
class RootFolderTest extends TestBase {

    private static final String BASECODE_SUBMISSION = "base";
    private static final String GLOBAL_BASECODE = "basecode-base";

    // root folder names:
    private static final String ROOT_1 = "basecode";
    private static final String ROOT_2 = "SimpleDuplicate";

    // number of submissions per root folder:
    private static final int ROOT_COUNT_1 = 3;
    private static final int ROOT_COUNT_2 = 2;

    @Test
    @DisplayName("test multiple root directories without basecode")
    void testMultiRootDirNoBasecode() throws ExitException {
        List<String> paths = List.of(getBasePath(ROOT_1), getBasePath(ROOT_2));
        JPlagResult result = runJPlag(paths, it -> it);
        assertEquals(ROOT_COUNT_1 + ROOT_COUNT_2, result.getNumberOfSubmissions());
    }

    @Test
    @DisplayName("test multiple root directories with external basecode submission")
    void testMultiRootDirSeparateBasecode() throws ExitException {
        String basecodePath = getBasePath(GLOBAL_BASECODE); // base code is not in root folder
        List<String> paths = List.of(getBasePath(ROOT_1), getBasePath(ROOT_2));
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionDirectory(new File(basecodePath)));
        assertEquals(ROOT_COUNT_1 + ROOT_COUNT_2, result.getNumberOfSubmissions());
    }

    @Test
    @DisplayName("test multiple root directories with basecode in one root directory")
    void testMultiRootDirBasecodeInRootDir() throws ExitException {
        String basecodePath = getBasePath(ROOT_1, BASECODE_SUBMISSION); // basecode is in root 1
        List<String> paths = List.of(getBasePath(ROOT_1), getBasePath(ROOT_2));
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionDirectory(new File(basecodePath)));
        // One submissions is removed as it is the basecode one:
        assertEquals(ROOT_COUNT_1 + ROOT_COUNT_2 - 1, result.getNumberOfSubmissions()); // -1 for basecode
    }

    @Test
    @DisplayName("test multiple root directories, one marked with as old")
    void testDisjunctNewAndOldRootDirectories() throws ExitException {
        List<String> newDirectories = List.of(getBasePath(ROOT_2));
        List<String> oldDirectories = List.of(getBasePath(ROOT_1));
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it);
        assertEquals(ROOT_COUNT_1 + ROOT_COUNT_2, result.getNumberOfSubmissions());
        int numberOfExpectedComparison = 1 + ROOT_COUNT_1 * ROOT_COUNT_2;
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }

    @Test
    @DisplayName("test multiple overlapping root directories, one marked with as old")
    void testOverlappingNewAndOldDirectoriesOverlap() throws ExitException {
        List<String> newDirectories = List.of(getBasePath(ROOT_2));
        List<String> oldDirectories = List.of(getBasePath(ROOT_2));
        assertDoesNotThrow(() -> runJPlag(newDirectories, oldDirectories, it -> it));
    }

    @Test
    @DisplayName("test multiple root directories with basecode in the old root directory")
    void testBasecodeInOldDirectory() throws ExitException {
        String basecodePath = getBasePath(ROOT_1, BASECODE_SUBMISSION); // basecode is in root 1
        List<String> newDirectories = List.of(getBasePath(ROOT_2));
        List<String> oldDirectories = List.of(getBasePath(ROOT_1));
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it.withBaseCodeSubmissionDirectory(new File(basecodePath)));
        int numberOfExpectedComparison = 1 + ROOT_COUNT_2 * (ROOT_COUNT_1 - 1); // -1 for basecode
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }

    @Test
    @DisplayName("test multiple submissions with same folder name")
    void testSubmissionsWithSameFolderName() throws ExitException {
        String[] roots = new String[]{"A", "B", "base"};
        List<String> submissions = Arrays.stream(roots)
                .map(it -> getBasePath("basecode" + File.separator + it))
                .toList();
        JPlagResult result = runJPlag(submissions, it -> it);
        List<String> submissionNames = result.getSubmissions().getSubmissions().stream().map(Submission::getName).sorted().toList();
        String conflictingFile = "TerrainType.java";

        for (int i = 0; i < roots.length; i++) {
            String expectedName = roots[i] + File.separator + conflictingFile;
            String effectiveName = submissionNames.get(i);
            assertEquals(expectedName, effectiveName);
        }
    }

    @Test
    @DisplayName("test single new submission")
    void testSingleNewSubmission() throws  ExitException {
        List<String> newSubmissions = List.of(getBasePath("basecode" + File.separator + "A"));
        List<String> oldSubmissions = List.of(getBasePath("basecode" + File.separator + "B"));
        JPlagResult result = runJPlag(newSubmissions, oldSubmissions, it -> it);
        long numberOfNewSubmissions = result.getSubmissions().getSubmissions().stream().filter(Submission::isNew).count();
        long numberOfOldSubmissions = result.getSubmissions().getSubmissions().stream().filter(it -> !it.isNew()).count();
        assertEquals(1, numberOfNewSubmissions);
        assertEquals(1, numberOfOldSubmissions);
    }

}
