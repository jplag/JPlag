package de.jplag.scxml.parser;

import java.util.ArrayList;
import java.util.List;

import de.jplag.scxml.ScxmlTokenAttribute;
import de.jplag.scxml.parser.model.StatechartElement;

/**
 * A parser adapter that provides a way to retrieve a list of token types. When a token is added, only the ordinal of
 * its type is stored. This can be used to "peek" at a list of token types that are extracted when visiting a
 * statechart.
 */
public class PeekAdapter extends ScxmlParserAdapter {

    private final List<Integer> tokenTypes = new ArrayList<>();

    /**
     * Lexicographically compares two lists of integer representations / ordinals of token types.
     * @param first the first list of ordinals of token types
     * @param second the second list of ordinals of token types
     * @return 0 if the lists are equal, a negative integer if the first list is lexicographically less than the second
     * list, or a positive integer if the first list is lexicographically greater than the second list
     */
    public static int compareTokenTypeLists(List<Integer> first, List<Integer> second) {
        int size = Math.min(first.size(), second.size());
        for (int i = 0; i < size; i++) {
            int result = Integer.compare(first.get(i), second.get(i));
            if (result != 0) {
                return result;
            }
        }
        return Integer.compare(first.size(), second.size());
    }

    @Override
    public void addToken(ScxmlTokenAttribute type, StatechartElement source) {
        tokenTypes.add(type.ordinal());
    }

    /**
     * @return the currently extracted list of token types
     */
    public List<Integer> getTokenTypes() {
        return tokenTypes;
    }
}
