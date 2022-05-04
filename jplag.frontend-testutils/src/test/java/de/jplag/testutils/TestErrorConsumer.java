package de.jplag.testutils;

import static org.junit.Assert.fail;

import de.jplag.ErrorConsumer;

/**
 * Mock error consumer that fails the test case on error occurence.
 * @author Timur Saglam
 */
public class TestErrorConsumer implements ErrorConsumer {

    @Override
    public void addError(String errorMessage) {
        System.err.println(errorMessage);
        fail(errorMessage);
    }

    @Override
    public void print(String message, String longMessage) {
        System.out.println(message);
    }

}
