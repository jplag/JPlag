package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OldNewRootDirectoriesArgumentTest extends CommandLineInterfaceTest {
    @Test
    void testNoRootDirectories() {
        buildOptionsFromCLI();

        assertEquals(0, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testTwoRootDirectoryArguments() {
        buildOptionsFromCLI("root1", "root2");

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testNewOption() {
        buildOptionsFromCLI("-new", "root1", "root2");

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testDoubleNewOption() {
        buildOptionsFromCLI("-new", "root1", "-new", "root2");

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testOldOption() {
        buildOptionsFromCLI("-old", "root1");

        assertEquals(0, options.submissionDirectories().size());
        assertEquals(1, options.oldSubmissionDirectories().size());
    }

    @Test
    void testNewAndOldOption() {
        buildOptionsFromCLI("-new", "root1", "-old", "root2");

        assertEquals(1, options.submissionDirectories().size());
        assertEquals(1, options.oldSubmissionDirectories().size());
    }
}
