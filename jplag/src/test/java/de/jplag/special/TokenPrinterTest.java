package de.jplag.special;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.Consumer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.TestBase;
import de.jplag.TokenPrinter;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

/**
 * Special test case the does not really test anything but prints the tokens and the corresponding line of code.
 * @author Timur Saglam
 */
public class TokenPrinterTest extends TestBase {
    private static final String LARGE_SPACE = "   ";
    private static final String LINE = "------------------";

    private static final int MIN_TOKEN_MATCH = 5;
    private static final String PRINTER_FOLDER = "PRINTER"; // in the folder 'jplag/src/test/resources/samples'

    @Disabled
    @Test
    void printCPPFiles() {
        printSubmissions(options -> {
            options.setLanguageOption(LanguageOption.C_CPP);
            options.setMinimumTokenMatch(MIN_TOKEN_MATCH); // for printing also allow small files
        });
    }

    @Disabled
    @Test
    void printJavaFiles() {
        printSubmissions(options -> {
            options.setMinimumTokenMatch(MIN_TOKEN_MATCH); // for printing also allow small files
        });
    }

    @Disabled("Not a meaningful test, used for designing the token set")
    @Test
    void printRLangFiles() {
        printSubmissions(options -> {
            options.setLanguageOption(LanguageOption.R_LANG);
            options.setMinimumTokenMatch(MIN_TOKEN_MATCH); // for printing also allow small files
        });
    }

    @Disabled("Not a meaningful test, used for designing the token set")
    @Test
    void printGoFiles() {
        printSubmissions(options -> options.setLanguageOption(LanguageOption.GO_LANG));
    }

    @Disabled("Not a meaningful test, used for designing the token set")
    @Test
    void printKotlinFiles() {
        printSubmissions(options -> options.setLanguageOption(LanguageOption.KOTLIN));
    }

    private void printSubmissions(Consumer<JPlagOptions> optionsCustomization) {
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
        System.out.println(TokenPrinter.printTokens(submission.getTokenList(), submission.getFiles(), submission.getRoot()));
    }

}
