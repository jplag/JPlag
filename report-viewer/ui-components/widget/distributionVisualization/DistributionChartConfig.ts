import type { BucketOptions, MetricJsonIdentifier } from '@jplag/model'

/**
 * Configuration for the distribution chart.
 */
export interface DistributionChartConfig {
  metric: MetricJsonIdentifier
  xScale: 'linear' | 'logarithmic'
  bucketCount: BucketOptions
}
