package de.jplag.java.babylon;

import java.nio.file.Path;
import java.util.Set;

import de.jplag.LanguageLoader;
import de.jplag.TokenPrinterUtils;
import de.jplag.java.babylon.tokenizer.impl.HighLevelBabylonTokenizer;

class Experiment implements BabylonDSL {
    private static final Path SOURCES = Path.of("languages", "java", "src", "test", "resources", "de", "jplag", "java");

    static void main() throws Exception {
        var files = Set.of(SOURCES.resolve("TryWithResource.java").toFile());
        JavaBabylonLanguage language = (JavaBabylonLanguage) LanguageLoader.getLanguage("java-babylon").orElseThrow();
        language.getOptions().getTransformationNames().setValue("tryWithoutResources, print");
        language.getOptions().getTokenizerName().setValue(HighLevelBabylonTokenizer.IDENTIFIER);
        var tokens = language.parse(files, false);
        IO.println(TokenPrinterUtils.printTokensByFile(tokens));
    }
}
