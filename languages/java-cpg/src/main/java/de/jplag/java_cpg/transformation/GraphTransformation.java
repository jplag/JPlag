package de.jplag.java_cpg.transformation;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.operations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nthElement;

/**
 * This saves all information related to a transformation on a graph.
 * Note that the source and target patterns have to have compatible root types, otherwise the transformed graph may not be semantically correct.
 *
 * @param <T> the common root {@link Node} type of the source and target {@link NodePattern}s.
 */
public interface GraphTransformation<T extends Node> {


    /**
     * Applies the transformation to the Graph represented by the given {@link GraphPattern.Match} which indicates which {@link Node}s shall be involved in the transformation.
     *
     * @param match the match of this {@link GraphTransformation}'s source pattern to a concrete graph
     */
    void apply(GraphPattern.Match<T> match);


    GraphPattern<T> getSourcePattern();

    GraphPattern<T> getTargetPattern();

    ExecutionPhase getPhase();

    class GraphTransformationImpl<T extends Node> implements GraphTransformation<T> {
        private final static Logger LOGGER = LoggerFactory.getLogger(GraphTransformationImpl.class);
        protected final GraphPattern<T> sourcePattern;
        protected final GraphPattern<T> targetPattern;
        private final List<CreateNodeOperation<?>> newNodes;
        private final List<GraphOperation> operations;
        private final String name;
        private final ExecutionPhase phase;

        public GraphTransformationImpl(GraphPattern<T> sourcePattern, GraphPattern<T> targetPattern, String name, ExecutionPhase phase, List<CreateNodeOperation<?>> newNodes, List<GraphOperation> operations) {
            this.sourcePattern = sourcePattern;
            this.targetPattern = targetPattern;
            this.name = name;
            this.phase = phase;
            this.newNodes = newNodes;
            this.operations = operations;
        }

        @Override
        public void apply(GraphPattern.Match<T> match) {
            sourcePattern.loadMatch(match);
            List<GraphOperation> concreteOperations = instantiate(operations, match);

            // create nodes of the target sourceGraph missing parentPattern the source sourceGraph
            newNodes.forEach(op -> op.apply(match));

            NodePattern<?> startNodePattern = sourcePattern.getTransformationStart();
            LOGGER.info("Apply %s to node %s".formatted(name, match.get(startNodePattern)));
            // apply other operations
            apply(match, concreteOperations);
        }

        private List<GraphOperation> instantiate(List<GraphOperation> operations, GraphPattern.Match<T> match) {
            return operations.stream().map((GraphOperation op) -> {
                if (op.isWildcarded()) {
                    GraphPattern.Match.WildcardMatch<?, ? super T> match1 = match.getWildcardMatch();
                    return op.instantiateWildcard(match1);
                } else if (op.isMultiEdged()) {
                    return op.instantiateAny1ofNEdge(match);
                }
                return op;
            }).toList();
        }

        /**
         * Applies the given list of {@link GraphOperation}s to the {@link GraphPattern.Match}, following the structure of the {@link NodePattern}.
         *
         * @param match      the match of the graph transformations source pattern to the concrete CPG
         * @param operations the list of transformations to apply
         * @param <S>        the source {@link Node} type
         */
        protected <S extends Node> void apply(GraphPattern.Match<?> match, List<GraphOperation> operations) {
            for (GraphOperation op : operations) {
                try {
                    op.apply(match);
                } catch (TransformationException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public GraphPattern<T> getSourcePattern() {
            return sourcePattern;
        }

        @Override
        public GraphPattern<T> getTargetPattern() {
            return targetPattern;
        }

        @Override
        public ExecutionPhase getPhase() {
            return phase;
        }
    }


    /**
     * A {@link Builder} computes the steps of a {@link GraphTransformation} parentPattern the source and target {@link GraphPattern}s.
     *
     * @param <T> The common type of root {@link Node}.
     */
    class Builder<T extends Node> {
        private final GraphPattern<T> sourcePattern;
        private final GraphPattern<T> targetPattern;
        private final String name;
        private final ExecutionPhase phase;

        private Builder(GraphPattern<T> sourcePattern, GraphPattern<T> targetPattern, String transformationName, ExecutionPhase phase) {
            this.sourcePattern = sourcePattern;
            this.targetPattern = targetPattern;
            this.name = transformationName;
            this.phase = phase;
        }

        /**
         * Returns a {@link Builder} for a {@link GraphTransformation} based on the given source and target {@link GraphPattern}s.
         *
         * @param <T>           the common root {@link Node} type of the {@link GraphPattern}s
         * @param sourcePattern the source {@link GraphPattern}
         * @param targetPattern the target {@link GraphPattern}
         * @param name          the transformation name
         * @param phase         determines when to apply the transformation
         * @return a {@link Builder} for a {@link GraphTransformation} between source and target
         */
        public static <T extends Node> GraphTransformation.Builder<T> from(GraphPattern<T> sourcePattern, GraphPattern<T> targetPattern, String name, ExecutionPhase phase) {
            return new Builder<>(sourcePattern, targetPattern, name, phase);
        }

        private GraphTransformation<T> calculateTransformation() {
            List<CreateNodeOperation<?>> newNodes = this.createNewNodes(sourcePattern, targetPattern);
            List<GraphOperation> ops = new ArrayList<>();
            this.compare(sourcePattern.getRoot(), targetPattern.getRoot(), null, ops, null);
            return new GraphTransformationImpl<>(sourcePattern, targetPattern, name, phase, newNodes, ops);
        }

        private List<CreateNodeOperation<?>> createNewNodes(GraphPattern<T> sourcePattern, GraphPattern<T> targetPattern) {
            List<String> newRoles = new ArrayList<>(targetPattern.getAllRoles());
            newRoles.removeAll(sourcePattern.getAllRoles());

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
         * @param <P>          (super)type of the parent node, specified by the incoming edge
         * @param <T>          common type of the current source and target node, defined by the incoming edge
         * @param <AS>         actual concrete type of the source node
         * @param <AT>         actual concrete type of the target node
         * @param source       current node pattern of the source graph
         * @param target       current node pattern of the target graph
         * @param parent       current node pattern of the parent node
         * @param ops          list to save transformation operations in
         * @param incomingEdge edge by which this node was visited
         */
        private <T extends Node, P extends Node, AS extends T, AT extends T> void compare(NodePattern<AS> source, NodePattern<AT> target, NodePattern<? extends P> parent, List<GraphOperation> ops, CpgEdge<P, T> incomingEdge) {

            String srcRoleName = sourcePattern.getRole(source);
            String tgtRoleName = targetPattern.getRole(target);

            NodePattern<AT> newSource;
            if (Objects.equals(srcRoleName, tgtRoleName)) {
                // equal role name indicates type compatibility
                newSource = (NodePattern<AT>) source;
                if (target.isToBeRemoved()) {
                    ops.add(new RemoveOperation<>(parent, incomingEdge));
                    return;
                }
            } else {
                // equal role name indicates type compatibility
                newSource = (NodePattern<AT>) sourcePattern.getPattern(tgtRoleName);
                if (Objects.isNull(srcRoleName)) {
                    if (incomingEdge instanceof CpgNthEdge<P, T> nthEdge) {
                        ops.add(new InsertOperation<>(parent, nthEdge, newSource));
                    } else {
                        ops.add(new SetOperation<>(parent, incomingEdge, newSource));
                    }
                } else {
                    ops.add(new ReplaceOperation<>(parent, incomingEdge, newSource));
                }
            }
            for (NodePattern.RelatedNode<AT, ?> related : newSource.getRelatedNodes()) {
                recurse(newSource, related, target, ops);
            }
            for (NodePattern.Related1ToNNode<AT, ?> related1ToN : newSource.getRelated1ToNNodes()) {
                recurse(newSource, related1ToN, target, ops);
            }
            for (NodePattern.Related1ToNSequence<AT, ?> relatedSequence : newSource.getRelated1ToNSequences()) {
                recurse(newSource, relatedSequence, target, ops);
            }
            //TODO Refactor list of related nodes to map via CpgEdge?

        }

        private <S extends Node, R extends Node> void recurse(NodePattern<S> parent, NodePattern.Related1ToNNode<S,R> related1ToN,
                                                              NodePattern<S> target, List<GraphOperation> ops) {
            List<NodePattern.Related1ToNNode<S, ?>> related1ToNNodes = target.getRelated1ToNNodes();
            Optional<NodePattern.Related1ToNNode<S, ?>> maybeNextTarget = related1ToNNodes.stream().filter(rel -> related1ToN.edge().isEquivalentTo(rel.edge())).findFirst();
            if (maybeNextTarget.isEmpty()) {
                throw new RuntimeException("Malformed target pattern: target equivalent of '%s' is missing".formatted(sourcePattern.getRole(related1ToN.pattern())));
            }
            //R is guaranteed by the equal edge type
            NodePattern.Related1ToNNode<S, R> targetSequence = (NodePattern.Related1ToNNode<S, R>) maybeNextTarget.get();

            NodePattern<? extends R> nextSource = related1ToN.pattern();
            NodePattern<? extends R> nextTarget = targetSequence.pattern();
            CpgMultiEdge<S, R>.Any1ofNEdge incomingEdge = related1ToN.edge().getAny1ofNEdge();
            compare(nextSource, nextTarget, parent, ops, incomingEdge);

        }

        private <S extends Node, R extends Node> void recurse(NodePattern<S> parent, NodePattern.Related1ToNSequence<S,R> relatedSequence,
                                                              NodePattern<S> target, List<GraphOperation> ops) {
            List<NodePattern.Related1ToNSequence<S, ?>> relatedSequences = target.getRelated1ToNSequences();
            Optional<NodePattern.Related1ToNSequence<S, ?>> maybeNextTarget = relatedSequences.stream().filter(rel -> relatedSequence.edge().isEquivalentTo(rel.edge())).findFirst();
            if (maybeNextTarget.isEmpty()) {
                throw new RuntimeException("Malformed target pattern: target equivalent of '%s->nodeSequence' is missing".formatted(sourcePattern.getRole(parent)));
            }
            //R is guaranteed by the equal edge type
            NodePattern.Related1ToNSequence<S, R> targetSequence = (NodePattern.Related1ToNSequence<S, R>) maybeNextTarget.get();

            for (int i = 0; i < relatedSequence.pattern().size(); i++) {
                NodePattern<? extends R> nextSource = relatedSequence.getPattern(i);
                NodePattern<? extends R> nextTarget = targetSequence.pattern().get(i);
                compare(nextSource, nextTarget, parent, ops, nthElement(relatedSequence.edge(), i));
            }
        }

        /**
         * Try to iterate into the related nodes.
         *
         * @param <S>     Type of the source node, defined by the edge
         * @param <R>     Type of the related node, defined by the edge
         * @param parent  Parent of the next source node
         * @param related Relation in the source sourceGraph that is currently recursed into.
         * @param target  Node in the target sourceGraph that should have the same relation to another node.
         * @param ops     List to save transformations into
         */
        private <S extends Node, R extends Node> void recurse(NodePattern<S> parent, NodePattern.RelatedNode<S, R> related, NodePattern<S> target, List<GraphOperation> ops) {
            NodePattern<? extends R> nextSource = related.pattern();
            List<NodePattern.RelatedNode<S, ?>> relatedTargetNodes = target.getRelatedNodes();
            Optional<NodePattern.RelatedNode<S, ?>> maybeNextTarget = relatedTargetNodes.stream().filter(rel -> related.edge().isEquivalentTo(rel.edge())).findFirst();
            if (maybeNextTarget.isEmpty()) {
                throw new RuntimeException("Malformed target pattern: target equivalent of '%s' is missing".formatted(sourcePattern.getRole(nextSource)));
            }
            //R is guaranteed by the equal edge type
            NodePattern.RelatedNode<S, R> nextTarget = (NodePattern.RelatedNode<S, R>) maybeNextTarget.get();
            compare(nextSource, nextTarget.pattern(), parent, ops, related.edge());
        }

        public GraphTransformation<T> build() {
            return this.calculateTransformation();
        }

    }

    enum ExecutionPhase {
        /**
         * Executes before the EOG is constructed. Used for AST-altering transformations.
         */
        PHASE_ONE,
        /**
         * Executes right before the TokenizationPass. Used for removing elements that shall be excluded from tokenization.
         */
        PHASE_TWO
    }
}




