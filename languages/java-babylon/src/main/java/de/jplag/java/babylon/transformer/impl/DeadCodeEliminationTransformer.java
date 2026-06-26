package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link SimpleTransformation} that eliminates ops that are not used.
 */
@AutoService(SimpleTransformation.class)
public class DeadCodeEliminationTransformer implements SimpleTransformation, BabylonDSL {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "dead-code-elimination";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        switch (op) {
            case Op.Pure _,CoreOp.TupleOp _,CoreOp.TupleLoadOp _,CoreOp.TupleWithOp _,CoreOp.VarOp _,CoreOp.VarAccessOp.VarLoadOp _ when unused(
                    op) -> {
            }
            case Op.Loop _ when op.bodies().stream().allMatch(this::isPure) -> {
            }
            default -> builder.add(op);
        }
        return builder;
    }

    private boolean unused(Op op) {
        return op.result().uses().isEmpty();
    }

    private boolean isPure(Body body) {
        return false; // TODO analyze body purity to allow eliding loops
    }
}
