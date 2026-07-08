package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.transformer.SimpleTransformation;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;

/**
 * {@link SimpleTransformation} that eliminates ops that are not used.
 */
@AutoService(SimpleTransformation.class)
public class DeadCodeEliminationTransformer implements SimpleTransformation {
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
        if (!canBeSkipped(op)) {
            builder.add(op);
        }
        return builder;
    }

    private boolean unused(Op op) {
        return op.result().uses().isEmpty();
    }

    private boolean canBeSkipped(Op op) {
        // tuple ops do not occur naturally, so we can safely assume that they are correct
        return switch (op) {
            case Op.Pure _,CoreOp.TupleOp _,CoreOp.TupleLoadOp _,CoreOp.TupleWithOp _,CoreOp.VarOp _,CoreOp.VarAccessOp.VarLoadOp _,JavaOp.LambdaOp _ when unused(
                    op) -> true;
            case JavaOp.ForOp forOp when canBeSkipped(forOp) -> true;
            case Op.Loop loop when op.bodies().stream()
                    .allMatch(body -> isPure(body, body == loop.loopBody() ? ScopeKind.LOOP : ScopeKind.NONE)) -> true;
            case JavaOp.IfOp _ when op.bodies().stream().allMatch(body -> isPure(body, ScopeKind.NONE)) -> true;
            default -> false;
        };
    }

    private boolean canBeSkipped(JavaOp.ForOp forOp) {
        return isPure(forOp.initBody(), ScopeKind.NONE) && isPure(forOp.condBody(), ScopeKind.NONE)
                && isPure(forOp.updateBody(), ScopeKind.FOR_UPDATE) && isPure(forOp.loopBody(), ScopeKind.LOOP);
    }

    private boolean isLocal(Value value, Block block) {
        return switch (value) {
            case Block.Parameter parameter -> block.parameters().contains(parameter);
            case Op.Result result when result.op() instanceof CoreOp.VarOp _ -> result.declaringBlock() == block;
            case Op.Result result when result.op() instanceof Op.Pure _ || result.op() instanceof CoreOp.TupleOp _
                    || result.op() instanceof CoreOp.TupleLoadOp || result.op() instanceof CoreOp.TupleWithOp -> result.op().operands().stream()
                            .allMatch(v -> isLocal(v, block));
            default -> false;
        };
    }

    private boolean isPure(Body body, ScopeKind scopeKind) {
        for (Block block : body.blocks()) {
            for (Op op : block.ops()) {
                switch (op) {
                    case Op.Pure _,CoreOp.TupleOp _,CoreOp.TupleLoadOp _,CoreOp.TupleWithOp _,CoreOp.VarOp _,CoreOp.VarAccessOp.VarLoadOp _,JavaOp.LambdaOp _,CoreOp.YieldOp _ -> {
                    }
                    case CoreOp.VarAccessOp.VarStoreOp varStoreOp -> {
                        switch (varStoreOp.varOperand()) {
                            case Block.Parameter param -> {
                                if (scopeKind != ScopeKind.FOR_UPDATE || !isLocal(param, block))
                                    return false;
                            }
                            case Op.Result result -> {
                                if (!isLocal(result, block))
                                    return false;
                            }
                        }
                    }
                    case Op.Loop loop when op.bodies().stream()
                            .allMatch(body1 -> isPure(body1, body1 == loop.loopBody() ? ScopeKind.LOOP : ScopeKind.NONE)) -> {
                        // Deliberately not isPure(..., loopBody || ...) since that could hide behavior
                    }
                    case JavaOp.IfOp _ when op.bodies().stream().allMatch(body1 -> isPure(body1, scopeKind)) -> {
                    }
                    case JavaOp.ContinueOp _,JavaOp.BreakOp _ when scopeKind == ScopeKind.LOOP -> {
                    }
                    default -> {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private enum ScopeKind {
        LOOP,
        FOR_UPDATE,
        NONE;
    }
}
