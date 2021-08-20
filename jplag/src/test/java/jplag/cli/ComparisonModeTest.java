package jplag.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import jplag.CommandLineArgument;
import jplag.options.JPlagOptions;
import jplag.strategy.ComparisonMode;

public class ComparisonModeTest extends CommandLineInterfaceTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testDefaultMode() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.DEFAULT_COMPARISON_MODE, options.getComparisonMode());
    }

    @Test
    public void testInvalidMode() {
        exit.expectSystemExitWithStatus(1);
        String argument = buildArgument(CommandLineArgument.COMPARISON_MODE, "Test'); DROP TABLE STUDENTS; --");
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
    }

    @Test
    public void testNormalMode() {
        ComparisonMode mode = ComparisonMode.NORMAL;
        String argument = buildArgument(CommandLineArgument.COMPARISON_MODE, mode.getName());
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(mode, options.getComparisonMode());
    }

    @Test
    public void testParallelMode() {
        ComparisonMode mode = ComparisonMode.PARALLEL;
        String argument = buildArgument(CommandLineArgument.COMPARISON_MODE, mode.getName());
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(mode, options.getComparisonMode());
    }

}
