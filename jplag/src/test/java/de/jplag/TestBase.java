package de.jplag;

import java.util.function.Consumer;

import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;
import de.jplag.options.Verbosity;

public abstract class TestBase {

    private static final String BASE_PATH = "src/test/resources/de/jplag/samples";

    protected String getBasePath() {
        return BASE_PATH;
    }

    protected JPlagResult runJPlagWithExclusionFile(String testSampleName, String exclusionFileName) throws ExitException {
        String blackList = String.format(getBasePath() + "/%s/%s", testSampleName, exclusionFileName);
        return runJPlag(testSampleName, options -> options.setExclusionFileName(blackList));
    }

    protected JPlagResult runJPlagWithDefaultOptions(String testSampleName) throws ExitException {
        return runJPlag(testSampleName, options -> {});
    }

    protected JPlagResult runJPlag(String testSampleName, Consumer<JPlagOptions> customization) throws ExitException {
        JPlagOptions options = new JPlagOptions(String.format(getBasePath() + "/%s", testSampleName), LanguageOption.JAVA_1_9);
        options.setVerbosity(Verbosity.LONG);
        customization.accept(options);
        JPlag jplag = new JPlag(options);
        return jplag.run();
    }
}
