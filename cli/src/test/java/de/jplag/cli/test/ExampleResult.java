package de.jplag.cli.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;
import de.jplag.options.ParsingOptions;
import de.jplag.options.SubmissionFileOptions;
import de.jplag.util.FileUtils;

/**
 * Provides a simple {@link JPlagResult} for tests to use. The tests should not depend on the contents of the result.
 */
public class ExampleResult {
    private static final String EXAMPLE_CODE = """
            public class A {
            }
            """.stripIndent();

    private static JPlagResult result;

    /**
     * @return The example result
     * @throws IOException If no temp files can be created
     * @throws ExitException If JPlag throws an exception
     */
    public static JPlagResult getExampleResult() throws IOException, ExitException {
        if (result == null) {
            Path dir = Files.createTempDirectory("jplagCode");
            FileUtils.write(dir.resolve("A.java"), EXAMPLE_CODE);
            FileUtils.write(dir.resolve("B.java"), EXAMPLE_CODE);

            result = JPlag.run(new JPlagOptions(new SubmissionFileOptions(Set.of(dir), Set.of(), null), new ParsingOptions(new JavaLanguage(), 0,
                    null, false, false, false, ParsingOptions.DEFAULT_SIMILARITY_METRIC, ParsingOptions.DEFAULT_SIMILARITY_THRESHOLD)));
        }
        return result;
    }
}
