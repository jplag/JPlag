import { MetricJsonIdentifier } from "@jplag/model";
import { Column, ComparisonTableSorting, Direction, DistributionChartConfig, FileSortingOptions } from "@jplag/ui-components/widget";
import { defineStore } from "pinia";
import { ref } from "vue";

export const uiStore = defineStore("uiStore", () => {
  const useDarkMode = ref(false);
  const distributionChartConfig = ref<DistributionChartConfig>({
    metric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
    xScale: 'linear',
    bucketCount: 10
  })
  const comparisonTableSorting = ref<ComparisonTableSorting>({
    column: Column.averageSimilarity,
    direction: Direction.descending
  })
  const fileSorting = ref<FileSortingOptions>(FileSortingOptions.ALPHABETICAL)

  return { useDarkMode, distributionChartConfig, comparisonTableSorting, fileSorting }
})