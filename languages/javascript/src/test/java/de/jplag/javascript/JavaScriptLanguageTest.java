package de.jplag.javascript;

import static de.jplag.typescript.TypeScriptTokenAttribute.ENUM_BEGIN;
import static de.jplag.typescript.TypeScriptTokenAttribute.ENUM_END;
import static de.jplag.typescript.TypeScriptTokenAttribute.ENUM_MEMBER;
import static de.jplag.typescript.TypeScriptTokenAttribute.INTERFACE_BEGIN;
import static de.jplag.typescript.TypeScriptTokenAttribute.INTERFACE_END;
import static de.jplag.typescript.TypeScriptTokenAttribute.NAMESPACE_BEGIN;
import static de.jplag.typescript.TypeScriptTokenAttribute.NAMESPACE_END;

import java.util.ArrayList;
import java.util.List;

import de.jplag.TokenAttribute;
import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;
import de.jplag.typescript.TypeScriptTokenAttribute;

public class JavaScriptLanguageTest extends LanguageModuleTest {

    public JavaScriptLanguageTest() {
        super(new JavaScriptLanguage(), TypeScriptTokenAttribute.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        List<TypeScriptTokenAttribute> javascriptTokens = new ArrayList<>(List.of(TypeScriptTokenAttribute.values()));
        javascriptTokens.removeAll(List.of(NAMESPACE_BEGIN, NAMESPACE_END, INTERFACE_BEGIN, INTERFACE_END, ENUM_BEGIN, ENUM_END, ENUM_MEMBER));
        collector.testFile("allJSTokens.js").testSourceCoverage().testContainedTokens(javascriptTokens.toArray(new TokenAttribute[0]));
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByPrefix("//");
    }
}
