package de.jplag.cli.logger;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class CollectedLoggerFactoryTest {
    private static final String TEST_LOGGER_NAME = "test";

    @Test
    void testRequestSameLoggerTwice() {
        CollectedLoggerFactory factory = new CollectedLoggerFactory();
        Logger first = factory.getLogger(TEST_LOGGER_NAME);
        Logger second = factory.getLogger(TEST_LOGGER_NAME);

        assertSame(first, second);
    }
}
