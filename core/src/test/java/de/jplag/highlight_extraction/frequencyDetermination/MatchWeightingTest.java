package de.jplag.highlight_extraction.frequencyDetermination;

import static de.jplag.highlight_extraction.StrategyMethods.createKey;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import de.jplag.Match;
import de.jplag.TestBase;
import de.jplag.highlight_extraction.MatchWeighting;
import de.jplag.highlight_extraction.FrequencyStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MatchWeightingTest extends TestBase {
    private static class DummyStrategy implements FrequencyStrategy {
        @Override
        public void create(List<String> tokens, String comparisonId, Map<String,List<String>> map, int size) {
            String key = createKey(tokens);
            map.computeIfAbsent(key, k -> new java.util.ArrayList<>());
        }

        @Override
        public void check(List<String> tokens, String comparisonId, Map<String,List<String>> map, int size) {
            String key = createKey(tokens);
            map.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(comparisonId);
        }

        @Override
        public double calculateWeight(Match match, Map<String, List<String>> frequencyMap, List<String> matchToken) {
            return matchToken.size();
        }
    }

    @Test
    @DisplayName("Match richtig gewichtet")
    void testWeightMatch_setsCorrectWeight() {
        FrequencyStrategy strategy = new DummyStrategy();
        Map<String, List<String>> frequencyMap = new HashMap<>();
        MatchWeighting weighting = new MatchWeighting(strategy, frequencyMap);

        Match match = new Match(0, 0, 3, 3); // Länge = 3
        List<String> tokens = List.of("a", "b", "c");

        weighting.weightMatch(match, tokens);

        assertEquals(3.0, match.getFrequencyWeight(), 0.01, "Match-Weight  == Token-Anzahl ?");
    }

    @Test
    @DisplayName("Matches alle richtig gewichtet")
    void testWeightAllMatches_weightsAllCorrectly() {
        FrequencyStrategy strategy = new DummyStrategy();
        Map<String, List<String>> frequencyMap = new HashMap<>();
        MatchWeighting weighting = new MatchWeighting(strategy, frequencyMap);

        List<String> tokenStream = List.of("a", "b", "c", "d", "e");

        Match match1 = new Match(0, 0, 2,  2);
        Match match2 = new Match(2, 0, 3,  3);

        List<Match> matches = List.of(match1, match2);

        weighting.weightAllMatches(matches, tokenStream);

        assertEquals(2.0, match1.getFrequencyWeight(), 0.01);
        assertEquals(3.0, match2.getFrequencyWeight(), 0.01);
    }

    @Test
    @DisplayName("Match das nicht gefunden wird wirft keinen fehler")
    void testWeightAllMatches_skipsOutOfBoundsMatch() {
        FrequencyStrategy strategy = new DummyStrategy();
        Map<String, List<String>> frequencyMap = new HashMap<>();
        MatchWeighting weighting = new MatchWeighting(strategy, frequencyMap);

        List<String> tokenStream = List.of("x", "y", "z");

        Match validMatch = new Match(0, 0, 2,  2);
        Match invalidMatch = new Match(2, 0, 5,  5);

        List<Match> matches = List.of(validMatch, invalidMatch);

        weighting.weightAllMatches(matches, tokenStream);

        assertEquals(2.0, validMatch.getFrequencyWeight(), 0.01);
        assertEquals(0.0, invalidMatch.getFrequencyWeight(), 0.01, "Match sollte Weight = 0 haben");
    }
}
