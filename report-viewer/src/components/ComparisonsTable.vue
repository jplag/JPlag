<!--
  Table which display all of the comparisons with their participating ids and similarity percentage for the selected metric.
-->
<template>
  <div class="flex flex-col">
    <ComparisonTableFilter
      v-model:search-string="searchString"
      :enable-cluster-sorting="clusters != undefined"
      :header="header"
    />

    <div class="flex flex-col overflow-hidden">
      <div class="font-bold">
        <!-- Header -->
        <div class="tableRow">
          <div class="tableCellNumber tableCell"></div>
          <div class="tableCellName tableCell items-center">Submissions in Comparison</div>
          <div class="tableCellSimilarity tableCell flex-col!">
            <div>Similarity</div>
            <div class="flex w-full flex-row">
              <ToolTipComponent class="flex-1" :direction="displayClusters ? 'top' : 'left'">
                <template #default>
                  <p class="w-full text-center">
                    {{ MetricTypes.AVERAGE_SIMILARITY.shortName }}
                  </p>
                </template>
                <template #tooltip>
                  <p class="whitespace-pre text-sm">
                    {{ MetricTypes.AVERAGE_SIMILARITY.tooltip }}
                  </p>
                </template>
              </ToolTipComponent>

              <ToolTipComponent class="flex-1" :direction="displayClusters ? 'top' : 'left'">
                <template #default>
                  <p class="w-full text-center">
                    {{
                      MetricTypes.METRIC_MAP[store().uiState.comparisonTableSecondaryMetric]
                        .shortName
                    }}
                  </p>
                </template>
                <template #tooltip>
                  <p class="whitespace-pre text-sm">
                    {{
                      MetricTypes.METRIC_MAP[store().uiState.comparisonTableSecondaryMetric].tooltip
                    }}
                  </p>
                </template>
              </ToolTipComponent>
            </div>
          </div>
          <div v-if="displayClusters" class="tableCellCluster tableCell items-center">Cluster</div>
        </div>
      </div>

      <!-- Body -->
      <div class="flex grow flex-col overflow-hidden">
        <DynamicScroller
          v-if="topComparisons.length > 0"
          ref="dynamicScroller"
          :items="displayedComparisons"
          :min-item-size="48"
          ><template #default="{ item, index, active }">
            <DynamicScrollerItem
              :item="item"
              :active="active"
              :size-dependencies="[
                item.firstSubmissionId,
                item.secondSubmissionId,
                store().isAnonymous(item.firstSubmissionId),
                store().isAnonymous(item.secondSubmissionId)
              ]"
              :data-index="index"
            >
              <!-- Row -->
              <div
                class="tableRow"
                :class="{
                  'bg-container-secondary-light dark:bg-container-secondary-dark': item.id % 2 == 1,
                  'bg-accent/30!': isHighlightedRow(item)
                }"
              >
                <RouterLink
                  :to="{
                    name: 'ComparisonView',
                    params: {
                      comparisonFileName: store().getComparisonFileName(
                        item.firstSubmissionId,
                        item.secondSubmissionId
                      )
                    }
                  }"
                  class="flex grow cursor-pointer flex-row"
                >
                  <!-- Index in sorted list -->
                  <div class="tableCellNumber tableCell">
                    <div class="w-full text-center">{{ item.sortingPlace + 1 }}</div>
                  </div>

                  <!-- Names -->
                  <div class="tableCellName tableCell">
                    <NameElement :id="item.firstSubmissionId" class="h-full w-1/2 px-2" />
                    <NameElement :id="item.secondSubmissionId" class="h-full w-1/2 px-2" />
                  </div>

                  <!-- Similarities -->
                  <div class="tableCellSimilarity tableCell">
                    <div class="w-1/2">
                      {{
                        MetricTypes.AVERAGE_SIMILARITY.format(
                          item.similarities[MetricTypes.AVERAGE_SIMILARITY.identifier]
                        )
                      }}
                    </div>
                    <div class="w-1/2">
                      {{
                        MetricTypes.METRIC_MAP[
                          store().uiState.comparisonTableSecondaryMetric
                        ].format(item.similarities[store().uiState.comparisonTableSecondaryMetric])
                      }}
                    </div>
                  </div>
                </RouterLink>

                <!-- Clusters -->
                <div
                  v-if="displayClusters"
                  class="tableCellCluster tableCell flex flex-col! items-center"
                >
                  <RouterLink
                    v-if="item.clusterIndex >= 0"
                    :to="{
                      name: 'ClusterView',
                      params: { clusterIndex: item.clusterIndex }
                    }"
                    class="flex w-full justify-center text-center"
                  >
                    <ToolTipComponent
                      class="w-fit"
                      direction="left"
                      :tool-tip-container-will-be-centered="true"
                    >
                      <template #default>
                        {{ clusters?.[item.clusterIndex].members?.length }}
                        <FontAwesomeIcon
                          :icon="['fas', 'user-group']"
                          :style="{ color: clusterIconColors[item.clusterIndex] }"
                        />
                        {{
                          (
                            (clusters?.[item.clusterIndex].averageSimilarity as number) * 100
                          ).toFixed(2)
                        }}%
                      </template>
                      <template #tooltip>
                        <p class="text-sm whitespace-nowrap">
                          {{ clusters?.[item.clusterIndex].members?.length }} submissions in cluster
                          with average similarity of
                          {{
                            (
                              (clusters?.[item.clusterIndex].averageSimilarity as number) * 100
                            ).toFixed(2)
                          }}%
                        </p>
                      </template>
                    </ToolTipComponent>
                  </RouterLink>
                </div>
              </div>
            </DynamicScrollerItem>
          </template>

          <template #after>
            <slot name="footer"></slot>
          </template>
        </DynamicScroller>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Cluster } from '@/model/Cluster'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import { type PropType, watch, computed, ref, type Ref } from 'vue'
import { store } from '@/stores/store'
import { DynamicScroller, DynamicScrollerItem } from 'vue-virtual-scroller'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faUserGroup } from '@fortawesome/free-solid-svg-icons'
import { generateHues } from '@/utils/ColorUtils'
import ToolTipComponent from './ToolTipComponent.vue'
import { MetricJsonIdentifier, MetricTypes } from '@/model/MetricType'
import NameElement from './NameElement.vue'
import ComparisonTableFilter from './ComparisonTableFilter.vue'

library.add(faUserGroup)

const props = defineProps({
  topComparisons: {
    type: Array<ComparisonListElement>,
    required: true
  },
  clusters: {
    type: Array<Cluster>,
    required: false,
    default: undefined
  },
  header: {
    type: String,
    default: 'Top Comparisons:'
  },
  highlightedRowIds: {
    type: Object as PropType<{ firstId: string; secondId: string }>,
    required: false,
    default: undefined
  }
})

const displayedComparisons = computed(() => {
  const comparisons = getFilteredComparisons(getSortedComparisons(Array.from(props.topComparisons)))
  let index = 1
  comparisons.forEach((c) => {
    c.id = index++
  })
  return comparisons
})

const searchString = ref('')

/**
 * This function gets called when the search bar for the comparison table has been updated.
 * It returns the input list, with the filter given in searchString applied.
 *
 * @param comparisons Sorted list of comparisons
 */
function getFilteredComparisons(comparisons: ComparisonListElement[]) {
  const searches = searchString.value
    .trimEnd()
    .toLowerCase()
    .split(/ +/g)
    .map((s) => s.trim().replace(/,/g, ''))
  if (searches.length == 0) {
    return comparisons
  }

  const indexSearches = searches
    .filter((s) => /index:[0-9]+/.test(s))
    .map((s) => s.substring(6))
    .map((s) => parseInt(s))

  const metricSearches = searches.filter((s) => /((avg|max):)?([<>])=?[0-9]+%?/.test(s))

  return comparisons.filter((c) => {
    // name search
    const name1 = store().submissionDisplayName(c.firstSubmissionId).toLowerCase()
    const name2 = store().submissionDisplayName(c.secondSubmissionId).toLowerCase()
    if (searches.some((s) => name1.includes(s) || name2.includes(s))) {
      return true
    }

    // index search
    if (indexSearches.includes(c.sortingPlace + 1)) {
      return true
    }
    if (searches.some((s) => (c.sortingPlace + 1).toString().includes(s))) {
      return true
    }

    // metric search
    const searchPerMetric: Record<MetricJsonIdentifier, string[]> = {} as Record<
      MetricJsonIdentifier,
      string[]
    >
    MetricTypes.METRIC_JSON_IDENTIFIERS.forEach((m) => {
      searchPerMetric[m] = []
    })
    metricSearches.forEach((s) => {
      const regexResult = /^(?:(avg|max):)([<>]=?[0-9]+%?$)/.exec(s)
      if (regexResult) {
        const metricName = regexResult[1]
        let metric = MetricTypes.AVERAGE_SIMILARITY
        for (const m of MetricTypes.METRIC_LIST) {
          if (m.shortName.toLowerCase() == metricName) {
            metric = m
            break
          }
        }
        searchPerMetric[metric.identifier].push(regexResult[2])
      } else {
        MetricTypes.METRIC_JSON_IDENTIFIERS.forEach((m) => {
          searchPerMetric[m].push(s)
        })
      }
    })
    for (const metric of MetricTypes.METRIC_JSON_IDENTIFIERS) {
      for (const search of searchPerMetric[metric]) {
        const regexResult = /([<>]=?)([0-9]+)%?/.exec(search)!
        const operator = regexResult[1]
        const value = parseInt(regexResult[2])
        if (evaluateMetricComparison(c.similarities[metric] * 100, operator, value)) {
          return true
        }
      }
    }

    return false
  })

  function evaluateMetricComparison(
    comparisonMetric: number,
    operator: string,
    checkValue: number
  ) {
    switch (operator) {
      case '>':
        return comparisonMetric > checkValue
      case '<':
        return comparisonMetric < checkValue
      case '>=':
        return comparisonMetric >= checkValue
      case '<=':
        return comparisonMetric <= checkValue
      default:
        return false
    }
  }
}

function getSortedComparisons(comparisons: ComparisonListElement[]) {
  comparisons.sort(
    (a, b) =>
      b.similarities[store().uiState.comparisonTableSortingMetric] -
      a.similarities[store().uiState.comparisonTableSortingMetric]
  )

  if (store().uiState.comparisonTableClusterSorting) {
    comparisons.sort((a, b) => b.clusterIndex - a.clusterIndex)

    comparisons.sort(
      (a, b) =>
        getClusterFor(b.clusterIndex).averageSimilarity -
        getClusterFor(a.clusterIndex).averageSimilarity
    )
  }

  let index = 0
  comparisons.forEach((c) => {
    c.sortingPlace = index++
  })
  return comparisons
}

function getClusterFor(clusterIndex: number) {
  if (clusterIndex < 0 || !props.clusters) {
    return { averageSimilarity: 0 }
  }
  return props.clusters[clusterIndex]
}

const displayClusters = props.clusters != undefined

let clusterIconHues = [] as Array<number>
const lightmodeSaturation = 80
const lightmodeLightness = 50
const lightmodeAlpha = 0.3
const darkmodeSaturation = 90
const darkmodeLightness = 65
const darkmodeAlpha = 0.6
if (props.clusters != undefined) {
  clusterIconHues = generateHues(props.clusters.length)
}
const clusterIconColors = computed(() =>
  clusterIconHues.map((h) => {
    return `hsla(${h}, ${
      store().uiState.useDarkMode ? darkmodeSaturation : lightmodeSaturation
    }%, ${
      store().uiState.useDarkMode ? darkmodeLightness : lightmodeLightness
    }%, ${store().uiState.useDarkMode ? darkmodeAlpha : lightmodeAlpha})`
  })
)

function isHighlightedRow(item: ComparisonListElement) {
  return (
    props.highlightedRowIds != undefined &&
    ((item.firstSubmissionId == props.highlightedRowIds.firstId &&
      item.secondSubmissionId == props.highlightedRowIds.secondId) ||
      (item.firstSubmissionId == props.highlightedRowIds.secondId &&
        item.secondSubmissionId == props.highlightedRowIds.firstId))
  )
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const dynamicScroller: Ref<any | null> = ref(null)

watch(
  computed(() => props.highlightedRowIds),
  (newValue, oldValue) => {
    if (
      newValue != undefined &&
      (newValue?.firstId != oldValue?.firstId || newValue?.secondId != oldValue?.secondId)
    ) {
      dynamicScroller.value?.scrollToItem(props.topComparisons.findIndex(isHighlightedRow))
    }
  }
)
</script>

<style scoped>
@reference "../style.css";

.tableCell {
  @apply mx-3 flex flex-row items-center justify-center text-center;
}

.tableRow {
  @apply flex flex-row text-center;
}

.tableCellNumber {
  @apply w-12 shrink-0;
}

.tableCellSimilarity {
  @apply w-40 shrink-0;
}

.tableCellCluster {
  @apply w-32 shrink-0;
}

.tableCellName {
  @apply grow;
}
</style>
