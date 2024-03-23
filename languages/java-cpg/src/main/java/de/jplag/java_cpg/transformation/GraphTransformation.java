package de.jplag.java_cpg.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.*;
import de.jplag.java_cpg.transformation.matching.pattern.relation.Relation;
import de.jplag.java_cpg.transformation.operations.*;

/**
 * This saves all information related to a transformation on a graph.
 */
public interface GraphTransformation {

    /**
     * Applies the transformation to the Graph represented by the given {@link Match} which indicates which {@link Node}s
     * shall be involved in the transformation.
     * @param match the match of this {@link GraphTransformation}'s source pattern to a concrete graph
     * @param ctx a {@link TranslationContext} object
     */
    void apply(Match match, TranslationContext ctx);

    /**
     * Gets the {@link ExecutionPhase} for this {@link GraphTransformation}
     * @return a {@link ExecutionOrder} object
     */
    ExecutionOrder getExecutionOrder();

    /**
     * Gets the name for this {@link GraphTransformation}.
     * @return the name
     */
    String getName();

    /**
     * Gets the {@link ExecutionPhase} for this {@link GraphTransformation}.
     * @return the execution phase
     */
    ExecutionPhase getPhase();

    /**
     * Gets the source {@link GraphPattern} for this {@link GraphTransformation}.
     * @return the source pattern
     */
    GraphPattern getSourcePattern();

    /**
     * Determines in which transformation pass this transformation is executed.
     */
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
         * Executes after the EOG is constructed, right before the TokenizationPass. Usages:
         * <ul>
         * <li>Transformations that rely on usage, type information</li>
         * <li>Removing elements that shall be excluded from Tokenization</li>
         * </ul>
         */
        CPG_TRANSFORM(true);

        public final boolean disconnectEog;

        ExecutionPhase(boolean disconnectEog) {
            this.disconnectEog = disconnectEog;
        }
    }

    /**
     * Determines the order in which matches of this {@link GraphTransformation} should be processed in order to ensure
     * correct and efficient processing.
     */
    enum ExecutionOrder {

        ASCENDING_LOCATION,
        DESCENDING_LOCATION
    }

    class GraphTransformationImpl implements GraphTransformation {
        private static final Logger logger = LoggerFactory.getLogger(GraphTransformationImpl.class);
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
            newNodes.forEach(op -> op.resolveAndApply(match, ctx));

            logger.debug("Apply {} to node {}", name, match);
            // apply other operations
            apply(match, concreteOperations, ctx);
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
                    op.resolveAndApply(match, ctx);
                } catch (RuntimeException e) {
                    throw new TransformationException(e);
                }
            }
            DummyNeighbor.getInstance().clear();
        }

        @Override
        public ExecutionOrder getExecutionOrder() {
            return this.executionOrder;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ExecutionPhase getPhase() {
            return phase;
        }

        @Override
        public GraphPattern getSourcePattern() {
            return sourcePattern;
        }

        private List<GraphOperation> instantiate(List<GraphOperation> operations, Match match) {
            return operations.stream().map((GraphOperation op) -> {
                if (op.isWildcarded()) {
                    return op.instantiateWildcard(match);
                } else if (op.isMultiEdged()) {
                    return op.instantiateAnyOfNEdge(match);
                }
                return op;
            }).toList();
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    /**
     * A {@link Builder} computes the steps of a {@link GraphTransformation} from the source and target
     * {@link SimpleGraphPattern}s.
     */
    class Builder {
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
        public static <T extends Node> Builder from(SimpleGraphPattern<T> sourcePattern, SimpleGraphPattern<T> targetPattern, String name,
                ExecutionPhase phase) {
            return new Builder(sourcePattern, targetPattern, name, phase);
        }

        public static Builder from(MultiGraphPattern sourcePattern, MultiGraphPattern targetPattern, String name, ExecutionPhase phase) {
            return new Builder(sourcePattern, targetPattern, name, phase);
        }

        public GraphTransformation build() {
            return this.calculateTransformation();
        }

        private GraphTransformation calculateTransformation() {
            List<CreateNodeOperation<?>> newNodes = this.createNewNodes(sourcePattern, targetPattern);
            List<GraphOperation> ops = new ArrayList<>();
            sourcePattern.compareTo(targetPattern, (srcPattern, tgtPattern) -> compare(srcPattern, tgtPattern, null, ops, null));

            return new GraphTransformationImpl(sourcePattern, targetPattern, name, phase, newNodes, ops, executionOrder);
        }

        /**
         * @param <P> (super)type of the parent node, specified by the incoming edge
         * @param <T> common type of the current source and target node, defined by the incoming edge
         * @param <T1> actual concrete type of the source node
         * @param <T2> actual concrete type of the target node
         * @param source current node pattern of the source graph
         * @param target current node pattern of the target graph
         * @param parent current node pattern of the parent node
         * @param ops list to save transformation operations in
         * @param incomingEdge edge by which this node was visited
         */
        private <T extends Node, P extends Node, T1 extends T, T2 extends T> void compare(NodePattern<T1> source, NodePattern<T2> target,
                NodePattern<? extends P> parent, List<GraphOperation> ops, CpgEdge<P, T> incomingEdge) {

            Role srcRole = sourcePattern.getRole(source);
            Role tgtRole = targetPattern.getRole(target);

            NodePattern<T2> newSource;
            if (Objects.equals(srcRole, tgtRole)) {
                // equal role name indicates type compatibility
                newSource = (NodePattern<T2>) source;
            } else {

                boolean disconnectEog = this.phase.disconnectEog && incomingEdge.isAst();

                /*
                 * Three cases: 1. Source and target not null -> replace 2. Source not null, target null -> remove 3. Source null,
                 * target not null -> insert/set
                 */

                if (!Objects.isNull(tgtRole) && !Objects.isNull(srcRole)) {
                    newSource = sourcePattern.getPattern(tgtRole, target.getRootClass());
                    ops.add(new ReplaceOperation<>(parent, incomingEdge, newSource, disconnectEog));
                } else if (Objects.isNull(srcRole)) {
                    newSource = sourcePattern.getPattern(tgtRole, target.getRootClass());
                    if (incomingEdge instanceof CpgNthEdge<P, T> nthEdge) {
                        ops.add(new InsertOperation<>(parent, nthEdge, newSource, disconnectEog));
                    } else {
                        ops.add(new SetOperation<>(parent, incomingEdge, newSource));
                    }
                } else {
                    // tgtRole == null
                    ops.add(new RemoveOperation<>(parent, incomingEdge, disconnectEog));
                    return;
                }

            }
            if (newSource.shouldStopRecursion()) {
                return;
            }
            newSource.markStopRecursion();

            handleRelationships(newSource, target, ops);

        }

        private List<CreateNodeOperation<?>> createNewNodes(GraphPattern sourcePattern, GraphPattern targetPattern) {
            List<Role> newRoles = new ArrayList<>(targetPattern.getAllRoles());
            newRoles.removeAll(sourcePattern.getAllRoles());

            List<CreateNodeOperation<?>> newNodes = new ArrayList<>();
            for (Role role : newRoles) {
                // new node pattern needed for the transformation calculation
                NodePattern<?> newPattern = sourcePattern.addNode(role, targetPattern.getPattern(role));

                // new nodes needed for the transformation application
                CreateNodeOperation<?> createNodeOperation = new CreateNodeOperation<>(sourcePattern, role, newPattern);
                newNodes.add(createNodeOperation);
            }
            return newNodes;
        }

        private <T extends Node> void handleRelationships(NodePattern<T> source, NodePattern<T> target, List<GraphOperation> ops) {
            source.handleRelationships(target, RelationComparisonFunction.from(this, ops));
        }

        public GraphTransformation.Builder setExecutionOrder(ExecutionOrder executionOrder) {
            this.executionOrder = executionOrder;
            return this;
        }

        @FunctionalInterface
        public interface RelationComparisonFunction {

            static RelationComparisonFunction from(Builder builder, List<GraphOperation> ops) {
                return new RelationComparisonFunction() {
                    @Override
                    public <T extends Node, T1 extends T, T2 extends T, P extends Node> void compare(NodePattern<T1> source, NodePattern<T2> target,
                            NodePattern<? extends P> parent, CpgEdge<P, T> incomingEdge) {
                        builder.compare(source, target, parent, ops, incomingEdge);
                    }
                };
            }

            default <T extends Node, P extends T, R extends Node> void castAndCompare(Relation<? super T, R, ?> source, Relation<?, ?, ?> target,
                    NodePattern.NodePatternImpl<P> parent) {
                Relation<T, R, ?> castTarget = (Relation<T, R, ?>) target;
                CpgEdge<? super T, R> edge = switch (source.getEdge()) {
                    case CpgMultiEdge<? super T, R> multiEdge -> multiEdge.getAnyOfNEdgeTo(source.pattern);
                    case CpgEdge<? super T, R> singleEdge -> singleEdge;
                    default -> throw new TransformationException("Relation edge must be CpgEdge or CpgMultiEdge");
                };

                compare(source.pattern, castTarget.pattern, parent, edge);
            }

            <T extends Node, T1 extends T, T2 extends T, P extends Node> void compare(NodePattern<T1> source, NodePattern<T2> target,
                    NodePattern<? extends P> parent, CpgEdge<P, T> incomingEdge);
        }
    }

}
