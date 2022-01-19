package de.jplag;

import java.nio.file.Path;
import java.util.function.Consumer;

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

    protected JPlagResult runJPlagWithExclusionFile(String testSampleName, String exclusionFileName) throws ExitException {
        String blackList = Path.of(BASE_PATH, testSampleName, exclusionFileName).toString();
        return runJPlag(testSampleName, options -> options.setExclusionFileName(blackList));
    }

    protected JPlagResult runJPlagWithDefaultOptions(String testSampleName) throws ExitException {
        return runJPlag(testSampleName, options -> {
        });
    }

    protected JPlagResult runJPlag(String testSampleName, Consumer<JPlagOptions.JPlagOptionsBuilder> customization) throws ExitException {
        var optionBuilder = JPlagOptions.builder()
                .setRootDirectoryName(Path.of(BASE_PATH, testSampleName).toString())
                .setLanguageOption(LanguageOption.JAVA)
                .setVerbosity(Verbosity.LONG);
        customization.accept(optionBuilder);
        JPlag jplag = new JPlag(optionBuilder.build());
        return jplag.run();
    }
}
