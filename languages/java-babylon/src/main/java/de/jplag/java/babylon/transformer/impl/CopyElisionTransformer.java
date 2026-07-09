package de.jplag.java.babylon.transformer.impl;

import static de.jplag.java.babylon.BabylonUtils.dominates;
import static de.jplag.java.babylon.BabylonUtils.place;
import static jdk.incubator.code.dialect.core.CoreOp.var;

import java.util.SequencedSet;

import javax.annotation.Nullable;

import de.jplag.java.babylon.transformer.SimpleTransformation;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link SimpleTransformation} that removes unused variable copies.
 */
@AutoService(SimpleTransformation.class)
public class CopyElisionTransformer implements SimpleTransformation {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "copy-elision";

    private final int maxReadUses;

    /**
     * Create a new instance with config options loaded from system properties.
     */
    public CopyElisionTransformer() {
        this(Integer.parseInt(System.getProperty("jplag.java-babylon.copy-elision.max-read-uses", "1")));
    }

    /**
     * Create a new instance.
     * @param maxReadUses the maximum number of reads before a variable is no longer elided
     */
    public CopyElisionTransformer(int maxReadUses) {
        this.maxReadUses = maxReadUses;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        switch (op) {
            case CoreOp.VarOp varOp when onlyWritten(varOp)
                    || (onlyRead(varOp) && !varOp.isUninitialized() && varOp.result().uses().size() <= maxReadUses) -> {
                // This varOp is either only written or only read a few times. Remove it (and adjust/replace usages)
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
                // This varOp is assigned a new value before it is ever read. Remove the init and possibly move it down.
                if (varStoreOp.parent().equals(varOp.parent())) {
                    builder.context().putProperty(varStoreOp, new ReplaceWithVariable(varOp));
                } else {
                    Op.Result replacement = place(builder, varOp.location(), var(varOp.varName(), varOp.varValueType()));
                    builder.context().mapValue(varOp.result(), replacement);
                }
            }
            case CoreOp.VarAccessOp.VarStoreOp varStoreOp when builder.context()
                    .getProperty(varStoreOp) instanceof ReplaceWithVariable(CoreOp.VarOp varOp) -> {
                Op.Result result = place(builder, varStoreOp.location(),
                        var(varOp.varName(), varOp.varValueType(), builder.context().getValue(varStoreOp.storeOperand())));
                builder.context().mapValue(varOp.result(), result);
            }

            case CoreOp.VarAccessOp.VarStoreOp varStoreOp when storedValueUnused(varStoreOp) -> {
                // A value is stored but never read, so the store can be removed (and later DCE can potentially evaluate the
                // computation)
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

    /**
     * Returns the {@link CoreOp.VarAccessOp.VarStoreOp} that is the first use of the variable, if any.<br>
     * Returns null if any operation could potentially access the variable before the store.
     * @param varOp the variable to check
     * @return the replacer, if any
     */
    private @Nullable CoreOp.VarAccessOp.VarStoreOp initialValueReplacer(CoreOp.VarOp varOp) {
        SequencedSet<Op.Result> uses = varOp.result().uses();
        return uses.stream().filter(use -> use.op() instanceof CoreOp.VarAccessOp.VarStoreOp).map(use -> (CoreOp.VarAccessOp.VarStoreOp) use.op())
                .filter(use -> uses.stream().allMatch(otherUse -> use.result() == otherUse || dominates(use, otherUse.op()))).findFirst()
                .orElse(null);
    }
}
