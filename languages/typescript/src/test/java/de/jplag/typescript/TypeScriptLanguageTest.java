package de.jplag.typescript;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Unit test for the TypeScript language module, verifying tokenization and source coverage. Extends
 * {@link LanguageModuleTest} to provide TypeScript-specific test files, token sequences, and rules for ignoring
 * irrelevant source lines such as comments and multi-line blocks.
 */
public class TypeScriptLanguageTest extends LanguageModuleTest {

    /**
     * Constructs a TypeScript language test module with the appropriate language and token type.
     */
    public TypeScriptLanguageTest() {
        super(new TypeScriptLanguage(), TypeScriptTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("simpleTest.ts").testSourceCoverage().testTokenSequence(TypeScriptTokenType.DECLARATION, TypeScriptTokenType.ASSIGNMENT,
                TypeScriptTokenType.DECLARATION, TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.FOR_BEGIN, TypeScriptTokenType.ASSIGNMENT,
                TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.FUNCTION_CALL, TypeScriptTokenType.FOR_END, TypeScriptTokenType.DECLARATION,
                TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.FUNCTION_CALL, TypeScriptTokenType.ASSIGNMENT);
        collector.testFile("forLoops.ts").testTokenSequence(TypeScriptTokenType.DECLARATION, TypeScriptTokenType.ASSIGNMENT,
                TypeScriptTokenType.FOR_BEGIN, TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.FUNCTION_CALL,
                TypeScriptTokenType.FOR_END, TypeScriptTokenType.FOR_BEGIN, TypeScriptTokenType.FUNCTION_CALL, TypeScriptTokenType.FOR_END,
                TypeScriptTokenType.FOR_BEGIN, TypeScriptTokenType.FUNCTION_CALL, TypeScriptTokenType.FOR_END);
        collector.testFile("methods.ts").testTokenSequence(TypeScriptTokenType.DECLARATION, TypeScriptTokenType.ASSIGNMENT,
                TypeScriptTokenType.METHOD_BEGIN, TypeScriptTokenType.RETURN, TypeScriptTokenType.METHOD_END, TypeScriptTokenType.DECLARATION,
                TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.METHOD_BEGIN, TypeScriptTokenType.RETURN, TypeScriptTokenType.METHOD_END,
                TypeScriptTokenType.DECLARATION, TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.METHOD_BEGIN, TypeScriptTokenType.RETURN,
                TypeScriptTokenType.METHOD_END);
        collector.testFile("class.ts").testSourceCoverage().testTokenSequence(TypeScriptTokenType.CLASS_BEGIN, TypeScriptTokenType.DECLARATION,
                TypeScriptTokenType.DECLARATION, TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.CONSTRUCTOR_BEGIN,
                TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.CONSTRUCTOR_END, TypeScriptTokenType.METHOD_BEGIN, TypeScriptTokenType.RETURN,
                TypeScriptTokenType.METHOD_END, TypeScriptTokenType.METHOD_BEGIN, TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.METHOD_END,
                TypeScriptTokenType.METHOD_BEGIN, TypeScriptTokenType.RETURN, TypeScriptTokenType.METHOD_END, TypeScriptTokenType.METHOD_BEGIN,
                TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.METHOD_END, TypeScriptTokenType.CLASS_END);
        collector.testFile("if.ts").testSourceCoverage().testTokenSequence(TypeScriptTokenType.IF_BEGIN, TypeScriptTokenType.FUNCTION_CALL,
                TypeScriptTokenType.IF_BEGIN, TypeScriptTokenType.IF_BEGIN, TypeScriptTokenType.FUNCTION_CALL, TypeScriptTokenType.IF_BEGIN,
                TypeScriptTokenType.FUNCTION_CALL, TypeScriptTokenType.IF_END, TypeScriptTokenType.IF_END, TypeScriptTokenType.IF_BEGIN,
                TypeScriptTokenType.FUNCTION_CALL, TypeScriptTokenType.IF_END);
        collector.testFile("allTokens.ts").testCoverages();
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByPrefix("//");
    }
}
