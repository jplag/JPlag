package de.jplag.java;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.commentextraction.CommentExtractorSettings;
import de.jplag.commentextraction.EnvironmentDelimiter;

import com.google.auto.service.AutoService;

/**
 * Language for Java programs. Supports the Java version with which is project is build with (see top-level pom.xml).
 */
@AutoService(Language.class)
public class JavaLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".java");
    }

    @Override
    public String getName() {
        return "Java";
    }

    @Override
    public String getIdentifier() {
        return "java";
    }

    @Override
    public int minimumTokenMatch() {
        return 9;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new Parser().parse(files);
    }

    @Override
    public boolean tokensHaveSemantics() {
        return true;
    }

    @Override
    public boolean supportsNormalization() {
        return true;
    }

    @Override
    public String toString() {
        return this.getIdentifier();
    }

    @Override
    public Optional<CommentExtractorSettings> getCommentExtractorSettings() {
        return Optional.of(new CommentExtractorSettings(
                List.of(new EnvironmentDelimiter("\"\"\""), new EnvironmentDelimiter("\""), new EnvironmentDelimiter("'")), // No comment environment
                List.of("//"), // line comments
                List.of(new EnvironmentDelimiter("/*", "*/")), // block comments
                List.of("\\"))); // escape characters
    }
}
