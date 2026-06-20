package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.transformer.SimpleTransformation;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.java.JavaOp;

/**
 * {@link SimpleTransformation} that removes asserts for environments where asserts are disabled.
 */
@AutoService(SimpleTransformation.class)
public class AssertRemoveTransformer implements SimpleTransformation {
    @Override
    public String getIdentifier() {
        return "assert-remove";
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.AssertOp)) {
            builder.add(op);
        }
        return builder;
    }
}
