package de.jplag.java.babylon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.jplag.LanguageLoader;
import de.jplag.Token;
import de.jplag.TokenPrinterUtils;
import de.jplag.java.JavaLanguage;

class Experiment {
    private static final Path JAVA_SOURCES = Path.of("languages", "java", "src", "test", "resources", "de", "jplag", "java");
    private static final Path JAVA_BABYLON_SOURCES = Path.of("languages", "java-babylon", "src", "test", "resources", "de", "jplag", "java",
            "babylon");
    private static final Path DATASETS = Path.of("..", "SameSame repr", "datasets");

    static void main() throws Exception {
        System.setProperty("jplag.java-babylon.pipeline.executor", "lazy");

        Set<File> files = listFiles(DATASETS.resolve("Progpedia56/refactor/plag-subm63"));

        JavaLanguage java = (JavaLanguage) LanguageLoader.getLanguage("java").orElseThrow();
        JavaBabylonLanguage babylon = (JavaBabylonLanguage) LanguageLoader.getLanguage("java-babylon").orElseThrow();

        babylon.getOptions().getTransformationNames().setValue(
                "assert-remove,try-with-resources-desugar,copy-elision,stream-fuse,enhanced-for-desugar,for-desugar,optional-elision,conditional-expression-desugar,switch-expression-desugar,constant-propagation,if-fuse,inline,block-normalize,constant-propagation,copy-elision,dead-code-elimination,copy-elision,dead-code-elimination,inline");
        babylon.getOptions().getTokenizerName().setValue("full");
        babylon.getOptions().clearCaches();
        babylon.parse(files, false);

        // write(Path.of("java.txt"), java.parse(files, false));
        //
        // babylon.getOptions().getTransformationNames().setValue("try-with-resources-desugar, lower, inline");
        // babylon.getOptions().getTokenizerName().setValue(HighLevelBabylonTokenizer.IDENTIFIER);
        // babylon.getOptions().clearCaches();
        // write(Path.of("java-babylon.txt"), babylon.parse(files, false));
        //
        // babylon.getOptions().getTransformationNames().setValue("try-with-resources-desugar, lower, inline");
        // babylon.getOptions().getTokenizerName().setValue(FullTypedBabylonTokenizer.IDENTIFIER);
        // babylon.getOptions().clearCaches();
        // write(Path.of("java-babylon-ft.txt"), babylon.parse(files, false));
    }

    private static Set<File> listFiles(Path directory) throws IOException {
        try (Stream<Path> stream = Files.list(directory)) {
            return stream.map(Path::toFile).collect(Collectors.toSet());
        }
    }

    private static void write(Path path, List<Token> tokens) throws IOException {
        Files.writeString(path, TokenPrinterUtils.printTokensByFile(tokens));
    }
}
