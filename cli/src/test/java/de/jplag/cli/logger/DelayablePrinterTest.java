package de.jplag.cli.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DelayablePrinterTest {
    private static final String TEST_MESSAGE = "Hello World";

    private static ByteArrayOutputStream outputStream;

    @BeforeAll
    static void setUp() {
        outputStream = new ByteArrayOutputStream();
        DelayablePrinter.getInstance().setOutputStream(new PrintStream(outputStream));
    }

    @AfterAll
    static void tearDown() {
        DelayablePrinter.getInstance().setOutputStream(System.out);
    }

    @AfterEach
    void cleanUpAfterTest() {
        DelayablePrinter.getInstance().resume();
        outputStream.reset();
    }

    @Test
    void testDelay() {
        DelayablePrinter.getInstance().delay();
        DelayablePrinter.getInstance().println(TEST_MESSAGE);

        Assertions.assertEquals("", outputStream.toString());

        DelayablePrinter.getInstance().resume();

        Assertions.assertEquals(TEST_MESSAGE + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testDirectPrinting() {
        DelayablePrinter.getInstance().println(TEST_MESSAGE);
        Assertions.assertEquals(TEST_MESSAGE + System.lineSeparator(), outputStream.toString());
    }
}
