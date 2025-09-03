import { MetricJsonIdentifier } from '@jplag/model'
import { getUseDarkModeSetting, saveUseDarkModeSetting } from '@jplag/ui-components/base'
import {
  Column,
  ComparisonTableSorting,
  Direction,
  DistributionChartConfig,
  FileSortingOptions
} from '@jplag/ui-components/widget'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const uiStore = defineStore('uiStore', () => {
  const _useDarkMode = ref(getUseDarkModeSetting())
  const useDarkMode = computed({
    get: () => _useDarkMode.value,
    set: (v: boolean) => {
      saveUseDarkModeSetting(v)
      _useDarkMode.value = v
    }
  })

  const distributionChartConfig = ref<DistributionChartConfig>({
    metric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
    xScale: 'linear',
    bucketCount: 10
  })
  const comparisonTableSorting = ref<ComparisonTableSorting>({
    column: Column.averageSimilarity,
    direction: Direction.descending
  })
  const secondaryTableMetric = ref<MetricJsonIdentifier>(MetricJsonIdentifier.MAXIMUM_SIMILARITY)
  const fileSorting = ref<FileSortingOptions>(FileSortingOptions.ALPHABETICAL)

  return {
    useDarkMode,
    distributionChartConfig,
    comparisonTableSorting,
    secondaryTableMetric,
    fileSorting
  }
})
