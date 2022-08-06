package de.jplag;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;
import de.jplag.options.Verbosity;

public abstract class TestBase {

    protected static final String BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "samples").toString();
    protected static final float DELTA = 0.1f;

    protected String getBasePath() {
        return BASE_PATH;
    }

    protected String getBasePath(String... subs) {
        String path = BASE_PATH;
        for (String sub : subs) {
            path += File.separator + sub;
        }
        return path;
    }

    protected JPlagResult runJPlagWithExclusionFile(String testSampleName, String exclusionFileName) throws ExitException {
        String blackList = Path.of(BASE_PATH, testSampleName, exclusionFileName).toString();
        return runJPlag(testSampleName, options -> options.withExclusionFileName(blackList));
    }

    protected JPlagResult runJPlagWithDefaultOptions(String testSampleName) throws ExitException {
        return runJPlag(testSampleName, options -> options);
    }

    protected JPlagResult runJPlag(String testSampleName, Function<JPlagOptions, JPlagOptions> customization) throws ExitException {
        return runJPlag(List.of(getBasePath(testSampleName)), List.of(), customization);
    }

    protected JPlagResult runJPlag(List<String> newPaths, Function<JPlagOptions, JPlagOptions> customization) throws ExitException {
        return runJPlag(newPaths, List.of(), customization);
    }

    protected JPlagResult runJPlag(List<String> newPaths, List<String> oldPaths, Function<JPlagOptions, JPlagOptions> customization)
            throws ExitException {
        JPlagOptions options = new JPlagOptions(LanguageOption.JAVA, newPaths, oldPaths);
        options = customization.apply(options);
        options = options.withVerbosity(Verbosity.LONG);
        JPlag jplag = new JPlag(options);
        return jplag.run();
    }
}
