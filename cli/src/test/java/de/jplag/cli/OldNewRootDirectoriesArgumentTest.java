package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArg;
import de.jplag.cli.test.CliArgBuilder;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class OldNewRootDirectoriesArgumentTest extends CliTest {
    @Test
    void testNoRootDirectories() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions();

        assertEquals(0, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testTwoRootDirectoryArguments() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArg.SUBMISSION_DIRECTORIES, new String[] {"root1", "root2"}));

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testNewOption() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArg.NEW_SUBMISSION_DIRECTORIES, new String[] {"root1", "root2"}));

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testDoubleNewOption() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArg.NEW_SUBMISSION_DIRECTORIES, new String[] {"root1", "root2"}));

        assertEquals(2, options.submissionDirectories().size());
        assertEquals(0, options.oldSubmissionDirectories().size());
    }

    @Test
    void testOldOption() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArg.OLD_SUBMISSION_DIRECTORIES, new String[] {"root1"}));

        assertEquals(0, options.submissionDirectories().size());
        assertEquals(1, options.oldSubmissionDirectories().size());
    }

    @Test
    void testNewAndOldOption() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArg.NEW_SUBMISSION_DIRECTORIES, new String[] {"root1"})
                .with(CliArg.OLD_SUBMISSION_DIRECTORIES, new String[] {"root1"}));

        assertEquals(1, options.submissionDirectories().size());
        assertEquals(1, options.oldSubmissionDirectories().size());
    }

    @Override
    public void initializeParameters(CliArgBuilder args) {
    }
}
