package de.jplag.cli.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;
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
            File dir = Files.createTempDirectory("jplagCode").toFile();
            FileUtils.write(new File(dir, "A.java"), EXAMPLE_CODE);
            FileUtils.write(new File(dir, "B.java"), EXAMPLE_CODE);

            result = JPlag.run(new JPlagOptions(new JavaLanguage(), Set.of(dir), Set.of()).withMinimumTokenMatch(0));
        }
        return result;
    }
}
