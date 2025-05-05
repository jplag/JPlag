package de.jplag.cli;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Language;
import de.jplag.LanguageLoader;

/**
 * LanguageChecker detects the set of languages based on the file suffixes in a directory.
 */
public class LanguageChecker {
    private static final Logger logger = LoggerFactory.getLogger(LanguageChecker.class);

    /**
     * Determines the language based on the file suffixes in the root directory.
     * @param rootDirectory The files in the root directory.
     * @param inputLanguage The user-specified language.
     * @return The detected language.
     */
    public Language languageChecker(File[] rootDirectory, Language inputLanguage) {
        Set<String> fileSuffixes = new HashSet<>();
        if (rootDirectory == null || inputLanguage == null) {
            return inputLanguage;
        }

        // Traverse the directory and collect file suffixes
        for (File file : rootDirectory) {
            traverse(file, fileSuffixes);
        }

        // If input language is specified, check if all suffixes are valid for this language
        Set<String> validSuffixes = new HashSet<>();
        for (String suffix : inputLanguage.suffixes()) {
            validSuffixes.add(suffix.toLowerCase());
        }
        if (validSuffixes.containsAll(fileSuffixes)) {
            logger.info("All file suffixes match the input language.");
            return inputLanguage;
        } else {
            logger.warn("Some file suffixes do not match the input language.");
        }

        // Detect possible matching languages based on the collected suffixes
        Set<Language> matchingLanguages = new HashSet<>();
        for (Language language : LanguageLoader.getAllAvailableLanguages().values()) {
            if (language.getName().equals("multi-language")) {
                continue;
            }
            Set<String> suffixSet = new HashSet<>();
            for (String s : language.suffixes()) {
                suffixSet.add(s.toLowerCase());
            }
            for (String suffix : fileSuffixes) {
                if (suffixSet.contains(suffix)) {
                    matchingLanguages.add(language);
                    break;
                }
            }
        }

        if (matchingLanguages.isEmpty()) {
            logger.warn("No matching languages found. InputLanguage: {} is invalid!", inputLanguage.getName());
            return inputLanguage;
        }

        if (matchingLanguages.size() == 1) {
            logger.warn("The language should be: {}", matchingLanguages.iterator().next());
            return matchingLanguages.iterator().next();
        }

        logger.warn("Multiple languages are founded, please use multi-language.");
        return inputLanguage;
    }

    /**
     * Recursively traverses directories and collects file suffixes.
     * @param file The current file or directory.
     * @param fileSuffixes The set of collected file suffixes.
     */
    private void traverse(File file, Set<String> fileSuffixes) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                traverse(child, fileSuffixes);  // Recursively process subdirectories
            }
        } else {
            String name = file.getName();
            int dotIndex = name.lastIndexOf(".");
            if (dotIndex != -1 && dotIndex < name.length() - 1) {
                String suffix = name.substring(dotIndex);
                fileSuffixes.add(suffix);
            }
        }
    }
}
