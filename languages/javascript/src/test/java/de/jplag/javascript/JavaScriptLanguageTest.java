package de.jplag.javascript;

import static de.jplag.typescript.TypeScriptTokenType.ENUM_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.ENUM_END;
import static de.jplag.typescript.TypeScriptTokenType.ENUM_MEMBER;
import static de.jplag.typescript.TypeScriptTokenType.INTERFACE_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.INTERFACE_END;
import static de.jplag.typescript.TypeScriptTokenType.NAMESPACE_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.NAMESPACE_END;

import java.util.ArrayList;
import java.util.List;

import de.jplag.TokenType;
import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;
import de.jplag.typescript.TypeScriptTokenType;

/**
 * Unit test for the JavaScript language module, verifying tokenization and source coverage. Extends
 * {@link LanguageModuleTest} to provide JavaScript-specific test files, filtered token sequences, and rules for
 * ignoring irrelevant source lines such as comments and multi-line blocks.
 */

public class JavaScriptLanguageTest extends LanguageModuleTest {

    /**
     * Constructs a JavaScript language test module with the appropriate language and token type.
     */
    public JavaScriptLanguageTest() {
        super(new JavaScriptLanguage(), TypeScriptTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        List<TypeScriptTokenType> javascriptTokens = new ArrayList<>(List.of(TypeScriptTokenType.values()));
        javascriptTokens.removeAll(List.of(NAMESPACE_BEGIN, NAMESPACE_END, INTERFACE_BEGIN, INTERFACE_END, ENUM_BEGIN, ENUM_END, ENUM_MEMBER));
        collector.testFile("allJSTokens.js").testSourceCoverage().testContainedTokens(javascriptTokens.toArray(new TokenType[0]));
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByPrefix("//");
    }
}
