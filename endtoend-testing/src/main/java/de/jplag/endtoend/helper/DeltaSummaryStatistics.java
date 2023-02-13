package de.jplag.endtoend.helper;

import java.util.DoubleSummaryStatistics;

/**
 * Summary statistics when evaluating the delta between two double values. This class manages two
 * {@link DoubleSummaryStatistics} and delegates the delta of the accepted values to the statistics depending on the
 * sign of the delta. In contrast to normal {@link DoubleSummaryStatistics}, negative and positive deltas do not cancel
 * each other out in the average value, as two separate statistics are managed. A delta of zero is not stored.
 */
public class DeltaSummaryStatistics {
    private final DoubleSummaryStatistics positive;
    private final DoubleSummaryStatistics negative;

    /**
     * Creates the delta summary statistics.
     */
    public DeltaSummaryStatistics() {
        positive = new FormattedDoubleSummaryStatistics();
        negative = new FormattedDoubleSummaryStatistics();
    }

    /**
     * Calculates the delta of the two double values and delegates it to the statistics depending on the sign of the delta.
     * A delta of zero is not stored.
     * @param first is the first double value.
     * @param second is the second double value.
     */
    public void accept(double first, double second) {
        double delta = first - second;
        if (delta > 0) {
            positive.accept(delta);
        } else if (delta < 0) {
            negative.accept(-delta); // delta is absolute
        }
    }

    /**
     * @return the {@link DoubleSummaryStatistics} of the positive delta values.
     */
    public DoubleSummaryStatistics getPositiveStatistics() {
        return positive;
    }

    /**
     * @return the {@link DoubleSummaryStatistics} of the negaitve delta values.
     */
    public DoubleSummaryStatistics getNegativeStatistics() {
        return negative;
    }

    /**
     * Customized implementation of {@link DoubleSummaryStatistics} with a customized textual representation.
     */
    private class FormattedDoubleSummaryStatistics extends DoubleSummaryStatistics {
        @Override
        public String toString() {
            return String.format("count=%d, average=%.4f, min=%.4f, max=%.4f", getCount(), getAverage(), getMin(), getMax());
        }
    }

}
