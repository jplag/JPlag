import type { ComparisonListElement } from './ComparisonListElement'

/**
 * Metric used in the Jplag Comparison
 * @property metricName - Name of the metric.
 * @property description - Description of the metric.
 * @property metricThreshold - Threshold of the metric.
 * @property distribution - Distribution of the metric.
 * @property comparisons - Comparisons of the metric.
 */
export type Metric = {
  metricName: string
  description: string
  metricThreshold: number
  distribution: Array<number>
  comparisons: Array<ComparisonListElement>
}
