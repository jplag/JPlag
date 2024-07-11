package de.jplag.cli.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TerminalPrinterTest {
    private final static String TEST_MESSAGE = "Hello World";

    private static ByteArrayOutputStream outputStream;

    @BeforeAll
    static void setUp() {
        outputStream = new ByteArrayOutputStream();
        TerminalPrinter.getInstance().setOutputStream(new PrintStream(outputStream));
    }

    @AfterAll
    static void tearDown() {
        TerminalPrinter.getInstance().setOutputStream(System.out);
    }

    @AfterEach
    void cleanUpAfterTest() {
        TerminalPrinter.getInstance().unDelay();
        outputStream.reset();
    }

    @Test
    void testDelay() {
        TerminalPrinter.getInstance().delay();
        TerminalPrinter.getInstance().println(TEST_MESSAGE);

        Assertions.assertEquals("", outputStream.toString());

        TerminalPrinter.getInstance().unDelay();

        Assertions.assertEquals(TEST_MESSAGE + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testDirectPrinting() {
        TerminalPrinter.getInstance().println(TEST_MESSAGE);
        Assertions.assertEquals(TEST_MESSAGE + System.lineSeparator(), outputStream.toString());
    }
}
