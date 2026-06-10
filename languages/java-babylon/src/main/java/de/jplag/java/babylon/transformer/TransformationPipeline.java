package de.jplag.java.babylon.transformer;

import com.sun.source.tree.TreeVisitor;
import de.jplag.java.babylon.MulticastTreeVisitor;
import jdk.incubator.code.dialect.core.CoreOp;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class TransformationPipeline {
    private final List<Step> steps;

    public TransformationPipeline(List<Step> steps) {
        this.steps = List.copyOf(steps);
    }

    public TreeVisitor<?, ?> prepass() {
        List<TreeVisitor<?, ?>> visitors = new ArrayList<>();
        for (Step step : steps) {
            TreeVisitor<?, ?> visitor = step.beginPrepass();
            if (visitor != null) {
                visitors.add(visitor);
            }
        }
        return MulticastTreeVisitor.create(visitors);
    }

    public CoreOp.FuncOp transform(CoreOp.FuncOp op) {
        for (Step step : steps) {
            op = step.apply(op);
        }
        return op;
    }

    public interface Step {
        String getIdentifier();

        default @Nullable TreeVisitor<?, ?> beginPrepass() {
            return null;
        }
        CoreOp.FuncOp apply(CoreOp.FuncOp op);
    }
}
