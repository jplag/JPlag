package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.RootDirectoryException;

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
        List<Path> paths = List.of(getBasePath(ROOT_1), getBasePath(ROOT_2));
        JPlagResult result = runJPlag(paths, it -> it);
        assertEquals(ROOT_COUNT_1 + ROOT_COUNT_2, result.getNumberOfSubmissions());
    }

    @Test
    @DisplayName("test multiple root directories with external basecode submission")
    void testMultiRootDirSeparateBasecode() throws ExitException {
        Path basecodePath = getBasePath(GLOBAL_BASECODE); // base code is not in root folder
        List<Path> paths = List.of(getBasePath(ROOT_1), getBasePath(ROOT_2));
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionDirectory(basecodePath));
        assertEquals(ROOT_COUNT_1 + ROOT_COUNT_2, result.getNumberOfSubmissions());
    }

    @Test
    @DisplayName("test multiple root directories with basecode in one root directory")
    void testMultiRootDirBasecodeInRootDir() throws ExitException {
        Path basecodePath = getBasePath(ROOT_1, BASECODE_SUBMISSION); // basecode is in root 1
        List<Path> paths = List.of(getBasePath(ROOT_1), getBasePath(ROOT_2));
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionDirectory(basecodePath));
        // One submissions is removed as it is the basecode one:
        assertEquals(ROOT_COUNT_1 + ROOT_COUNT_2 - 1, result.getNumberOfSubmissions()); // -1 for basecode
    }

    @Test
    @DisplayName("test multiple root directories, one marked with as old")
    void testDisjunctNewAndOldRootDirectories() throws ExitException {
        List<Path> newDirectories = List.of(getBasePath(ROOT_2));
        List<Path> oldDirectories = List.of(getBasePath(ROOT_1));
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it);
        assertEquals(ROOT_COUNT_1 + ROOT_COUNT_2, result.getNumberOfSubmissions());
        int numberOfExpectedComparison = 1 + ROOT_COUNT_1 * ROOT_COUNT_2;
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }

    @Test
    @DisplayName("test multiple overlapping root directories, one marked with as old")
    void testOverlappingNewAndOldDirectoriesOverlap() {
        List<Path> newDirectories = List.of(getBasePath(ROOT_2));
        List<Path> oldDirectories = List.of(getBasePath(ROOT_2));
        assertThrows(RootDirectoryException.class, () -> runJPlag(newDirectories, oldDirectories, it -> it));
    }

    @Test
    @DisplayName("test multiple root directories with basecode in the old root directory")
    void testBasecodeInOldDirectory() throws ExitException {
        Path basecodePath = getBasePath(ROOT_1, BASECODE_SUBMISSION); // basecode is in root 1
        List<Path> newDirectories = List.of(getBasePath(ROOT_2));
        List<Path> oldDirectories = List.of(getBasePath(ROOT_1));
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it.withBaseCodeSubmissionDirectory(basecodePath));
        int numberOfExpectedComparison = 1 + ROOT_COUNT_2 * (ROOT_COUNT_1 - 1); // -1 for basecode
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }
}
