package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OldNewRootDirectoriesArgumentTest extends CommandLineInterfaceTest {
    @Test
    void testNoRootDirectories() throws CliException {
        buildOptionsFromCLI(arguments());

        assertEquals(0, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testTwoRootDirectoryArguments() throws CliException {
        buildOptionsFromCLI(arguments().rootDirectory("root1", "root2"));

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testNewOption() throws CliException {
        buildOptionsFromCLI(arguments().newRootDirectories("root1", "root2"));

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testDoubleNewOption() throws CliException {
        buildOptionsFromCLI(arguments().newRootDirectories("root1").newRootDirectories("root2"));

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testOldOption() throws CliException {
        buildOptionsFromCLI(arguments().oldRootDirectories("root1"));

        assertEquals(0, options.submissionDirectories().size());
        assertEquals(1, options.oldSubmissionDirectories().size());
    }

    @Test
    void testNewAndOldOption() throws CliException {
        buildOptionsFromCLI(arguments().newRootDirectories("root2").oldRootDirectories("root2"));

        assertEquals(1, options.submissionDirectories().size());
        assertEquals(1, options.oldSubmissionDirectories().size());
    }
}
