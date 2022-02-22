import {ComparisonListElement} from "./ComparisonListElement";

/**
 * Metric used in the Jplag Comparison
 */
export type Metric = {
    metricName: string,
    metricThreshold: number,
    distribution: Array<number>,
    comparisons: Array<ComparisonListElement>
}