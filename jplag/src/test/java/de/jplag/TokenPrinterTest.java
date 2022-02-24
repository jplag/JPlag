package de.jplag;

import static org.junit.Assert.fail;

import java.util.function.Consumer;

import org.junit.Test;

import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

/**
 * Temporary test case the does not really test anything but prints the tokens and the corresponding line of code.
 * Should not be merged into master as this is only for explorative testing.
 * @author Timur Saglam
 */
public class TokenPrinterTest extends TestBase {
    private static final String PRINTER_FOLDER = "PRINTER"; // in the folder 'jplag/src/test/resources/samples'

    @Test
    public void printCPPFiles() {
        printSubmissions(options -> {
            options.setLanguageOption(LanguageOption.C_CPP);
            options.setMinimumTokenMatch(5); // for printing also allow small files
        });
    }

    @Test
    public void printJavaFiles() {
        printSubmissions(options -> {
            options.setMinimumTokenMatch(5); // for printing also allow small files
        });
    }

    private void printSubmissions(Consumer<JPlagOptions> optionsCustomization) {
        try {
            JPlagResult result = runJPlag(PRINTER_FOLDER, optionsCustomization);
            for (Submission submission : result.getSubmissions().getSubmissions()) {
                System.out.println();
                System.out.println("------------------");
                System.out.println("   " + submission.getName());
                System.out.println("------------------");
                TokenPrinter.printTokens(submission);
            }
            System.out.println("JPlag printed " + result.getSubmissions() + " valid submissions!");
        } catch (ExitException e) {
            System.err.println("JPlag threw Error: " + e.getMessage());
            fail();
        }
    }

}
