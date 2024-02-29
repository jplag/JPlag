package de.jplag.java_cpg.transformation.matching.pattern;

import com.google.common.collect.Iterators;
import de.fraunhofer.aisec.cpg.graph.Node;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class MultiGraphPattern extends GraphPatternImpl {
    private final List<SimpleGraphPattern<?>> subgraphs;

    public MultiGraphPattern(List<SimpleGraphPattern<?>> subgraphs, PatternRegistry patterns) {
        super(patterns);
        this.subgraphs = subgraphs;
    }

    @Override
    public List<NodePattern<?>> getRoots() {
        return subgraphs.stream().map(SimpleGraphPattern::getRoot).collect(Collectors.toList());

    }

    @Override
    public Iterator<Match> multiMatch(Map<NodePattern<?>, List<? extends Node>> rootCandidateMap) {
        List<Match> matches = new ArrayList<>();
        matches.add(new Match(this));
        getRoots().forEach(root -> {
            if (matches.isEmpty()) return;
            List<? extends Node> rootCandidates = rootCandidateMap.get(root);
            List<Match> rootMatches = rootCandidates.stream().flatMap(rootCandidate -> {
                List<Match> matchesCopy = copy(matches);
                root.recursiveMatch(rootCandidate, matchesCopy, null);
                return matchesCopy.stream();
            }).toList();
            matches.clear();
            matches.addAll(rootMatches);
            // root matches from the nth root get passed on to the (n+1)th root
        });
        return matches.stream().sorted().toList().iterator();
    }

    @Override
    public boolean validate(Match match) {
        Map<NodePattern<?>, List<? extends Node>> rootCandidates = getRoots().stream().collect(Collectors.toMap(
            root -> root,
            root -> List.of(match.get(root))
        ));
        Iterator<Match> matches = multiMatch(rootCandidates);
        while (matches.hasNext()) {
            if (matches.next().equals(match)) return true;
        }
        return false;
    }

    @Override
    public List<Class<? extends Node>> getCandidateNodeClasses() {
        return null;
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

    @Override
    public <T extends Node> List<Match> recursiveMatch(T candidate) {
        return null;
    }
}
