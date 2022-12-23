package de.jplag.normalization;

import java.util.List;

import de.jplag.Token;

public class Normalizer {

    private Normalizer() {
    }

    public static List<Token> normalize(List<Token> tokens) {
        List<TokenGroup> tokenGroups = TokenGroup.group(tokens);
        NormalizationGraph graph = new NormalizationGraph(tokenGroups);
        tokenGroups = graph.linearize();
        return TokenGroup.ungroup(tokenGroups);
    }
}
