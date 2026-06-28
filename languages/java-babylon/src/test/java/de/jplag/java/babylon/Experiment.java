package de.jplag.java.babylon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import de.jplag.LanguageLoader;
import de.jplag.Token;
import de.jplag.TokenPrinterUtils;
import de.jplag.java.JavaLanguage;
import de.jplag.java.babylon.tokenizer.impl.FullTypedBabylonTokenizer;
import de.jplag.java.babylon.tokenizer.impl.HighLevelBabylonTokenizer;

class Experiment implements BabylonDSL {
    private static final Path JAVA_SOURCES = Path.of("languages", "java", "src", "test", "resources", "de", "jplag", "java");
    private static final Path JAVA_BABYLON_SOURCES = Path.of("languages", "java-babylon", "src", "test", "resources", "de", "jplag", "java",
            "babylon");
    private static final Path DATASETS = Path.of("..", "SameSame repr", "datasets");

    static void main() throws Exception {
        System.setProperty("jplag.java-babylon.pipeline.executor", "lazy");

        Set<File> files = Set.of(DATASETS.resolve("Progpedia19/human/subm44/Sociologia.java").toFile());

        JavaLanguage java = (JavaLanguage) LanguageLoader.getLanguage("java").orElseThrow();
        JavaBabylonLanguage babylon = (JavaBabylonLanguage) LanguageLoader.getLanguage("java-babylon").orElseThrow();

        write(Path.of("java.txt"), java.parse(files, false));

        babylon.getOptions().getTransformationNames().setValue("try-with-resources-desugar, lower, inline");
        babylon.getOptions().getTokenizerName().setValue(HighLevelBabylonTokenizer.IDENTIFIER);
        babylon.getOptions().clearCaches();
        write(Path.of("java-babylon.txt"), babylon.parse(files, false));

        babylon.getOptions().getTransformationNames().setValue("try-with-resources-desugar, lower, inline");
        babylon.getOptions().getTokenizerName().setValue(FullTypedBabylonTokenizer.IDENTIFIER);
        babylon.getOptions().clearCaches();
        write(Path.of("java-babylon-ft.txt"), babylon.parse(files, false));
    }

    private static void write(Path path, List<Token> tokens) throws IOException {
        Files.writeString(path, TokenPrinterUtils.printTokensByFile(tokens));
    }
}
