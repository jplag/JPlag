package de.jplag.special;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.ReportObjectFactory;

/**
 * Test class to validate the code example syntax used in the Readme. As the examples use fictional file paths,
 * executing the tests would fail.
 */
@Disabled("Not an actual test class. Used to validate Readme test example syntax")
class ReadmeCodeExampleTest {
    /**
     * Minimal example for using JPlag. To assure that the Readme is always correct, the method's body must be kept in sync
     * with the code example in `Readme.md`.
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testReadmeCodeExample() {
        Language language = new JavaLanguage();
        Set<File> submissionDirectories = Set.of(new File("/path/to/rootDir"));
        File baseCode = new File("/path/to/baseCode");
        JPlagOptions options = new JPlagOptions(language, submissionDirectories, Set.of()).withBaseCodeSubmissionDirectory(baseCode);

        try {
            JPlagResult result = JPlag.run(options);

            // Optional
            ReportObjectFactory reportObjectFactory = new ReportObjectFactory(new File("/path/to/output"));
            reportObjectFactory.createAndSaveReport(result);
        } catch (ExitException e) {
            // error handling here
        } catch (FileNotFoundException e) {
            // handle IO exception here
        }
    }
}
