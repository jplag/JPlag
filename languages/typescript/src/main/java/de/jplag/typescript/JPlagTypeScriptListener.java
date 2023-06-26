package de.jplag.typescript;

import de.jplag.typescript.grammar.TypeScriptParserBaseListener;

public class JPlagTypeScriptListener extends TypeScriptParserBaseListener {
    private final Parser parser;
    public JPlagTypeScriptListener(Parser parser) {
        this.parser = parser;
    }

}
