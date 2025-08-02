package de.jplag.python;

import org.junit.jupiter.api.Test;

import io.github.treesitter.jtreesitter.InputEncoding;
import io.github.treesitter.jtreesitter.Language;
import io.github.treesitter.jtreesitter.Node;
import io.github.treesitter.jtreesitter.Parser;
import io.github.treesitter.jtreesitter.Tree;

public class TreeSitterPythonTest {
    @Test
    public void testTreeSitterPythonIntegration() {
        Language language = new Language(TreeSitterPython.language());
        try (Parser parser = new Parser(language)) {
            try (Tree tree = parser.parse("def main():", InputEncoding.UTF_8).orElseThrow()) {
                Node rootNode = tree.getRootNode();
                assert rootNode.getType().equals("module");
                assert rootNode.getStartPoint().column() == 0;
                assert rootNode.getEndPoint().column() == 11;
            }
        }
    }
}
