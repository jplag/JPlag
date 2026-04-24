package de.jplag.cli.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdleBarTest {
    private static final String TEST_BAR_TEXT = "Test";
    private static final long IDLE_BAR_ANIMATION_DELAY = 200;

    private static final int TARGET_FRAME_NUMBER = 5;

    /**
     * Tests if the output of the idle bar looks plausible.
     */
    @Test
    void testIdleBarPlausible() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream oldSystemOut = System.out;
        System.setOut(new PrintStream(outputStream));

        IdleBar idleBar = new IdleBar(TEST_BAR_TEXT);
        idleBar.start();
        while (outputStream.toString().split("\\r").length <= TARGET_FRAME_NUMBER) {
            Thread.yield();
        }

        idleBar.dispose();
        System.setOut(oldSystemOut);

        String result = outputStream.toString();
        String[] animationFrames = result.substring(1).split("\\r");

        String firstFrame = animationFrames[0];
        int numberOfSpaces = firstFrame.lastIndexOf('>') - firstFrame.indexOf('<') - 3 - 1;
        for (int i = 0; i < TARGET_FRAME_NUMBER; i++) {
            checkIdleBarOutput(animationFrames[i], i, numberOfSpaces);
        }
    }

    /**
     * Checks that the given string matches the expected content of an animation frame.
     * @param output The animation frame.
     * @param frameIndex The index of the frame.
     * @param numberOfSpaces The number of spaces within the bar.
     */
    private void checkIdleBarOutput(String output, int frameIndex, int numberOfSpaces) {
        int pass = frameIndex / numberOfSpaces;
        int offset = frameIndex % numberOfSpaces;
        if (pass % 2 == 1) {
            offset = numberOfSpaces - offset;
        }

        String expectedOutput = TEST_BAR_TEXT + ' ' + '<' + " ".repeat(offset) + "<+>" + " ".repeat(numberOfSpaces - offset) + '>';

        int endOfPredictableOutput = output.lastIndexOf(' ');
        String predictableOutput = output.substring(0, endOfPredictableOutput);
        String time = output.substring(endOfPredictableOutput + 1).trim();

        Assertions.assertEquals(expectedOutput, predictableOutput);
        Assertions.assertTrue(time.matches("[0-9]:[0-9]{2}:[0-9]{2}"), "Invalid format for time");

        String[] timeParts = time.split(":");
        int seconds = Integer.parseInt(timeParts[0]) * 60 * 60 + Integer.parseInt(timeParts[1]) * 60 + Integer.parseInt(timeParts[2]);
        int expectedTime = (int) (IDLE_BAR_ANIMATION_DELAY * frameIndex / 1000);
        Assertions.assertTrue(Math.abs(seconds - expectedTime) < 1, "Frame time of by more than one second");
    }
}