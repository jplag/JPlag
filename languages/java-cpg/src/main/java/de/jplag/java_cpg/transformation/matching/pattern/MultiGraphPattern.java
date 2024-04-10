package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.GraphTransformation;

/**
 * A {@link MultiGraphPattern} is a {@link GraphPattern} that involves multiple subtrees that may or may not be linked
 * to each other directly in the AST. It can be used to facilitate the formulation of complex
 * {@link GraphTransformation}s.
 */
public class MultiGraphPattern extends GraphPatternImpl {
    private final List<SimpleGraphPattern<?>> subgraphs;

    /**
     * Creates a new {@link MultiGraphPattern}.
     * @param subgraphs the child graphs
     * @param patterns the pattern registry
     */
    public MultiGraphPattern(List<SimpleGraphPattern<?>> subgraphs, PatternRegistry patterns) {
        super(patterns);
        this.subgraphs = subgraphs;
        if (Objects.isNull(representingNode)) {
            representingNode = subgraphs.getFirst().getRoot();
        }
    }

    @Override
    public List<NodePattern<Node>> getRoots() {
        return subgraphs.stream().map(SimpleGraphPattern::getRoot).map(root -> (NodePattern<Node>) root)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Match> match(Map<NodePattern<?>, List<? extends Node>> rootCandidateMap) {
        List<Match> matches = new ArrayList<>();
        matches.add(new Match(this));
        getRoots().forEach(root -> {
            if (matches.isEmpty()) {
                return;
            }
            List<? extends Node> rootCandidates = rootCandidateMap.get(root);
            List<Match> rootMatches = rootCandidates.stream().flatMap(rootCandidate -> {
                List<Match> matchesCopy = copy(matches);
                root.recursiveMatch(rootCandidate, matchesCopy);
                return matchesCopy.stream();
            }).toList();
            matches.clear();
            matches.addAll(rootMatches);
            // root matches from the nth root get passed on to the (n+1)th root
        });
        return matches.stream().sorted().toList();
    }

    @Override
    public boolean validate(Match match) {
        Map<NodePattern<?>, List<? extends Node>> rootCandidates = getRoots().stream()
                .collect(Collectors.toMap(root -> root, root -> List.of(match.get(root))));
        List<Match> matches = match(rootCandidates);
        return matches.stream().anyMatch(match::equals);
    }

    @Override
    public void compareTo(GraphPattern targetPattern, BiConsumer<NodePattern<?>, NodePattern<?>> compareFunction) {
        MultiGraphPattern multiTarget = (MultiGraphPattern) targetPattern;
        assert this.subgraphs.size() == multiTarget.subgraphs.size();
        for (int i = 0; i < this.subgraphs.size(); i++) {
            SimpleGraphPattern<?> srcSubgraph = subgraphs.get(i);
            SimpleGraphPattern<?> tgtSubgraph = multiTarget.subgraphs.get(i);
            compareFunction.accept(srcSubgraph.getRoot(), tgtSubgraph.getRoot());
        }
    }

}
