package de.jplag.java.babylon;

import de.jplag.TokenPrinterUtils;

import java.nio.file.Path;
import java.util.Set;

class Experiment implements BabylonDSL {
    private static final Path SOURCES = Path.of(
            "languages", "java", "src", "test", "resources", "de", "jplag", "java"
    );

    static void main() throws Exception {
        var files = Set.of(SOURCES.resolve("TryWithResource.java").toFile());
        JavaBabylonLanguage language = new JavaBabylonLanguage();
        language.getOptions().getTransformationNames().setValue("tryWithoutResources, print");
        var tokens = language.parse(files, false);
        IO.println(TokenPrinterUtils.printTokensByFile(tokens));
    }
}
