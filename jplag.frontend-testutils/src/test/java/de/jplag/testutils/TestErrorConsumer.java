package de.jplag.testutils;

import static org.junit.jupiter.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ErrorConsumer;

/**
 * Mock error consumer that fails the test case on error occurrence.
 * @author Timur Saglam
 */
public class TestErrorConsumer implements ErrorConsumer {
    private final Logger logger = LoggerFactory.getLogger("JPlag-Test");

    @Override
    public void addError(String errorMessage) {
        logger.error(errorMessage);
        fail(errorMessage);
    }

    @Override
    public void print(String message, String longMessage) {
        logger.info(message);
    }

}
