package de.jplag.java_cpg.transformation.matching.pattern;

import static de.jplag.java_cpg.transformation.matching.edges.Edges.CALL_EXPRESSION__ARGUMENTS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.CALL_EXPRESSION__INVOKES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.FunctionDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression;
import de.jplag.java_cpg.transformation.GraphTransformation.Builder.RelationComparisonFunction;
import de.jplag.java_cpg.transformation.Role;
import de.jplag.java_cpg.transformation.matching.edges.AnyOfNEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.pattern.relation.RelatedOneToNNode;

class MultiEdgeIsolationTest {

    @Test
    void sequenceIndicesBelongToRelationsRatherThanSharedEdgesOrChildPatterns() {
        var child = NodePattern.forNodeType(FunctionDeclaration.class);
        var first = new RelatedOneToNNode<>(child, CALL_EXPRESSION__INVOKES);
        var third = new RelatedOneToNNode<>(child, CALL_EXPRESSION__INVOKES, 2);

        assertNotSame(first.getTransformationEdge(), third.getTransformationEdge());
        // Check for repeatability of getter:
        assertSame(first.getTransformationEdge(), first.getTransformationEdge());
        assertEquals(0, first.getTransformationEdge().getMinimalIndex());
        assertEquals(2, third.getTransformationEdge().getMinimalIndex());
    }

    @Test
    void matchingResolvesTheRelationPlaceholder() {
        var parent = NodePattern.forNodeType(CallExpression.class);
        var child = NodePattern.forNodeType(FunctionDeclaration.class);
        var relation = new RelatedOneToNNode<>(child, CALL_EXPRESSION__INVOKES);
        var match = new Match(new SimpleGraphPattern<>(parent, new PatternRegistry()));

        match.resolveAnyOfNEdge(parent, relation, 3);

        var resolved = match.getEdge(parent, relation.getTransformationEdge());
        assertSame(CALL_EXPRESSION__INVOKES, resolved.getMultiEdge());
        assertEquals(3, resolved.getIndex());
    }

    @Test
    void transformationComparisonPreservesConsecutiveIndices() {
        var pattern = new ArgumentPatternBuilder().build();
        Collection<Integer> indices = new ArrayList<>();
        pattern.getRoot().handleRelationships(NodePattern.forNodeType(CallExpression.class), new RelationComparisonFunction() {
            @Override
            public <T extends Node, T1 extends T, T2 extends T, P extends Node> void compare(NodePattern<T1> source, NodePattern<T2> target,
                    NodePattern<? extends P> parent, CpgEdge<P, T> incomingEdge) {
                var placeholder = assertInstanceOf(AnyOfNEdge.class, incomingEdge);
                assertSame(CALL_EXPRESSION__ARGUMENTS, placeholder.getMultiEdge());
                indices.add(placeholder.getMinimalIndex());
            }
        });
        assertEquals(List.of(0, 1), indices);
    }

    @Test
    void reusingAConsecutiveModificationDoesNotChangeEarlierPatterns() {
        var builder = new ArgumentPatternBuilder();
        var sequence = builder.consecutiveArguments();
        var first = builder.create(CallExpression.class, TestRole.ROOT, sequence);
        var second = new ArgumentPatternBuilder().create(CallExpression.class, TestRole.ROOT, sequence);

        assertSequenceMatch(first, arguments());
        assertSequenceMatch(second, arguments());
    }

    @Test
    @Timeout(30)
    void independentPatternsCanUseSharedEdgesConcurrently() throws Exception {
        // Fresh patterns used to populate caches on the same static edge descriptors.
        repeatConcurrently(() -> assertSequenceMatch(new ArgumentPatternBuilder().build(), arguments()));
    }

    @Test
    @Timeout(30)
    void onePatternCanMatchIndependentGraphsConcurrently() throws Exception {
        var sharedPattern = new ArgumentPatternBuilder().build();
        repeatConcurrently(() -> assertSequenceMatch(sharedPattern, arguments()));
    }

    private static void repeatConcurrently(Runnable matchAssertion) throws Exception {
        var start = new CountDownLatch(1);
        Collection<Future<?>> tasks = new ArrayList<>();
        try (var executor = Executors.newFixedThreadPool(8)) {
            for (int worker = 0; worker < 8; worker++) {
                tasks.add(executor.submit(() -> {
                    start.await();
                    for (int iteration = 0; iteration < 200; iteration++) {
                        matchAssertion.run();
                    }
                    return null;
                }));
            }
            // Align the workers so matching is exercised under contention rather than sequentially.
            start.countDown();
            for (Future<?> task : tasks) {
                task.get(20, TimeUnit.SECONDS);
            }
        }
    }

    private static CallExpression arguments() {
        var call = new CallExpression();
        var prefix = new CallExpression();
        prefix.setCode("prefix");
        var first = new CallExpression();
        first.setCode("first");
        var second = new CallExpression();
        second.setCode("second");
        call.setArguments(List.of(prefix, first, second));
        return call;
    }

    private static void assertSequenceMatch(SimpleGraphPattern<CallExpression> pattern, CallExpression call) {
        var matches = pattern.recursiveMatch(call);
        assertEquals(1, matches.size());
        assertSame(call.getArguments().get(1), matches.getFirst().get(pattern.getPattern(TestRole.FIRST)));
        assertSame(call.getArguments().get(2), matches.getFirst().get(pattern.getPattern(TestRole.SECOND)));
    }

    private static final class ArgumentPatternBuilder extends GraphPatternBuilder {
        @Override
        public SimpleGraphPattern<CallExpression> build() {
            return create(CallExpression.class, TestRole.ROOT, consecutiveArguments());
        }

        private PatternModification<CallExpression> consecutiveArguments() {
            return relatedConsecutive(CALL_EXPRESSION__ARGUMENTS, Expression.class,
                    node(Expression.class, TestRole.FIRST, property(node -> "first".equals(node.getCode()))),
                    node(Expression.class, TestRole.SECOND, property(node -> "second".equals(node.getCode()))));
        }
    }

    private enum TestRole implements Role {
        ROOT,
        FIRST,
        SECOND;

        @Override
        public String getName() {
            return name();
        }
    }
}
