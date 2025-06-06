import type { BucketOptions } from '../Distribution'
import type { MetricJsonIdentifier } from '../MetricJsonIdentifier'

/**
 * Configuration for the distribution chart.
 */
export interface DistributionChartConfig {
  metric: MetricJsonIdentifier
  xScale: 'linear' | 'logarithmic'
  bucketCount: BucketOptions
}
