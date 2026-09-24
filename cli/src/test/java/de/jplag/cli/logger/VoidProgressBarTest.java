package de.jplag.cli.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.slf4j.event.Level;

import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarType;

class VoidProgressBarTest {
    @Test
    void testVoidProgressBarBehaviour() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        System.setOut(new PrintStream(outputStream));

        VoidProgressBar progressBar = new VoidProgressBar();
        progressBar.step();
        progressBar.step(2);
        progressBar.dispose();

        System.setOut(originalOutput);

        assertEquals("", outputStream.toString());
    }

    @ParameterizedTest
    @MethodSource("getRelevantLogLevels")
    void testVoidProgressBarCreated(Level logLevel) {
        Level originalLogLevel = CollectedLogger.getLogLevel();
        CollectedLogger.setLogLevel(logLevel);

        ProgressBarType type = Mockito.mock(ProgressBarType.class);
        Mockito.when(type.isIdleBar()).thenReturn(false);
        ProgressBar progressBar = new CliProgressBarProvider().initProgressBar(type, 10);
        progressBar.dispose();

        assertInstanceOf(VoidProgressBar.class, progressBar);

        CollectedLogger.setLogLevel(originalLogLevel);
    }

    public static Level[] getRelevantLogLevels() {
        return new Level[] {Level.TRACE, Level.DEBUG, Level.ERROR, Level.WARN};
    }
}
