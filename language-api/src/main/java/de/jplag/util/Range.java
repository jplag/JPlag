package de.jplag.util;

/**
 * Represents a range. The start is always inclusive, the end exclusive
 * @param <T>
 */
public class Range<T extends Comparable<T>> {
    T start;
    T end;

    public Range(T start, T end) {
        this.start = start;
        this.end = end;
    }

    public T getStart() {
        return start;
    }

    public T getEnd() {
        return end;
    }

    boolean touches(Range<T> other) {
        return start.compareTo(other.end) <= 0 && other.start.compareTo(end) <= 0;
    }

    boolean overlaps(Range<T> other) {
        return start.compareTo(other.end) < 0 && other.start.compareTo(end) < 0;
    }

    Range<T> merge(Range<T> other) {
        T mergedStart = ComparableUtils.min(start, other.start);
        T mergedEnd = ComparableUtils.max(end, other.end);
        return new Range<>(mergedStart, mergedEnd);
    }
}
