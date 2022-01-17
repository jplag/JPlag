package de.jplag;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguageProvider;
import de.jplag.options.JPlagOptions;
import de.jplag.options.Verbosity;

public abstract class TestBase {

    protected static final String BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "samples").toString();
    protected static final float DELTA = 0.1f;

    protected String getBasePath() {
        return BASE_PATH;
    }

    protected String getBasePath(String... subs) {
        String path = BASE_PATH;
        for (String sub: subs) {
            path += File.separator + sub;
        }
        return path;
    }

    protected JPlagResult runJPlagWithExclusionFile(String testSampleName, String exclusionFileName) throws ExitException {
        String blackList = Path.of(BASE_PATH, testSampleName, exclusionFileName).toString();
        return runJPlag(testSampleName, options -> options.setExclusionFileName(blackList));
    }

    protected JPlagResult runJPlagWithDefaultOptions(String testSampleName) throws ExitException {
        return runJPlag(testSampleName, options -> {
        });
    }

    protected JPlagResult runJPlag(String testSampleName, Consumer<JPlagOptions> customization) throws ExitException {
        JPlagOptions options = new JPlagOptions(Path.of(BASE_PATH, testSampleName).toString(), JavaLanguageProvider.NAME);
        options.setVerbosity(Verbosity.LONG);
        customization.accept(options);
        JPlag jplag = new JPlag(options);
        return jplag.run();
    }
}
