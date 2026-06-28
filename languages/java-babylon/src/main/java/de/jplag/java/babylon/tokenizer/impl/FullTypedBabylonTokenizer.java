package de.jplag.java.babylon.tokenizer.impl;

import java.io.File;
import java.lang.constant.ClassDesc;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.jplag.TokenType;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;

import com.google.auto.service.AutoService;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import jdk.incubator.code.CodeType;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.java.JavaType;

/**
 * {@link BabylonTokenizer} implementation that fully outputs all {@link Op}s as tokens without further interpretation.
 * Includes result types.
 */
public class FullTypedBabylonTokenizer extends FullBabylonTokenizer {
    /**
     * Identifier of this tokenizer.
     */
    public static final String IDENTIFIER = "full-typed";

    /**
     * Create a new instance.
     * @param parser the parser to output to
     * @param file the current file
     */
    public FullTypedBabylonTokenizer(ParserBabylon parser, File file) {
        super(parser, file);
    }

    @Override
    protected TokenType getTokenType(Op op) {
        StringBuilder sb = new StringBuilder();
        if (op.parent() != null) {
            Op.Result opr = op.result();
            if (!opr.type().equals(JavaType.VOID)) {
                sb.append(opr.type().externalize()).append(" = ");
            }
        }
        sb.append(op.externalizeOpName());
        return new UnknownTokenType(sb.toString());
    }

    /**
     * {@link BabylonTokenizer.Provider} for {@link FullTypedBabylonTokenizer}.
     */
    @AutoService(BabylonTokenizer.Provider.class)
    public static class Provider extends FullBabylonTokenizer.Provider {
        /**
         * Create a new instance.
         */
        public Provider() {
            super(IDENTIFIER);
        }

        @Override
        public BabylonTokenizer getTokenizer(TokenizerConstructionContext context) {
            return new FullTypedBabylonTokenizer(context.parser(), context.file());
        }
    }
}
