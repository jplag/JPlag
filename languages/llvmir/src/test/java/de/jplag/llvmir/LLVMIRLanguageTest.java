package de.jplag.llvmir;

import static de.jplag.llvmir.LLVMIRTokenAttribute.CATCH_PAD;
import static de.jplag.llvmir.LLVMIRTokenAttribute.CATCH_RETURN;
import static de.jplag.llvmir.LLVMIRTokenAttribute.CATCH_SWITCH;
import static de.jplag.llvmir.LLVMIRTokenAttribute.CLEAN_UP_PAD;
import static de.jplag.llvmir.LLVMIRTokenAttribute.CLEAN_UP_RETURN;

import java.util.Arrays;
import java.util.List;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Provides tests for the llvmir language module
 */
class LLVMIRLanguageTest extends LanguageModuleTest {
    public LLVMIRLanguageTest() {
        super(new LLVMIRLanguage(), LLVMIRTokenAttribute.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        List<LLVMIRTokenAttribute> missingTokens = List.of(CATCH_SWITCH, CATCH_RETURN, CLEAN_UP_RETURN, CATCH_PAD, CLEAN_UP_PAD);
        LLVMIRTokenAttribute[] expectedTokens = Arrays.stream(LLVMIRTokenAttribute.values()).filter(it -> !missingTokens.contains(it))
                .toArray(LLVMIRTokenAttribute[]::new);

        collector.testFile("Complete.ll").testSourceCoverage().testContainedTokens(expectedTokens);

        // Finding an example for the new exception handling instructions was difficult.
        // Therefore, the NewExceptionHandling.ll file can only be parsed and not executed.
        collector.testFile("NewExceptionHandling.ll").testSourceCoverage().testContainedTokens(CATCH_SWITCH, CATCH_RETURN, CLEAN_UP_RETURN, CATCH_PAD,
                CLEAN_UP_PAD);

    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreLinesByPrefix(";");
        collector.ignoreLinesByPrefix("target datalayout");
        collector.ignoreLinesByPrefix("target triple");
        collector.ignoreLinesByPrefix("unreachable");
    }
}