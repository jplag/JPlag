package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;

/**
 * Test class for the multi-root feature and the old-new feature.
 * @author Timur Saglam
 */
class RootFolderTest extends TestBase {

    private static final String BASECODE_SUBMISSION = "base";
    private static final String GLOBAL_BASECODE = "basecode-base";
    private static final String BASE_SAMPLE = "basecode";
    private static final String SIMPLE_SAMPLE = "SimpleDuplicate";

    @Test
    void testMultiRootDirNoBasecode() throws ExitException {
        List<String> paths = List.of(getBasePath(BASE_SAMPLE), getBasePath(SIMPLE_SAMPLE)); // 3 + 2 submissions.
        JPlagResult result = runJPlag(paths, it -> it);
        assertEquals(5, result.getNumberOfSubmissions());
    }

    @Test
    void testMultiRootDirSeparateBasecode() throws ExitException {
        String basecodePath = getBasePath(GLOBAL_BASECODE);
        List<String> paths = List.of(getBasePath(BASE_SAMPLE), getBasePath(SIMPLE_SAMPLE)); // 3 + 2 submissions.
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionDirectory(new File(basecodePath)));
        assertEquals(5, result.getNumberOfSubmissions());
    }

    @Test
    void testMultiRootDirBasecodeInSubmissionDir() throws ExitException {
        String basecodePath = getBasePath(BASE_SAMPLE, BASECODE_SUBMISSION);
        List<String> paths = List.of(getBasePath(BASE_SAMPLE), getBasePath(SIMPLE_SAMPLE)); // 2 + 2 submissions.
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionDirectory(new File(basecodePath)));
        assertEquals(4, result.getNumberOfSubmissions());
    }

    @Test
    void testDisjunctNewAndOldRootDirectories() throws ExitException {
        List<String> newDirectories = List.of(getBasePath(SIMPLE_SAMPLE)); // 2 submissions
        List<String> oldDirectories = List.of(getBasePath(BASE_SAMPLE)); // 3 submissions
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it);
        int numberOfExpectedComparison = 1 + 3 * 2;
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }

    @Test
    void testOverlappingNewAndOldDirectoriesOverlap() throws ExitException {
        List<String> newDirectories = List.of(getBasePath(SIMPLE_SAMPLE)); // 2 submissions
        List<String> oldDirectories = List.of(getBasePath(SIMPLE_SAMPLE));
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it);
        int numberOfExpectedComparison = 1;
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }

    @Test
    void testBasecodeInOldDirectory() throws ExitException {
        String basecodePath = getBasePath(BASE_SAMPLE, BASECODE_SUBMISSION);
        List<String> newDirectories = List.of(getBasePath(SIMPLE_SAMPLE)); // 2 submissions
        List<String> oldDirectories = List.of(getBasePath(BASE_SAMPLE)); // 3 - 1 submissions
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it.withBaseCodeSubmissionDirectory(new File(basecodePath)));
        int numberOfExpectedComparison = 1 + 2 * 2;
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }
}
