package jplag;

import jplag.options.JPlagOptions;
import jplag.options.LanguageOption;

public abstract class TestBase {

    protected JPlagResult runJPlagWithDefaultOptions(String testSampleName) throws ExitException {
        JPlagOptions options = new JPlagOptions(
                String.format("src/test/resources/samples/%s", testSampleName),
                LanguageOption.JAVA_1_9);

        JPlag jplag = new JPlag(options);
        return jplag.run();
    }

}
