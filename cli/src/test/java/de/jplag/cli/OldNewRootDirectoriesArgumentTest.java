package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliArgumentBuilder;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class OldNewRootDirectoriesArgumentTest extends CliTest {
    private static final String[] TEST_ROOT_1 = new String[] {"root1"};
    private static final String[] TEST_ROOT_2 = new String[] {"root2"};
    private static final String[] TEST_ROOT_BOTH = new String[] {"root1", "root2"};

    @Test
    void testNoRootDirectories() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions();

        assertEquals(0, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testTwoRootDirectoryArguments() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SUBMISSION_DIRECTORIES, TEST_ROOT_BOTH));

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testNewOption() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.NEW_SUBMISSION_DIRECTORIES, TEST_ROOT_1));

        assertEquals(1, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testDoubleNewOption() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.NEW_SUBMISSION_DIRECTORIES, TEST_ROOT_BOTH));

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testOldOption() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.OLD_SUBMISSION_DIRECTORIES, TEST_ROOT_1));

        assertEquals(0, options.submissionDirectories().size());
        assertEquals(1, options.oldSubmissionDirectories().size());
    }

    @Test
    void testNewAndOldOption() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(
                args -> args.with(CliArgument.NEW_SUBMISSION_DIRECTORIES, TEST_ROOT_1).with(CliArgument.OLD_SUBMISSION_DIRECTORIES, TEST_ROOT_2));

        assertEquals(1, options.submissionDirectories().size());
        assertEquals(1, options.oldSubmissionDirectories().size());
    }

    @Override
    public void initializeParameters(CliArgumentBuilder args) {
        // Prevent default parameterization
    }
}
