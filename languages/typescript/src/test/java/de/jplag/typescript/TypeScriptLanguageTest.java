package de.jplag.typescript;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;
import de.jplag.typescript.TypeScriptLanguage;
import de.jplag.typescript.TypeScriptTokenType;

public class TypeScriptLanguageTest extends LanguageModuleTest {

    public TypeScriptLanguageTest() {
        super(new TypeScriptLanguage(), TypeScriptTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("forLoops.ts").testTokenSequence(TypeScriptTokenType.ASSIGNMENT, TypeScriptTokenType.FOR_BEGIN, TypeScriptTokenType.FOR_END, TypeScriptTokenType.FOR_BEGIN, TypeScriptTokenType.FOR_END,  TypeScriptTokenType.FOR_BEGIN, TypeScriptTokenType.FOR_END);
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByPrefix("//");
    }
}
