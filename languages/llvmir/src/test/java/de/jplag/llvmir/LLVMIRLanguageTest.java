package de.jplag.llvmir;

import static de.jplag.llvmir.LLVMIRTokenType.CATCH_PAD;
import static de.jplag.llvmir.LLVMIRTokenType.CATCH_RETURN;
import static de.jplag.llvmir.LLVMIRTokenType.CATCH_SWITCH;
import static de.jplag.llvmir.LLVMIRTokenType.CLEAN_UP_PAD;
import static de.jplag.llvmir.LLVMIRTokenType.CLEAN_UP_RETURN;

import java.util.Arrays;
import java.util.List;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Provides tests for the llvmir language module.
 */
class LLVMIRLanguageTest extends LanguageModuleTest {

    /**
     * Constructs a LLVM-IR language test module with the appropriate language and token type.
     */
    public LLVMIRLanguageTest() {
        super(new LLVMIRLanguage(), LLVMIRTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        List<LLVMIRTokenType> missingTokens = List.of(CATCH_SWITCH, CATCH_RETURN, CLEAN_UP_RETURN, CATCH_PAD, CLEAN_UP_PAD);
        LLVMIRTokenType[] expectedTokens = Arrays.stream(LLVMIRTokenType.values()).filter(it -> !missingTokens.contains(it))
                .toArray(LLVMIRTokenType[]::new);

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