import { MetricJsonIdentifier } from '@jplag/model'
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
  const _useDarkMode = ref(getDefaultUseDarkModeOption())
  const useDarkMode = computed({
    get: () => _useDarkMode.value,
    set: (v: boolean) => {
      localStorage.setItem(USE_DARK_MODE_KEYWORD, v ? 'true' : 'false')
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

const USE_DARK_MODE_KEYWORD = 'jplag:use-dark-mode'

function getDefaultUseDarkModeOption() {
  const local = localStorage.getItem(USE_DARK_MODE_KEYWORD)
  if (local !== null) {
    return local === 'true'
  }

  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
}
