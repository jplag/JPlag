package de.jplag.x86;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

public class X86_64AssemblerLanguageTest extends LanguageModuleTest {
    public X86_64AssemblerLanguageTest() {
        super(new X86_64AssemblerLanguage(), X86_64AssemblerTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("test.asm");
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
    }
}
