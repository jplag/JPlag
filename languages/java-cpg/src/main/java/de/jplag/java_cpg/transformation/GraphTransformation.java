package de.jplag.java_cpg.transformation;

import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nthElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.*;
import de.jplag.java_cpg.transformation.matching.pattern.SimpleGraphPattern;
import de.jplag.java_cpg.transformation.operations.*;

/**
 * This saves all information related to a transformation on a graph. Note that the source and target patterns have to
 * have compatible root types, otherwise the transformed graph may not be semantically correct.
 * @param <T> the common root {@link Node} type of the source and target {@link NodePattern}s.
 */
public interface GraphTransformation<T extends Node> {

    /**
     * Applies the transformation to the Graph represented by the given {@link Match} which indicates which {@link Node}s
     * shall be involved in the transformation.
     * @param match the match of this {@link GraphTransformation}'s source pattern to a concrete graph
     */
    void apply(Match match, TranslationContext ctx);

    GraphPattern getSourcePattern();

    GraphPattern getTargetPattern();

    ExecutionPhase getPhase();

    String getName();

    ExecutionOrder getExecutionOrder();

    enum ExecutionPhase {

        /**
         * Executes right after the construction of the AST, to ensure its well-formedness.
         */
        OBLIGATORY(false),

        /**
         * Executes before the EOG is constructed. Used for AST-altering transformations.
         */
        AST_TRANSFORM(false),
        /**
         * Executes after the EOG is constructed, right before the TokenizationPass.
         * <p>
         * Usages:
         * <ul>
         * <li>Transformations that rely on usage, type information</li>
         * <li>Removing elements that shall be excluded from Tokenization</li>
         * </ul>
         * </p>
         */
        CPG_TRANSFORM(true);

        public final boolean disconnectEog;

        ExecutionPhase(boolean disconnectEog) {
            this.disconnectEog = disconnectEog;
        }
    }

    enum ExecutionOrder {

        ASCENDING_LOCATION,
        DESCENDING_LOCATION
    }

    class GraphTransformationImpl<T extends Node> implements GraphTransformation<T> {
        private final static Logger logger = LoggerFactory.getLogger(GraphTransformationImpl.class);
        protected final GraphPattern sourcePattern;
        protected final GraphPattern targetPattern;
        private final List<CreateNodeOperation<?>> newNodes;
        private final List<GraphOperation> operations;
        private final String name;
        private final ExecutionPhase phase;
        private final ExecutionOrder executionOrder;

        public GraphTransformationImpl(GraphPattern sourcePattern, GraphPattern targetPattern, String name, ExecutionPhase phase,
                List<CreateNodeOperation<?>> newNodes, List<GraphOperation> operations, ExecutionOrder executionOrder) {
            this.sourcePattern = sourcePattern;
            this.targetPattern = targetPattern;
            this.name = name;
            this.phase = phase;
            this.newNodes = newNodes;
            this.operations = operations;
            this.executionOrder = executionOrder;
        }

        @Override
        public void apply(Match match, TranslationContext ctx) {
            List<GraphOperation> concreteOperations = instantiate(operations, match);

            // create nodes of the target graph missing from the source graph
            newNodes.forEach(op -> op.resolve(match, ctx));

            logger.debug("Apply %s to node %s".formatted(name, match.get(sourcePattern.getRepresentingNode())));
            // apply other operations
            apply(match, concreteOperations, ctx);
        }

        private List<GraphOperation> instantiate(List<GraphOperation> operations, Match match) {
            return operations.stream().map((GraphOperation op) -> {
                if (op.isWildcarded()) {
                    return op.instantiateWildcard(match);
                } else if (op.isMultiEdged()) {
                    return op.instantiateAny1ofNEdge(match);
                }
                return op;
            }).toList();
        }

        /**
         * Applies the given list of {@link GraphOperation}s to the {@link Match}, following the structure of the
         * {@link NodePattern}.
         * @param match the match of the graph transformations source pattern to the concrete CPG
         * @param operations the list of transformations to apply
         * @param ctx the translation context of the current translation
         */
        protected void apply(Match match, List<GraphOperation> operations, TranslationContext ctx) {
            for (GraphOperation op : operations) {
                try {
                    op.resolve(match, ctx);
                } catch (TransformationException | RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }
            DummyNeighbor.getInstance().clear();
        }

        @Override
        public GraphPattern getSourcePattern() {
            return sourcePattern;
        }

        @Override
        public GraphPattern getTargetPattern() {
            return targetPattern;
        }

        @Override
        public ExecutionPhase getPhase() {
            return phase;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ExecutionOrder getExecutionOrder() {
            return this.executionOrder;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    /**
     * A {@link Builder} computes the steps of a {@link GraphTransformation} from the source and target
     * {@link SimpleGraphPattern}s.
     * @param <T> The common type of root {@link Node}.
     */
    class Builder<T extends Node> {
        private final GraphPattern sourcePattern;
        private final GraphPattern targetPattern;
        private final String name;
        private final ExecutionPhase phase;
        private ExecutionOrder executionOrder;

        private Builder(GraphPattern sourcePattern, GraphPattern targetPattern, String transformationName, ExecutionPhase phase) {
            this.sourcePattern = sourcePattern;
            this.targetPattern = targetPattern;
            this.name = transformationName;
            this.phase = phase;
            this.executionOrder = ExecutionOrder.DESCENDING_LOCATION;
        }

        /**
         * Returns a {@link Builder} for a {@link GraphTransformation} based on the given source and target
         * {@link SimpleGraphPattern}s.
         * @param <T> the common root {@link Node} type of the {@link SimpleGraphPattern}s
         * @param sourcePattern the source {@link SimpleGraphPattern}
         * @param targetPattern the target {@link SimpleGraphPattern}
         * @param name the transformation name
         * @param phase determines when to apply the transformation
         * @return a {@link Builder} for a {@link GraphTransformation} between source and target
         */
        public static <T extends Node> Builder<T> from(SimpleGraphPattern<T> sourcePattern, SimpleGraphPattern<T> targetPattern, String name,
                ExecutionPhase phase) {
            return new Builder<>(sourcePattern, targetPattern, name, phase);
        }

        public static Builder<Node> from(MultiGraphPattern sourcePattern, MultiGraphPattern targetPattern, String name, ExecutionPhase phase) {
            return new Builder<>(sourcePattern, targetPattern, name, phase);
        }

        private GraphTransformation<T> calculateTransformation() {
            List<CreateNodeOperation<?>> newNodes = this.createNewNodes(sourcePattern, targetPattern);
            List<GraphOperation> ops = new ArrayList<>();
            sourcePattern.compareTo(targetPattern, (srcPattern, tgtPattern) -> compare(srcPattern, tgtPattern, null, ops, null));

            return new GraphTransformationImpl<>(sourcePattern, targetPattern, name, phase, newNodes, ops, executionOrder);
        }

        private List<CreateNodeOperation<?>> createNewNodes(GraphPattern sourcePattern, GraphPattern targetPattern) {
            List<String> newRoles = new ArrayList<>(targetPattern.getAllIds());
            newRoles.removeAll(sourcePattern.getAllIds());

            List<CreateNodeOperation<?>> newNodes = new ArrayList<>();
            for (String roleName : newRoles) {
                // new node pattern needed for the transformation calculation
                NodePattern<?> newPattern = sourcePattern.addNode(roleName, targetPattern.getPattern(roleName));

                // new nodes needed for the transformation application
                CreateNodeOperation<?> createNodeOperation = new CreateNodeOperation<>(sourcePattern, roleName, newPattern);
                newNodes.add(createNodeOperation);
            }
            return newNodes;
        }

        /**
         * @param <P> (super)type of the parent node, specified by the incoming edge
         * @param <T> common type of the current source and target node, defined by the incoming edge
         * @param <AS> actual concrete type of the source node
         * @param <AT> actual concrete type of the target node
         * @param source current node pattern of the source graph
         * @param target current node pattern of the target graph
         * @param parent current node pattern of the parent node
         * @param ops list to save transformation operations in
         * @param incomingEdge edge by which this node was visited
         */
        private <T extends Node, P extends Node, AS extends T, AT extends T> void compare(NodePattern<AS> source, NodePattern<AT> target,
                NodePattern<? extends P> parent, List<GraphOperation> ops, CpgEdge<P, T> incomingEdge) {

            String srcRoleName = sourcePattern.getId(source);
            String tgtRoleName = targetPattern.getId(target);

            NodePattern<AT> newSource;
            if (Objects.equals(srcRoleName, tgtRoleName)) {
                // equal role name indicates type compatibility
                newSource = (NodePattern<AT>) source;
            } else {
                // equal role name indicates type compatibility
                newSource = (NodePattern<AT>) sourcePattern.getPattern(tgtRoleName);

                boolean disconnectEog = this.phase.disconnectEog && incomingEdge.isAst();

                if (!Objects.isNull(srcRoleName) && !Objects.isNull(tgtRoleName)) {
                    ops.add(new ReplaceOperation<>(parent, incomingEdge, newSource, disconnectEog));
                } else if (Objects.isNull(srcRoleName)) {
                    if (incomingEdge instanceof CpgNthEdge<P, T> nthEdge) {
                        ops.add(new InsertOperation<>(parent, nthEdge, newSource, disconnectEog));
                    } else {
                        ops.add(new SetOperation<>(parent, incomingEdge, newSource, disconnectEog));
                    }
                } else {
                    // tgtRoleName == null
                    ops.add(new RemoveOperation<>(parent, incomingEdge, disconnectEog));
                    return;
                }
            }
            if (newSource.shouldStopRecursion()) {
                return;
            }
            newSource.markStopRecursion();

            handleSimpleRelationships(newSource, target, ops);
            handleMultiRelationships(target, ops, newSource);
            handleSequenceRelationships(newSource, target, ops);

        }


        private <S extends Node> void handleSimpleRelationships(NodePattern<S> source, NodePattern<S> target, List<GraphOperation> ops) {
            List<NodePattern.RelatedNode<? super S, ?>> unprocessedTargetRelated = new ArrayList<>(target.getRelatedNodes());
            for (NodePattern.RelatedNode<? super S, ?> sourceRelated : source.getRelatedNodes()) {
                if (sourceRelated.edge().isAnalytic())
                    continue;

                Optional<? extends NodePattern.RelatedNode> maybeNextTarget = unprocessedTargetRelated.stream()
                        .filter(rel -> sourceRelated.edge().isEquivalentTo(rel.edge()))
                        .map(rel -> sourceRelated.getClass().cast(rel)).findFirst();

                maybeNextTarget.ifPresent(unprocessedTargetRelated::remove);
                recurseSimple(source, sourceRelated, maybeNextTarget.orElse(null), ops);
            }

            for (NodePattern.RelatedNode targetRelated : unprocessedTargetRelated) {
                // -> SetOperation
                recurseSimple(source, targetRelated, targetRelated, ops);
            }
        }

        /**
         * Try to iterate into the related nodes.
         * @param <S> Type of the source node, defined by the edge
         * @param <R> Type of the related node, defined by the edge
         * @param parent Parent of the next source node
         * @param sourceRelated Relation in the source graph that is currently recursed into.
         * @param targetRelated Relation in the target graph equivalent to sourceRelated.
         * @param ops List to save transformations into
         */
        private <S extends Node, P extends S, R extends Node> void recurseSimple(NodePattern<P> parent, NodePattern.RelatedNode<S, R> sourceRelated,
                NodePattern.RelatedNode<S, R> targetRelated, List<GraphOperation> ops) {
            NodePattern<? extends R> nextSource = sourceRelated.pattern();

            NodePattern<? extends R> nextTarget = Objects.isNull(targetRelated) ? nextSource : targetRelated.pattern();
            compare(nextSource, nextTarget, parent, ops, sourceRelated.edge());
        }

        private <T extends Node, AT extends T> void handleMultiRelationships(NodePattern<AT> target, List<GraphOperation> ops,
                NodePattern<AT> newSource) {
            List<NodePattern.Related1ToNNode<? super AT, ?>> allTargetRelated = target.getRelated1ToNNodes();
            for (NodePattern.Related1ToNNode<? super AT, ?> sourceRelated : newSource.getRelated1ToNNodes()) {
                if (sourceRelated.edge().isAnalytic())
                    continue;

                Optional<NodePattern.Related1ToNNode> maybeTargetRelated = allTargetRelated.stream()
                        .filter(rel -> sourceRelated.edge().isEquivalentTo(rel.edge()))
                        // exactly 1 candidate -> role names may differ, possible replacement
                        // more than 1 candidate -> role names must match
                        .filter(rel -> allTargetRelated.size() == 1
                                || sourcePattern.getId(sourceRelated.pattern()).equals(targetPattern.getId(rel.pattern())))
                        .findFirst().map(rel -> sourceRelated.getClass().cast(rel));

                maybeTargetRelated.ifPresent(allTargetRelated::remove);

                recurseMulti(newSource, sourceRelated, maybeTargetRelated.orElse(null), ops);
            }

            for (NodePattern.Related1ToNNode targetRelated : allTargetRelated) {
                // needs to be inserted
                recurseMulti(newSource, targetRelated, targetRelated, ops);
            }
        }

        private <S extends Node, P extends S, R extends Node> void recurseMulti(NodePattern<P> parent,
                NodePattern.Related1ToNNode<S, R> sourceRelated, NodePattern.Related1ToNNode<S, ?> targetRelated, List<GraphOperation> ops) {
            NodePattern<? extends R> nextSource = sourceRelated.pattern();
            CpgMultiEdge<S, R>.Any1ofNEdge incomingEdge = sourceRelated.edge().getAny1ofNEdgeTo(nextSource);

            NodePattern<? extends R> nextTarget;
            if (Objects.isNull(targetRelated)) {
                // needs to be removed
                nextTarget = nextSource;
            } else {
                // R is guaranteed by the equal edge type
                NodePattern.Related1ToNNode<S, R> target1ofN = (NodePattern.Related1ToNNode<S, R>) targetRelated;
                nextTarget = target1ofN.pattern();
            }
            compare(nextSource, nextTarget, parent, ops, incomingEdge);

        }

        private <S extends Node> void handleSequenceRelationships(NodePattern<S> source, NodePattern<S> target, List<GraphOperation> ops) {
            List<NodePattern.Related1ToNSequence<? super S, ?>> allTargetRelated = target.getRelated1ToNSequences();
            for (NodePattern.Related1ToNSequence<? super S, ?> sourceRelated : source.getRelated1ToNSequences()) {
                if (sourceRelated.edge().isAnalytic())
                    continue;

                Optional<NodePattern.Related1ToNSequence> maybeTargetRelated = allTargetRelated.stream()
                        .filter(rel -> sourceRelated.edge().isEquivalentTo(rel.edge())).findFirst().map(rel -> sourceRelated.getClass().cast(rel));

                maybeTargetRelated.ifPresent(allTargetRelated::remove);
                recurseSequence(source, sourceRelated, maybeTargetRelated.orElse(null), ops);
            }
        }

        private <S extends Node, P extends S, R extends Node> void recurseSequence(NodePattern<P> parent,
                NodePattern.Related1ToNSequence<? super S, R> sourceRelated, NodePattern.Related1ToNSequence<S, ?> targetRelated,
                List<GraphOperation> ops) {

            // R is guaranteed by the equal edge type
            NodePattern.Related1ToNSequence<S, R> typedTargetRelated = (NodePattern.Related1ToNSequence<S, R>) targetRelated;

            for (int i = 0; i < sourceRelated.pattern().size(); i++) {
                // Todo: What if the sequence is not supposed to be inserted at the beginning?
                NodePattern<? extends R> nextSource = sourceRelated.getPattern(i);
                NodePattern<? extends R> nextTarget = typedTargetRelated.pattern().get(i);
                compare(nextSource, nextTarget, parent, ops, nthElement(sourceRelated.edge(), i));
            }
        }

        public GraphTransformation<T> build() {
            return this.calculateTransformation();
        }

        public GraphTransformation.Builder<T> setExecutionOrder(ExecutionOrder executionOrder) {
            this.executionOrder = executionOrder;
            return this;
        }
    }

}
