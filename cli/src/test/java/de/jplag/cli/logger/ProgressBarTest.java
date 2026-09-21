package de.jplag.cli.logger;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarType;

class ProgressBarTest {
    @Test
    void testInitProgressBar() {
        Level originalLogLevel = CollectedLogger.getLogLevel();
        CollectedLogger.setLogLevel(Level.INFO);

        ProgressBar progressBar = new CliProgressBarProvider().initProgressBar(ProgressBarType.COMPARING, 10);
        assertInstanceOf(TongfeiProgressBar.class, progressBar);

        CollectedLogger.setLogLevel(originalLogLevel);
    }
}
