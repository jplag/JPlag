package de.jplag.typescript;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

public class TypeScriptLanguageTest extends LanguageModuleTest {

    public TypeScriptLanguageTest() {
        super(new TypeScriptLanguage(), TypeScriptTokenAttribute.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("simpleTest.ts").testSourceCoverage().testTokenSequence(TypeScriptTokenAttribute.DECLARATION,
                TypeScriptTokenAttribute.ASSIGNMENT, TypeScriptTokenAttribute.DECLARATION, TypeScriptTokenAttribute.ASSIGNMENT,
                TypeScriptTokenAttribute.FOR_BEGIN, TypeScriptTokenAttribute.ASSIGNMENT, TypeScriptTokenAttribute.ASSIGNMENT,
                TypeScriptTokenAttribute.FUNCTION_CALL, TypeScriptTokenAttribute.FOR_END, TypeScriptTokenAttribute.DECLARATION,
                TypeScriptTokenAttribute.ASSIGNMENT, TypeScriptTokenAttribute.FUNCTION_CALL, TypeScriptTokenAttribute.ASSIGNMENT);
        collector.testFile("forLoops.ts").testTokenSequence(TypeScriptTokenAttribute.DECLARATION, TypeScriptTokenAttribute.ASSIGNMENT,
                TypeScriptTokenAttribute.FOR_BEGIN, TypeScriptTokenAttribute.ASSIGNMENT, TypeScriptTokenAttribute.ASSIGNMENT,
                TypeScriptTokenAttribute.FUNCTION_CALL, TypeScriptTokenAttribute.FOR_END, TypeScriptTokenAttribute.FOR_BEGIN,
                TypeScriptTokenAttribute.FUNCTION_CALL, TypeScriptTokenAttribute.FOR_END, TypeScriptTokenAttribute.FOR_BEGIN,
                TypeScriptTokenAttribute.FUNCTION_CALL, TypeScriptTokenAttribute.FOR_END);
        collector.testFile("methods.ts").testTokenSequence(TypeScriptTokenAttribute.DECLARATION, TypeScriptTokenAttribute.ASSIGNMENT,
                TypeScriptTokenAttribute.METHOD_BEGIN, TypeScriptTokenAttribute.RETURN, TypeScriptTokenAttribute.METHOD_END,
                TypeScriptTokenAttribute.DECLARATION, TypeScriptTokenAttribute.ASSIGNMENT, TypeScriptTokenAttribute.METHOD_BEGIN,
                TypeScriptTokenAttribute.RETURN, TypeScriptTokenAttribute.METHOD_END, TypeScriptTokenAttribute.DECLARATION,
                TypeScriptTokenAttribute.ASSIGNMENT, TypeScriptTokenAttribute.METHOD_BEGIN, TypeScriptTokenAttribute.RETURN,
                TypeScriptTokenAttribute.METHOD_END);
        collector.testFile("class.ts").testSourceCoverage().testTokenSequence(TypeScriptTokenAttribute.CLASS_BEGIN,
                TypeScriptTokenAttribute.DECLARATION, TypeScriptTokenAttribute.DECLARATION, TypeScriptTokenAttribute.ASSIGNMENT,
                TypeScriptTokenAttribute.CONSTRUCTOR_BEGIN, TypeScriptTokenAttribute.ASSIGNMENT, TypeScriptTokenAttribute.CONSTRUCTOR_END,
                TypeScriptTokenAttribute.METHOD_BEGIN, TypeScriptTokenAttribute.RETURN, TypeScriptTokenAttribute.METHOD_END,
                TypeScriptTokenAttribute.METHOD_BEGIN, TypeScriptTokenAttribute.ASSIGNMENT, TypeScriptTokenAttribute.METHOD_END,
                TypeScriptTokenAttribute.METHOD_BEGIN, TypeScriptTokenAttribute.RETURN, TypeScriptTokenAttribute.METHOD_END,
                TypeScriptTokenAttribute.METHOD_BEGIN, TypeScriptTokenAttribute.ASSIGNMENT, TypeScriptTokenAttribute.METHOD_END,
                TypeScriptTokenAttribute.CLASS_END);
        collector.testFile("if.ts").testSourceCoverage().testTokenSequence(TypeScriptTokenAttribute.IF_BEGIN, TypeScriptTokenAttribute.FUNCTION_CALL,
                TypeScriptTokenAttribute.IF_BEGIN, TypeScriptTokenAttribute.IF_BEGIN, TypeScriptTokenAttribute.FUNCTION_CALL,
                TypeScriptTokenAttribute.IF_BEGIN, TypeScriptTokenAttribute.FUNCTION_CALL, TypeScriptTokenAttribute.IF_END,
                TypeScriptTokenAttribute.IF_END, TypeScriptTokenAttribute.IF_BEGIN, TypeScriptTokenAttribute.FUNCTION_CALL,
                TypeScriptTokenAttribute.IF_END);
        collector.testFile("allTokens.ts").testCoverages();
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByPrefix("//");
    }
}
