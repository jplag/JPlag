package de.jplag.java.babylon.transformer;

import java.util.List;

import de.jplag.java.babylon.MulticastTreeVisitor;

import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SimpleTreeVisitor;

/**
 * Represents an operation to be performed before the main pass
 * ({@link de.jplag.java.babylon.TokenGeneratingTreeScannerBabylon}) is performed.
 * @param <Context> the type of the returned context
 */
public interface Prepass<Context> extends TreeVisitor<Void, Void> {
    /**
     * Build a final context object to be consumed by the main pass.
     * @return the built context
     */
    Context finalizeContext();

    /**
     * {@link Prepass} based on {@link MulticastTreeVisitor} that delegates to several {@link Prepass} implementations.
     */
    final class Multicast extends MulticastTreeVisitor implements Prepass<Multicast.Context> {
        private final Object tag;

        /**
         * Create a new instance.
         * @param targets the targets to which to broadcast visits
         * @param tag an object to be attached to any resulting contexts for identification
         */
        public Multicast(List<Prepass<?>> targets, Object tag) {
            super(targets);
            this.tag = tag;
        }

        @Override
        public Context finalizeContext() {
            List<?> contexts = targets.stream().map(s -> ((Prepass<?>) s).finalizeContext()).toList();
            return new Context(contexts, tag);
        }

        /**
         * Context implementation used by {@link Prepass.Multicast}.
         * @param contexts the individual contexts, one for each target
         * @param tag the tag passed to the {@link Multicast} constructor
         */
        public record Context(List<?> contexts, Object tag) {
        }
    }

    /**
     * {@link Prepass} implementation that does no actual work, for use in simple transformations.
     */
    class Unit extends SimpleTreeVisitor<Void, Void> implements Prepass<Void> {
        /**
         * Instance of this singleton.
         */
        public static final Unit INSTANCE = new Unit();

        private Unit() {
        }

        @Override
        public Void finalizeContext() {
            return null;
        }
    }
}
