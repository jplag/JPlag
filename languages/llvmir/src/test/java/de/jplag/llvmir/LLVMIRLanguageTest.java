package de.jplag.llvmir;

import static de.jplag.llvmir.LLVMIRTokenType.*;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Provides tests for the llvmir language module
 */
class LLVMIRLanguageTest extends LanguageModuleTest {
    public LLVMIRLanguageTest() {
        super(new LLVMIRLanguage(), LLVMIRTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        // All Tokens except: [CATCH_SWITCH, CATCH_RETURN, CLEAN_UP_RETURN, CATCH_PAD, CLEAN_UP_PAD]
        collector.testFile("Complete.ll").testSourceCoverage().testContainedTokens(FILENAME, FUNCTION_BODY_BEGIN, FUNCTION_BODY_END,
                BASIC_BLOCK_BEGIN, BASIC_BLOCK_END, FUNCTION_DECLARATION, FUNCTION_DEFINITION, GLOBAL_VARIABLE, ASSEMBLY, TYPE_DEFINITION, STRUCTURE,
                ARRAY, VECTOR, RETURN, BRANCH, SWITCH, CASE, CONDITIONAL_BRANCH, INVOKE, CALL_BRANCH, RESUME, ADDITION, SUBTRACTION, MULTIPLICATION,
                DIVISION, REMAINDER, SHIFT, AND, OR, XOR, EXTRACT_ELEMENT, INSERT_ELEMENT, SHUFFLE_VECTOR, EXTRACT_VALUE, INSERT_VALUE, ALLOCATION,
                LOAD, STORE, FENCE, COMPARE_EXCHANGE, ATOMIC_READ_MODIFY_WRITE, ATOMIC_ORDERING, GET_ELEMENT_POINTER, BITCAST, CONVERSION, COMPARISON,
                PHI, SELECT, CALL, VARIABLE_ARGUMENT, LANDING_PAD, CLAUSE);

        // Finding an example for the new exception handling instructions was difficult.
        // Therefore, the NewExceptionHandling.ll file can only be parsed and not executed.
        collector.testFile("NewExceptionHandling.ll").testSourceCoverage().testContainedTokens(CATCH_SWITCH, CATCH_RETURN, CLEAN_UP_RETURN, CATCH_PAD,
                CLEAN_UP_PAD);

    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreLinesByPrefix("; ModuleID");
        collector.ignoreLinesByPrefix("target datalayout");
        collector.ignoreLinesByPrefix("target triple");
        collector.ignoreLinesByPrefix("; Function Attrs:");
        collector.ignoreLinesByPrefix("unreachable");
    }
}