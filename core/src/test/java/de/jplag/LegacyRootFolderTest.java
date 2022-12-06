package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;

/**
 * Tests for the legacy behaviour of the String-based base code initializer.
 */
@Deprecated(since = "4.0.0", forRemoval = true)
class LegacyRootFolderTest extends TestBase {
    @Test
    void testMultiRootDirSeparateBasecode() throws ExitException {
        String basecodePath = getBasePath("basecode-base");
        List<String> paths = List.of(getBasePath("basecode"), getBasePath("SimpleDuplicate")); // 3 + 2 submissions.
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionName(basecodePath));
        assertEquals(5, result.getNumberOfSubmissions());
    }

    @Test
    void testMultiRootDirBasecodeInSubmissionDir() throws ExitException {
        String basecodePath = getBasePath("basecode", "base");
        List<String> paths = List.of(getBasePath("basecode"), getBasePath("SimpleDuplicate")); // 2 + 2 submissions.
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionName(basecodePath));
        assertEquals(4, result.getNumberOfSubmissions());
    }

    @Test
    void testMultiRootDirBasecodeName() {
        List<String> paths = List.of(getBasePath("basecode"), getBasePath("SimpleDuplicate"));
        String basecodePath = "base"; // Should *not* find basecode/base
        assertThrows(IllegalArgumentException.class, () -> runJPlag(paths, it -> it.withBaseCodeSubmissionName(basecodePath)));
    }

    @Test
    void testBasecodeInOldDirectory() throws ExitException {
        String basecodePath = getBasePath("basecode", "base");
        List<String> newDirectories = List.of(getBasePath("SimpleDuplicate")); // 2 submissions
        List<String> oldDirectories = List.of(getBasePath("basecode")); // 3 - 1 submissions
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it.withBaseCodeSubmissionName(basecodePath));
        int numberOfExpectedComparison = 1 + 2 * 2;
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }
}
