package jplag;

import java.util.function.Consumer;

import jplag.options.JPlagOptions;
import jplag.options.LanguageOption;

public abstract class TestBase {

    private static final String BASE_PATH = "src/test/resources/samples";

    protected JPlagResult runJPlagWithExclusionFile(String testSampleName, String exclusionFileName) throws ExitException {
        String blackList = String.format(BASE_PATH + "/%s/%s", testSampleName, exclusionFileName);
        return runJPlagWithOptions(testSampleName, options -> options.setExclusionFileName(blackList));
    }

    protected JPlagResult runJPlagWithDefaultOptions(String testSampleName) throws ExitException {
        return runJPlagWithOptions(testSampleName, options -> {});
    }

    protected JPlagResult runJPlagWithOptions(String testSampleName, Consumer<JPlagOptions> customization) throws ExitException {
        JPlagOptions options = new JPlagOptions(String.format(BASE_PATH + "/%s", testSampleName), LanguageOption.JAVA_1_9);
        options.setDebugParser(true);
        customization.accept(options);
        JPlag jplag = new JPlag(options);
        return jplag.run();
    }

}
