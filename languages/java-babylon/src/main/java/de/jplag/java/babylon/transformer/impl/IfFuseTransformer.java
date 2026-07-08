package de.jplag.java.babylon.transformer.impl;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;
import de.jplag.java.babylon.transformer.impl.util.YieldTransformer;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeContext;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.JavaType;

/**
 * {@link SimpleTransformation} that fuses unneeded if statements into their parent.
 */
@AutoService(SimpleTransformation.class)
public class IfFuseTransformer implements SimpleTransformation, BabylonDSL {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "if-fuse";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.IfOp ifOp) || ifOp.bodies().size() != 3) {
            builder.add(op);
            return builder;
        }

        Iterator<Body> iterator = ifOp.bodies().iterator();
        Body condition = iterator.next();
        @Nullable
        Boolean constantCondition = constantBooleanValue(condition);
        Body ifTrue = iterator.next();
        Body ifFalse = iterator.next();

        if (constantCondition != null) {
            builder.transformBody(constantCondition ? ifTrue : ifFalse, List.of(), (YieldTransformer) (block, _) -> block);
            return builder;
        } else if (isEmpty(ifTrue)) {
            if (isEmpty(ifFalse)) {
                builder.transformBody(condition, List.of(), (YieldTransformer) (block, _) -> block);
                return builder;
            } else if (isJustIf(ifFalse)) {
                placeFusedIf(builder, condition, true, ifOp, (JavaOp.IfOp) ifFalse.entryBlock().ops().getFirst());
                return builder;
            } else {
                builder.add(op);
                return builder;
            }
        } else if (isEmpty(ifFalse) && isJustIf(ifTrue)) {
            placeFusedIf(builder, condition, false, ifOp, (JavaOp.IfOp) ifTrue.entryBlock().ops().getFirst());
            return builder;
        } else {
            builder.add(op);
            return builder;
        }
    }

    private void placeFusedIf(Block.Builder builder, Body outerCond, boolean flipOuter, JavaOp.IfOp outer, JavaOp.IfOp inner) {
        if (inner.bodies().size() != 3) {
            builder.add(outer);
            return;
        }

        Consumer<Block.Builder> outerCondBuilder = maybeFlip(builder.context(), outerCond, flipOuter);

        Iterator<Body> iterator = inner.bodies().iterator();
        Body condition = iterator.next();
        Body ifTrue = iterator.next();
        Body ifFalse = iterator.next();

        if (isEmpty(ifTrue)) {
            if (isEmpty(ifFalse)) {
                builder.add(outer); // inner will be removed by transform-on-append
            } else {
                place(builder, outer.location(), JavaOp.if_(builder.parentBody()).if_(b -> {
                    Op.Result and = place(b, inner.location(),
                            JavaOp.conditionalAnd(b.parentBody(), outerCondBuilder, maybeFlip(builder.context(), condition, true)).build());
                    place(b, inner.location(), CoreOp.core_yield(and));
                }).then(b -> {
                    b.transformBody(ifFalse, List.of(), builder.context(), CodeTransformer.COPYING_TRANSFORMER);
                }).else_());
            }
        } else if (isEmpty(ifFalse)) {
            place(builder, outer.location(), JavaOp.if_(builder.parentBody()).if_(b -> {
                Op.Result and = place(b, inner.location(),
                        JavaOp.conditionalAnd(b.parentBody(), outerCondBuilder, maybeFlip(builder.context(), condition, false)).build());
                place(b, inner.location(), CoreOp.core_yield(and));
            }).then(b -> {
                b.transformBody(ifTrue, List.of(), builder.context(), CodeTransformer.COPYING_TRANSFORMER);
            }).else_());
        } else {
            builder.add(outer);
        }
    }

    private Consumer<Block.Builder> maybeFlip(CodeContext context, Body body, boolean flip) {
        return builder -> {
            builder.transformBody(body, List.of(), context, flip ? new FlipBoolean() : CodeTransformer.COPYING_TRANSFORMER);
        };
    }

    private static class FlipBoolean implements YieldTransformer, BabylonDSL {
        @Override
        public Block.Builder acceptYield(Block.Builder builder, CoreOp.YieldOp yield) {
            Op.Result flipped = place(builder, yield.location(), JavaOp.not(builder.context().getValue(yield.yieldValue())));
            place(builder, yield.location(), CoreOp.core_yield(flipped));
            return builder;
        }
    }

    private boolean isEmpty(Body body) {
        if (body.blocks().size() != 1) {
            return false;
        }
        Block block = body.entryBlock();
        for (Op op : block.ops()) {
            if (op instanceof Op.Pure) {
                continue;
            } else if (op instanceof CoreOp.YieldOp yieldOp) {
                return yieldOp.operands().isEmpty();
            } else {
                return false;
            }
        }
        throw new IllegalArgumentException("Block does not end in a finishing op");
    }

    private @Nullable Boolean constantBooleanValue(Body body) {
        if (body.blocks().size() != 1) {
            return null;
        }
        Block block = body.entryBlock();
        if (block.ops().size() != 1) {
            return null;
        }
        Iterator<Op> iterator = block.ops().iterator();
        if (!(iterator.next() instanceof CoreOp.ConstantOp constantOp) || constantOp.resultType() != JavaType.BOOLEAN) {
            return null;
        }
        if (!(iterator.next() instanceof CoreOp.YieldOp yieldOp) || yieldOp.operands().size() != 1
                || !yieldOp.operands().getFirst().equals(constantOp.result())) {
            return null;
        }
        return (Boolean) constantOp.value();
    }

    private boolean isJustIf(Body body) {
        if (body.blocks().size() != 1) {
            return false;
        }
        Block block = body.entryBlock();
        if (block.ops().size() != 2) {
            return false;
        }
        Iterator<Op> iterator = block.ops().iterator();
        if (!(iterator.next() instanceof JavaOp.IfOp)) {
            return false;
        }
        return iterator.next() instanceof CoreOp.YieldOp yieldOp && yieldOp.operands().isEmpty();
    }
}
