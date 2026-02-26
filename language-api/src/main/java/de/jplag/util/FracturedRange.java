package de.jplag.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FracturedRange<T extends Comparable<T>> {
    private List<Range<T>> continuousRanges;

    private FracturedRange(List<Range<T>> subRanges) {
        this.continuousRanges = subRanges;
    }

    public FracturedRange(Range<T> range) {
        this(List.of(range));
    }

    public FracturedRange() {
        this(Collections.emptyList());
    }

    public FracturedRange<T> mergeWith(Range<T> newRange) {
        List<Range<T>> copy = new ArrayList<>(continuousRanges);

        for (Range<T> continuousRange : continuousRanges) {
            if (newRange.touches(continuousRange)) {
                copy.remove(continuousRange);
                return new FracturedRange(copy).mergeWith(continuousRange.merge(newRange));
            }
        }

        copy.add(newRange);
        return new FracturedRange<>(copy);
    }

    public FracturedRange<T> mergeWith(FracturedRange<T> other) {
        FracturedRange mergedRange = new FracturedRange(this.continuousRanges);

        for (Range<T> continuousRange : other.getContinuousRanges()) {
            mergedRange = mergedRange.mergeWith(continuousRange);
        }

        return mergedRange;
    }

    public static <T extends Comparable<T>> FracturedRangeBuilder<T> builder() {
        return new FracturedRangeBuilder<>();
    }

    public boolean overlaps(Range<T> range) {
        return continuousRanges.stream().anyMatch(it -> it.overlaps(range));
    }

    public boolean overlaps(FracturedRange<T> other) {
        return continuousRanges.stream().anyMatch(it -> other.overlaps(it));
    }

    public List<Range<T>> getContinuousRanges() {
        return continuousRanges;
    }

    public static class FracturedRangeBuilder<T extends Comparable<T>> {
        private FracturedRange<T> current = new FracturedRange<>(Collections.emptyList());

        private FracturedRangeBuilder() {
        }

        public FracturedRangeBuilder add(Range<T> newRange) {
            current = current.mergeWith(newRange);
            return this;
        }

        public FracturedRange<T> build() {
            return current;
        }
    }
}
