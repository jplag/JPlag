package de.jplag;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.jplag.exceptions.ExitException;
import de.jplag.java.Language;
import de.jplag.options.JPlagOptions;

public abstract class TestBase {

    protected static final String BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "samples").toString();
    protected static final double DELTA = 0.001;

    protected String getBasePath() {
        return BASE_PATH;
    }

    protected String getBasePath(String... subs) {
        StringJoiner joiner = new StringJoiner(File.separator);
        joiner.add(BASE_PATH);
        for (String sub : subs) {
            joiner.add(sub);
        }
        return joiner.toString();
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
        var newFiles = newPaths.stream().map(path -> new File(path)).collect(Collectors.toSet());
        var oldFiles = oldPaths.stream().map(path -> new File(path)).collect(Collectors.toSet());
        JPlagOptions options = new JPlagOptions(LanguageLoader.getLanguage(Language.IDENTIFIER).orElseThrow(), newFiles, oldFiles);
        options = customization.apply(options);
        JPlag jplag = new JPlag(options);
        return jplag.run();
    }
}
