package de.jplag.special;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.Function;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.TestBase;
import de.jplag.TokenPrinter;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

/**
 * Special test case the does not really test anything but prints the tokens and the corresponding line of code.
 * @author Timur Saglam
 */
class TokenPrinterTest extends TestBase {
    private static final String LARGE_SPACE = "   ";
    private static final String LINE = "------------------";

    private static final int MIN_TOKEN_MATCH = 5;
    private static final String PRINTER_FOLDER = "PRINTER"; // in the folder 'jplag/src/test/resources/samples'

    private static final String LANGUAGE_CPP = de.jplag.cpp.Language.IDENTIFIER;
    private static final String LANGUAGE_R = de.jplag.rlang.Language.IDENTIFIER;
    private static final String LANGUAGE_KOTLIN = de.jplag.kotlin.Language.IDENTIFIER;

    private static final String LANGUAGE_GO = de.jplag.golang.Language.IDENTIFIER;

    @Disabled("Not a meaningful test, used for designing the token set")
    @Test
    void printCPPFiles() {
        printSubmissions(options -> options.withLanguageOption(new de.jplag.cpp.Language()).withMinimumTokenMatch(MIN_TOKEN_MATCH));
    }

    @Disabled("Not a meaningful test, used for designing the token set")
    @Test
    void printJavaFiles() {
        printSubmissions(options -> options.withMinimumTokenMatch(MIN_TOKEN_MATCH));
    }

    @Disabled("Not a meaningful test, used for designing the token set")
    @Test
    void printRLangFiles() {
        printSubmissions(options -> options.withLanguageOption(new de.jplag.rlang.Language()).withMinimumTokenMatch(MIN_TOKEN_MATCH));
    }

    @Disabled("Not a meaningful test, used for designing the token set")
    @Test
    void printGoFiles() {
        printSubmissions(options -> options.withLanguageOption(new de.jplag.golang.Language()));
    }

    @Disabled("Not a meaningful test, used for designing the token set")
    @Test
    void printKotlinFiles() {
        printSubmissions(options -> options.withLanguageOption(new de.jplag.kotlin.Language()));
    }

    private void printSubmissions(Function<JPlagOptions, JPlagOptions> optionsCustomization) {
        try {
            JPlagResult result = runJPlag(PRINTER_FOLDER, optionsCustomization);
            for (Submission submission : result.getSubmissions().getSubmissions()) {
                printSubmission(submission);
            }
            System.out.println("JPlag printed " + result.getSubmissions().numberOfSubmissions() + " valid submissions!");
        } catch (ExitException exception) {
            System.err.println("JPlag threw Error: " + exception.getMessage());
            fail();
        }
    }

    private void printSubmission(Submission submission) {
        System.out.println();
        System.out.println(LINE);
        System.out.println(LARGE_SPACE + submission.getName());
        System.out.println(LINE);
        System.out.println(TokenPrinter.printTokens(submission.getTokenList(), submission.getRoot()));
    }

}
