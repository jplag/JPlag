package de.jplag.java.babylon.tokenizer;

import java.io.File;

import de.jplag.java.babylon.ParserBabylon;

import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;

/**
 * Encapsulates the logic of converting code models into {@link de.jplag.Token}s passed to a {@link ParserBabylon}.<br>
 * {@link Provider}s are loaded using {@link TokenizerLoader}.
 */
public interface BabylonTokenizer {
    /**
     * Tokenize a single {@link Body}.
     * @param body the body to tokenize
     */
    default void handle(Body body) {
        for (Block block : body.blocks()) {
            handle(block);
        }
    }

    /**
     * Tokenize a single {@link Block}.
     * @param block the block to tokenize
     */
    default void handle(Block block) {
        for (Op op : block.ops()) {
            handle(op);
        }
    }

    /**
     * Tokenize a single {@link Op}.
     * @param op the op to tokenize
     */
    void handle(Op op);

    /**
     * Encapsulates the logic of constructing {@link BabylonTokenizer}s for particular files.<br>
     * Used by {@link TokenizerLoader} to load tokenizers while avoiding global mutable state.
     */
    interface Provider {
        /**
         * @return Identifier of the transformation used for CLI options and dynamic loading. You should use some name within
         * {@code [a-z_-]+}
         */
        String getIdentifier();

        /**
         * Obtain a {@link BabylonTokenizer} for a particular {@link File} and {@link ParserBabylon}, allowing
         * {@link de.jplag.Token}s to be generated from just {@link Op}s.
         * @param parser the parser to bind to
         * @param file the file to bind to
         * @return the bound tokenizer
         */
        BabylonTokenizer getTokenizer(ParserBabylon parser, File file);
    }
}
