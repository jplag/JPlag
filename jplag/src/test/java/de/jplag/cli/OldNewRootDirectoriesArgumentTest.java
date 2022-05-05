package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class OldNewRootDirectoriesArgumentTest extends CommandLineInterfaceTest {
    @Test
    public void testNoRootDirectories() {
        buildOptionsFromCLI();

        assertEquals(0, options.getSubmissionDirectories().size());
        assertEquals(0, options.getOldSubmissionDirectories().size());
    }

    @Test
    public void testTwoRootDirectoryArguments() {
        buildOptionsFromCLI("root1", "root2");

        assertEquals(2, options.getSubmissionDirectories().size());
        assertEquals(0, options.getOldSubmissionDirectories().size());
    }

    @Test
    public void testNewOption() {
        buildOptionsFromCLI("-new", "root1", "root2");

        assertEquals(2, options.getSubmissionDirectories().size());
        assertEquals(0, options.getOldSubmissionDirectories().size());
    }

    @Test
    public void testDoubleNewOption() {
        buildOptionsFromCLI("-new", "root1", "-new", "root2");

        assertEquals(2, options.getSubmissionDirectories().size());
        assertEquals(0, options.getOldSubmissionDirectories().size());
    }

    @Test
    public void testOldOption() {
        buildOptionsFromCLI("-old", "root1");

        assertEquals(0, options.getSubmissionDirectories().size());
        assertEquals(1, options.getOldSubmissionDirectories().size());
    }

    @Test
    public void testNewAndOldOption() {
        buildOptionsFromCLI("-new", "root1", "-old", "root2");

        assertEquals(1, options.getSubmissionDirectories().size());
        assertEquals(1, options.getOldSubmissionDirectories().size());
    }
}
