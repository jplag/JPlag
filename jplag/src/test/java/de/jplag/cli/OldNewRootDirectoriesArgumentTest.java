package de.jplag.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OldNewRootDirectoriesArgumentTest extends CommandLineInterfaceTest {
    @Test
    public void testNoRootDirectories() {
        buildOptionsFromCLI();

        assertEquals(0, options.getPlagiarismCheckRootDirectoryNames().size());
        assertEquals(0, options.getPriorSubmissionsDirectoryNames().size());
    }

    @Test
    public void testTwoRootDirectoryArguments() {
        buildOptionsFromCLI("root1", "root2");

        assertEquals(2, options.getPlagiarismCheckRootDirectoryNames().size());
        assertEquals(0, options.getPriorSubmissionsDirectoryNames().size());
    }

    @Test
    public void testNewOption() {
        buildOptionsFromCLI("-new", "root1", "root2");

        assertEquals(2, options.getPlagiarismCheckRootDirectoryNames().size());
        assertEquals(0, options.getPriorSubmissionsDirectoryNames().size());
    }

    @Test
    public void testDoubleNewOption() {
        buildOptionsFromCLI("-new", "root1", "-new", "root2");

        assertEquals(2, options.getPlagiarismCheckRootDirectoryNames().size());
        assertEquals(0, options.getPriorSubmissionsDirectoryNames().size());
    }

    @Test
    public void testOldOption() {
        buildOptionsFromCLI("-old", "root1");

        assertEquals(0, options.getPlagiarismCheckRootDirectoryNames().size());
        assertEquals(1, options.getPriorSubmissionsDirectoryNames().size());
    }

    @Test
    public void testNewAndOldOption() {
        buildOptionsFromCLI("-new", "root1", "-old", "root2");

        assertEquals(1, options.getPlagiarismCheckRootDirectoryNames().size());
        assertEquals(1, options.getPriorSubmissionsDirectoryNames().size());
    }
}
