package de.jplag.java.babylon.transformer.impl;

import java.util.SequencedSet;

import javax.annotation.Nullable;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link SimpleTransformation} that removes unused constants or variable copies.
 */
@AutoService(SimpleTransformation.class)
public class CopyElisionTransformer implements SimpleTransformation, BabylonDSL {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "copy-elision";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        switch (op) {
            case CoreOp.VarOp varOp when onlyWritten(varOp) || onlyRead(varOp) -> {
                for (Op.Result use : varOp.result().uses()) {
                    builder.context().putProperty(use.op(), IDENTIFIER);
                }
            }
            case CoreOp.VarAccessOp varAccessOp when builder.context().getProperty(varAccessOp) == IDENTIFIER -> {
                if (!varAccessOp.result().uses().isEmpty()) {
                    if (varAccessOp instanceof CoreOp.VarAccessOp.VarLoadOp) {
                        builder.context().mapValue(varAccessOp.result(), builder.context().getValue(varAccessOp.varOp().initOperand()));
                    } else {
                        throw new IllegalStateException("Store ops should not have uses");
                    }
                }
            }
            case CoreOp.VarOp varOp when initialValueReplacer(varOp) instanceof CoreOp.VarAccessOp.VarStoreOp varStoreOp -> {
                if (varOp.isUninitialized()) {
                    builder.context().putProperty(varStoreOp, new ReplaceWithVariable(varOp));
                } else {
                    Op.Result replacement = place(builder, varOp.location(), CoreOp.var(varOp.varName(), varOp.varValueType()));
                    builder.context().mapValue(varOp.result(), replacement);
                }
            }
            case CoreOp.VarAccessOp.VarStoreOp varStoreOp when builder.context()
                    .getProperty(varStoreOp) instanceof ReplaceWithVariable(CoreOp.VarOp varOp) -> {
                Op.Result result = place(builder, varStoreOp.location(),
                        CoreOp.var(varOp.varName(), varOp.varValueType(), builder.context().getValue(varStoreOp.storeOperand())));
                builder.context().mapValue(varOp.result(), result);
            }
            case CoreOp.VarAccessOp.VarStoreOp varStoreOp when storedValueUnused(varStoreOp) -> {
            }
            // TODO elide var x; ...; assign x; ...; var y = x; now only use y
            default -> builder.add(op);
        }
        return builder;
    }

    private record ReplaceWithVariable(CoreOp.VarOp varOp) {
    }

    private boolean onlyWritten(CoreOp.VarOp varOp) {
        return varOp.result().uses().stream().allMatch(use -> use.op() instanceof CoreOp.VarAccessOp.VarStoreOp);
    }

    private boolean onlyRead(CoreOp.VarOp varOp) {
        return varOp.result().uses().stream().allMatch(use -> use.op() instanceof CoreOp.VarAccessOp.VarLoadOp);
    }

    private boolean storedValueUnused(CoreOp.VarAccessOp.VarStoreOp varStoreOp) {
        if (!(varStoreOp.varOperand() instanceof Op.Result variable)) {
            // Cannot be the variable declaration, so we can't check all loads
            return false;
        }
        if (!(variable.op() instanceof CoreOp.VarOp)) {
            // Maybe unpacked from a tuple? Either way, we can't check all loads
            return false;
        }
        return variable.uses().stream().filter(use -> !(use.op() instanceof CoreOp.VarAccessOp.VarStoreOp))
                .allMatch(use -> dominates(use.op(), varStoreOp));
    }

    private @Nullable CoreOp.VarAccessOp.VarStoreOp initialValueReplacer(CoreOp.VarOp varOp) {
        SequencedSet<Op.Result> uses = varOp.result().uses();
        return uses.stream()
                .filter(use -> use.op() instanceof CoreOp.VarAccessOp.VarStoreOp
                        && uses.stream().allMatch(otherUse -> use == otherUse || dominates(use.op(), otherUse.op())))
                .findFirst().map(use -> (CoreOp.VarAccessOp.VarStoreOp) use.op()).orElse(null);
    }
}
