package de.jplag.java;

import jdk.incubator.code.*;
import jdk.incubator.code.dialect.core.CoreOp;

import static de.jplag.java.Experiment.requireSingle;
import static jdk.incubator.code.dialect.java.JavaType.VOID;

public interface BabylonDSL {
    default void copy(Body from, Block.Builder to) {
        // this implicitly clears out the parameters which we depend on
        for (Op op1 : requireSingle(from.blocks()).ops()) {
            if (isLocationMarker(op1)) continue;
            to.op(op1);
        }
    }

    default Op locationMarker(Op.Location location) {
        var op = CoreOp.constant(VOID, null);
        op.setLocation(location);
        return op;
    }

    default boolean isLocationMarker(Op op) {
        if (!(op instanceof CoreOp.ConstantOp cnst)) return false;
        if (cnst.resultType() != VOID) return false;
        if (cnst.value() != null) return false;
        return true;
    }

    default Op.Location location(Body body) {
        if (body == null) return null;
        for (Block block : body.blocks()) {
            var location = location(block);
            if (location != null) return location;
        }
        return null;
    }

    default Op.Location location(Block block) {
        for (Op op : block.ops()) {
            var location = op.location();
            if (location != null) return location;
        }
        return null;
    }

    default Op.Location location(CodeElement ce) {
        return switch (ce) {
            case Block block -> location(block);
            case Body body -> location(body);
            case Op op -> op.location();
        };
    }

    default Op.Location location(Value value) {
        return location(value.declaringElement());
    }

    default Op.Result place(Block.Builder bd, Op.Location location, Op op) {
        op.setLocation(location);
        return bd.op(op);
    }
}
