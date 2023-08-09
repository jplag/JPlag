package de.jplag.llvmir;

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
        collector.testFile("Complete.ll").testSourceCoverage().testContainedTokens(LLVMIRTokenType.FILENAME, LLVMIRTokenType.FUNCTION_BODY_BEGIN,
                LLVMIRTokenType.FUNCTION_BODY_END, LLVMIRTokenType.BASIC_BLOCK_BEGIN, LLVMIRTokenType.BASIC_BLOCK_END,
                LLVMIRTokenType.FUNCTION_DECLARATION, LLVMIRTokenType.FUNCTION_DEFINITION, LLVMIRTokenType.GLOBAL_VARIABLE, LLVMIRTokenType.ASSEMBLY,
                LLVMIRTokenType.TYPE_DEFINITION, LLVMIRTokenType.STRUCTURE, LLVMIRTokenType.ARRAY, LLVMIRTokenType.VECTOR, LLVMIRTokenType.RETURN,
                LLVMIRTokenType.BRANCH, LLVMIRTokenType.SWITCH, LLVMIRTokenType.CASE, LLVMIRTokenType.CONDITIONAL_BRANCH, LLVMIRTokenType.INVOKE,
                LLVMIRTokenType.CALL_BRANCH, LLVMIRTokenType.RESUME, LLVMIRTokenType.ADDITION, LLVMIRTokenType.SUBTRACTION,
                LLVMIRTokenType.MULTIPLICATION, LLVMIRTokenType.DIVISION, LLVMIRTokenType.REMAINDER, LLVMIRTokenType.SHIFT, LLVMIRTokenType.AND,
                LLVMIRTokenType.OR, LLVMIRTokenType.XOR, LLVMIRTokenType.EXTRACT_ELEMENT, LLVMIRTokenType.INSERT_ELEMENT,
                LLVMIRTokenType.SHUFFLE_VECTOR, LLVMIRTokenType.EXTRACT_VALUE, LLVMIRTokenType.INSERT_VALUE, LLVMIRTokenType.ALLOCATION,
                LLVMIRTokenType.LOAD, LLVMIRTokenType.STORE, LLVMIRTokenType.FENCE, LLVMIRTokenType.COMPARE_EXCHANGE,
                LLVMIRTokenType.ATOMIC_READ_MODIFY_WRITE, LLVMIRTokenType.ATOMIC_ORDERING, LLVMIRTokenType.GET_ELEMENT_POINTER,
                LLVMIRTokenType.BITCAST, LLVMIRTokenType.CONVERSION, LLVMIRTokenType.COMPARISON, LLVMIRTokenType.PHI, LLVMIRTokenType.SELECT,
                LLVMIRTokenType.CALL, LLVMIRTokenType.VARIABLE_ARGUMENT, LLVMIRTokenType.LANDING_PAD, LLVMIRTokenType.CLAUSE);
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