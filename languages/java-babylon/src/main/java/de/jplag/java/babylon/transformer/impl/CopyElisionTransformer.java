package de.jplag.java.babylon.transformer.impl;

import java.util.SequencedSet;

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
                if (!varOp.isUninitialized()) {
                    for (Op.Result use : varOp.result().uses()) {
                        builder.context().putProperty(use.op(), IDENTIFIER);
                    }
                }
            }
            case CoreOp.VarAccessOp varAccessOp when builder.context().getProperty(varAccessOp) == IDENTIFIER -> {
                if (varAccessOp instanceof CoreOp.VarAccessOp.VarLoadOp) {
                    builder.context().mapValue(varAccessOp.result(), builder.context().getValue(varAccessOp.varOp().initOperand()));
                }
            }
            case CoreOp.VarOp varOp when !varOp.isUninitialized() && initialValueIsUnused(varOp) -> {
                Op.Result replacement = place(builder, varOp.location(), CoreOp.var(varOp.varName(), varOp.varValueType()));
                builder.context().mapValue(varOp.result(), replacement);
            }
            case CoreOp.VarAccessOp.VarStoreOp varStoreOp when storedValueUnused(varStoreOp) -> {
            }
            // TODO elide var x; ...; assign x; ...; var y = x; now only use y
            default -> builder.add(op);
        }
        return builder;
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
                .allMatch(use -> varStoreOp.result().isDominatedBy(use));
    }

    private boolean initialValueIsUnused(CoreOp.VarOp varOp) {
        SequencedSet<Op.Result> uses = varOp.result().uses();
        return uses.stream().anyMatch(use -> use.op() instanceof CoreOp.VarAccessOp.VarStoreOp
                && uses.stream().allMatch(otherUse -> use == otherUse || otherUse.isDominatedBy(use)));
    }
}
