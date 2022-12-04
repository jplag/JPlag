package de.jplag;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.RootDirectoryException;
import de.jplag.options.JPlagOptions;

/**
 * Test for the same root cases that will be re-named.
 */
public class PreventSameRootNameTest extends TestBase {

    private static final JPlagOptions BASEOPTIONS = new JPlagOptions(new de.jplag.java.Language(), Set.of(), Set.of());
    private static final String NOTEXISTDIRECTORY = Path.of(TestBase.BASE_PATH, "basecode", "NotExistDirectory").toString();
    private static final String SAMPLE_SAMEROOTNAME_1 = Path.of(TestBase.BASE_PATH, "SameRootName", "2019", "root").toString();
    private static final String SAMPLE_SAMEROOTNAME_2 = Path.of(TestBase.BASE_PATH, "SameRootName", "2020", "root").toString();

    @Test
    @DisplayName("test same root directory with withSubmissionDirectories options")
    void testSameRootWithNew() {
        JPlagOptions options = BASEOPTIONS.withSubmissionDirectories(Set.of(new File(SAMPLE_SAMEROOTNAME_1), new File(SAMPLE_SAMEROOTNAME_2)));
        try {
            PreventSameRootName preventSameRootName = new PreventSameRootName(options);
            JPlagOptions newOptions = preventSameRootName.getOptions();
            File submissionDirectory_2 = options.submissionDirectories().stream().toList().get(0);
            File submissionDirectory_1 = options.submissionDirectories().stream().toList().get(1);
            File newChangedSubmissionDirectory_2 = newOptions.submissionDirectories().stream().toList().get(0);
            File newChangedSubmissionDirectory_1 = newOptions.submissionDirectories().stream().toList().get(1);
            Assertions.assertEquals(submissionDirectory_2.getPath() + "_2", newChangedSubmissionDirectory_2.getPath());
            Assertions.assertEquals(submissionDirectory_1.getPath() + "_1", newChangedSubmissionDirectory_1.getPath());
        } catch (RootDirectoryException e) {
            fail("PreventSameRootName Class threw an exception:", e);
        }
    }

    @Test
    @DisplayName("test same root directory with withSubmissionDirectories and oldSubmissionDirectories options")
    void testSameRootWithNewAndOld() {
        JPlagOptions options = BASEOPTIONS.withSubmissionDirectories(Set.of(new File(SAMPLE_SAMEROOTNAME_2)))
                .withOldSubmissionDirectories(Set.of(new File(SAMPLE_SAMEROOTNAME_1)));
        try {
            PreventSameRootName preventSameRootName = new PreventSameRootName(options);
            JPlagOptions newOptions = preventSameRootName.getOptions();
            File oldSubmissionDirectory = options.oldSubmissionDirectories().stream().toList().get(0);
            File submissionDirectory = options.submissionDirectories().stream().toList().get(0);
            File newChangedOldSubmissionDirectory = newOptions.oldSubmissionDirectories().stream().toList().get(0);
            File newChangedSubmissionDirectory = newOptions.submissionDirectories().stream().toList().get(0);
            Assertions.assertEquals(submissionDirectory.getPath() + "_2", newChangedSubmissionDirectory.getPath());
            Assertions.assertEquals(oldSubmissionDirectory.getPath() + "_1", newChangedOldSubmissionDirectory.getPath());
        } catch (RootDirectoryException e) {
            fail("PreventSameRootName Class threw an exception:", e);
        }
    }

    @Test
    @DisplayName("test not existed root directory with withSubmissionDirectories option")
    void testCheckDirectoryExistWithNewSubmissionDirectories() {
        JPlagOptions options = BASEOPTIONS.withSubmissionDirectories(Set.of(new File(NOTEXISTDIRECTORY)));
        testRootDirectoryException(options);
    }

    @Test
    @DisplayName("test not existed root directory with withOldSubmissionDirectories option")
    void testCheckDirectoryExistWithOldSubmissionDirectories() {
        JPlagOptions options = BASEOPTIONS.withOldSubmissionDirectories(Set.of(new File(NOTEXISTDIRECTORY)));
        testRootDirectoryException(options);
    }

    /**
     * Test RootDirectoryException for a given Jplag-options.
     * @param options is the customized Jplag-options.
     */
    void testRootDirectoryException(JPlagOptions options) {
        RootDirectoryException exception = Assertions.assertThrows(RootDirectoryException.class, () -> new PreventSameRootName(options));
        Assertions.assertEquals("Submission Directory doesn't exist: " + NOTEXISTDIRECTORY, exception.getMessage());
    }
}
