package de.jplag.java.babylon;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import de.jplag.LanguageLoader;
import de.jplag.TokenPrinterUtils;
import de.jplag.java.JavaLanguage;
import de.jplag.java.babylon.tokenizer.impl.HighLevelBabylonTokenizer;

class Experiment implements BabylonDSL {
    private static final Path SOURCES = Path.of("languages", "java", "src", "test", "resources", "de", "jplag", "java");
    private static final Path DATASETS = Path.of("..", "SameSame repr", "datasets");

    static void main() throws Exception {
        var files = Set.of(DATASETS.resolve("Progpedia19/human/subm44/Sociologia.java").toFile());

        JavaBabylonLanguage language = (JavaBabylonLanguage) LanguageLoader.getLanguage("java-babylon").orElseThrow();
        language.getOptions().getTransformationNames().setValue("tryWithoutResources, print");
        language.getOptions().getTokenizerName().setValue(HighLevelBabylonTokenizer.IDENTIFIER);
        var tokens = language.parse(files, false);
        Files.writeString(Path.of("java-babylon.txt"), TokenPrinterUtils.printTokensByFile(tokens));

        var language2 = (JavaLanguage) LanguageLoader.getLanguage("java").orElseThrow();
        var tokens2 = language2.parse(files, false);
        Files.writeString(Path.of("java.txt"), TokenPrinterUtils.printTokensByFile(tokens2));
    }
}
