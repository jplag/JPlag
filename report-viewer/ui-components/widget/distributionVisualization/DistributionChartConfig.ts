import type { BucketOptions, DistributionMetrics } from '@jplag/model'

/**
 * Configuration for the distribution chart.
 */
export interface DistributionChartConfig {
  metric: DistributionMetrics
  xScale: 'linear' | 'logarithmic'
  bucketCount: BucketOptions
}
