package de.jplag;

import java.util.List;

import de.jplag.util.FracturedRange;
import de.jplag.util.Pair;
import de.jplag.util.Range;

public class TokenRange {
    private FracturedRange<Integer> indexRange;

    private TokenRange(FracturedRange<Integer> indices) {
        this.indexRange = new FracturedRange<>();
    }

    public TokenRange(int start, int end) {
        this.indexRange = new FracturedRange<>(new Range<>(start, end + 1));
    }

    public TokenRange() {
        this.indexRange = new FracturedRange<>();
    }

    public TokenRange(List<Pair<Integer, Integer>> subRanges) {
        this();
        for (Pair<Integer, Integer> range : subRanges) {
            addRange(range.getFirst(), range.getSecond());
        }
    }

    public void addRange(int start, int end) {
        indexRange = indexRange.mergeWith(new Range<>(start, end + 1));
    }

    public int getFirstToken() {
        return indexRange.getContinuousRanges().stream().mapToInt(Range::getStart).min().orElse(-1);
    }

    public int getLastToken() {
        return indexRange.getContinuousRanges().stream().mapToInt(Range::getStart).max().orElse(-1);
    }

    public int tokenCount() {
        return indexRange.getContinuousRanges().stream().mapToInt(it -> it.getEnd() - it.getStart()).sum();
    }

    public boolean overlaps(TokenRange other) {
        return indexRange.overlaps(other.indexRange);
    }

    public TokenRange mergedWith(TokenRange other) {
        return new TokenRange(indexRange.mergeWith(other.indexRange));
    }

    public TokenRange reArrangeByNewOrder(List<Integer> newOrder) {
        int[] inverseNewOrderMap = new int[newOrder.size()];
        for (int i = 0; i < newOrder.size(); i++) {
            inverseNewOrderMap[newOrder.get(i)] = i;
        }

        TokenRange reOrdered = new TokenRange();
        for (Range<Integer> range : indexRange.getContinuousRanges()) {
            for (int i = range.getStart(); i < range.getEnd(); i++) {
                reOrdered.addRange(inverseNewOrderMap[i], inverseNewOrderMap[i] + 1);
            }
        }

        return reOrdered;
    }
}
