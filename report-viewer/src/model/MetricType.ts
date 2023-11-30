/**
 * This enum maps the metric type to the index they have in the generated JSON and respectivly in the store.
 */
export enum MetricType {
  AVERAGE = 'AVG',
  MAXIMUM = 'MAX'
}

export type MetricToolTipData = {
  longName: string
  shortName: string
  tooltip: string
}

export const metricToolTips: Record<MetricType, MetricToolTipData> = {
  [MetricType.AVERAGE]: {
    longName: 'Average Similarity',
    shortName: 'AVG',
    tooltip:
      'The average similarity of the two files.\nA high similarity indicates that the programms work in a similar way.'
  },
  [MetricType.MAXIMUM]: {
    longName: 'Maximum Similarity',
    shortName: 'MAX',
    tooltip:
      'The maximum similarity of the two files.\nUsefull if programms are very dfferent in size.'
  }
}
