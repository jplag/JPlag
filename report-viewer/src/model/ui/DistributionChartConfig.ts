import type { MetricType } from '../MetricType'

/**
 * Configuration for the distribution chart.
 */
export interface DistributionChartConfig {
  metric: MetricType
  xScale: 'linear' | 'logarithmic'
}
