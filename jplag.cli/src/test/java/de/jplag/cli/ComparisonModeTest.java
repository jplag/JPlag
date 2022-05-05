package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;
import de.jplag.options.JPlagOptions;
import de.jplag.strategy.ComparisonMode;

class ComparisonModeTest extends CommandLineInterfaceTest {

    @Test
    void testDefaultMode() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.DEFAULT_COMPARISON_MODE, options.getComparisonMode());
    }

    @Test
    void testInvalidMode() throws Exception {
        String argument = buildArgument(CommandLineArgument.COMPARISON_MODE, "Test'); DROP TABLE STUDENTS; --");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
    }

    @Test
    void testNormalMode() {
        ComparisonMode mode = ComparisonMode.NORMAL;
        String argument = buildArgument(CommandLineArgument.COMPARISON_MODE, mode.getName());
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(mode, options.getComparisonMode());
    }

    @Test
    void testParallelMode() {
        ComparisonMode mode = ComparisonMode.PARALLEL;
        String argument = buildArgument(CommandLineArgument.COMPARISON_MODE, mode.getName());
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(mode, options.getComparisonMode());
    }

}
