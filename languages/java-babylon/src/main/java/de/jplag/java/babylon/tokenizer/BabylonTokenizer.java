package de.jplag.java.babylon.tokenizer;

import de.jplag.java.babylon.ParserBabylon;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;

import java.io.File;

/**
 * Encapsulates the logic of converting code models into {@link de.jplag.Token}s passed to a {@link ParserBabylon}.
 */
public interface BabylonTokenizer {
    /**
     * @return Identifier of the transformation used for CLI options and dynamic loading. You should use some name within
     * {@code [a-z_-]+}
     */
    String getIdentifier();

    /**
     * Bind to a particular {@link File} and {@link ParserBabylon}, allowing {@link de.jplag.Token}s to be generated from just {@link Op}s.
     *
     * @param parser the parser to bind to
     * @param file the file to bind to
     * @return the bound tokenizer
     */
    AtFile atFile(ParserBabylon parser, File file);

    /**
     * Tokenizer bound to a particular file, defaulting to that file for output {@link de.jplag.Token}s.
     */
    interface AtFile {
        /**
         * Tokenize a single {@link Body}.
         *
         * @param body the body to tokenize
         */
        default void handle(Body body) {
            for (Block block : body.blocks()) {
                handle(block);
            }
        }

        /**
         * Tokenize a single {@link Block}.
         *
         * @param block the block to tokenize
         */
        default void handle(Block block) {
            for (Op op : block.ops()) {
                handle(op);
            }
        }

        /**
         * Tokenize a single {@link Value}.
         *
         * @param value the value to tokenize
         */
        default void handle(Value value) {
            switch (value) {
                case null -> {}
                case Block.Parameter _ -> {}
                case Op.Result result -> handle(result.op());
            }
        }

        /**
         * Tokenize a single {@link Op}.
         *
         * @param op the op to tokenize
         */
        void handle(Op op);
    }
}
