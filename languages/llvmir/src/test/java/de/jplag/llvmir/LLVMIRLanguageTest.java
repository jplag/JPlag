package de.jplag.llvmir;

import org.junit.jupiter.api.Disabled;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Provides tests for the llvmir language module
 */
@Disabled
class LLVMIRLanguageTest extends LanguageModuleTest {
    public LLVMIRLanguageTest() {
        super(new LLVMIRLanguage(), LLVMIRTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("Complete.ll").testCoverages();
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        // TODO
    }
}