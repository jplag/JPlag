package de.jplag.typescript;

import de.jplag.TokenType;
import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

import java.util.ArrayList;
import java.util.List;

import static de.jplag.typescript.TypeScriptTokenType.ENUM_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.ENUM_END;
import static de.jplag.typescript.TypeScriptTokenType.ENUM_MEMBER;
import static de.jplag.typescript.TypeScriptTokenType.INTERFACE_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.INTERFACE_END;
import static de.jplag.typescript.TypeScriptTokenType.NAMESPACE_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.NAMESPACE_END;

public class JavaScriptLanguageTest extends LanguageModuleTest {

    public JavaScriptLanguageTest() {
        super(new TypeScriptLanguage(), TypeScriptTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        List<TypeScriptTokenType> javascriptTokens = new ArrayList<>(List.of(TypeScriptTokenType.values()));
        javascriptTokens.removeAll(List.of(NAMESPACE_BEGIN, NAMESPACE_END, INTERFACE_BEGIN, INTERFACE_END, ENUM_BEGIN, ENUM_END, ENUM_MEMBER));
        System.out.println(javascriptTokens);
        collector.testFile("allJSTokens.js").testSourceCoverage().testContainedTokens(javascriptTokens.toArray(new TokenType[0]));
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByPrefix("//");
    }
}
