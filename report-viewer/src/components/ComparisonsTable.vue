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
          <div class="tableCellNumber"></div>
          <div class="tableCellName items-center">Submissions in Comparison</div>
          <div class="tableCellSimilarity !flex-col">
            <div>Similarity</div>
            <div class="flex w-full flex-row">
              <ToolTipComponent class="flex-1" :direction="displayClusters ? 'top' : 'left'">
                <template #default>
                  <p class="w-full text-center">
                    {{ metricToolTips[MetricType.AVERAGE].shortName }}
                  </p>
                </template>
                <template #tooltip>
                  <p class="whitespace-pre text-sm">
                    {{ metricToolTips[MetricType.AVERAGE].tooltip }}
                  </p>
                </template>
              </ToolTipComponent>

              <ToolTipComponent class="flex-1" :direction="displayClusters ? 'top' : 'left'">
                <template #default>
                  <p class="w-full text-center">
                    {{ metricToolTips[MetricType.MAXIMUM].shortName }}
                  </p>
                </template>
                <template #tooltip>
                  <p class="whitespace-pre text-sm">
                    {{ metricToolTips[MetricType.MAXIMUM].tooltip }}
                  </p>
                </template>
              </ToolTipComponent>
            </div>
          </div>
          <div class="tableCellCluster items-center" v-if="displayClusters">Cluster</div>
        </div>
      </div>

      <!-- Body -->
      <div class="flex flex-grow flex-col overflow-hidden">
        <DynamicScroller
          v-if="topComparisons.length > 0"
          :items="displayedComparisons"
          :min-item-size="48"
          ref="dynamicScroller"
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
                  '!bg-accent !bg-opacity-30': isHighlightedRow(item)
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
                  class="flex flex-grow cursor-pointer flex-row"
                >
                  <!-- Index in sorted list -->
                  <div class="tableCellNumber">
                    <div class="w-full text-center">{{ item.sortingPlace + 1 }}</div>
                  </div>

                  <!-- Names -->
                  <div class="tableCellName">
                    <NameElement :id="item.firstSubmissionId" class="h-full w-1/2 px-2" />
                    <NameElement :id="item.secondSubmissionId" class="h-full w-1/2 px-2" />
                  </div>

                  <!-- Similarities -->
                  <div class="tableCellSimilarity">
                    <div class="w-1/2">
                      {{ (item.similarities[MetricType.AVERAGE] * 100).toFixed(2) }}%
                    </div>
                    <div class="w-1/2">
                      {{ (item.similarities[MetricType.MAXIMUM] * 100).toFixed(2) }}%
                    </div>
                  </div>
                </RouterLink>

                <!-- Clusters -->
                <div class="tableCellCluster flex !flex-col items-center" v-if="displayClusters">
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
                        <p class="whitespace-nowrap text-sm">
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
import { generateColors } from '@/utils/ColorUtils'
import ToolTipComponent from './ToolTipComponent.vue'
import { MetricType, metricToolTips } from '@/model/MetricType'
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
    required: false
  },
  header: {
    type: String,
    default: 'Top Comparisons:'
  },
  highlightedRowIds: {
    type: Object as PropType<{ firstId: string; secondId: string }>,
    required: false
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
 * It updates the displayed comparisons to only show the ones that  have part of any search result in their id. The search is not case sensitive. The parts can be separated by commas or spaces.
 * It also updates the anonymous set to unhide a submission if its name was typed in the search bar at any point in time.
 *
 * @param newVal The new value of the search bar
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

  const nameSearches = searches.filter((s) => !/index:[0-9]+/.test(s))
  const indexSearches = searches
    .filter((s) => /index:[0-9]+/.test(s))
    .map((s) => s.substring(6))
    .map((s) => parseInt(s))

  return comparisons.filter((c) => {
    const id1 = c.firstSubmissionId.toLowerCase()
    const id2 = c.secondSubmissionId.toLowerCase()
    if (nameSearches.some((s) => id1.includes(s) || id2.includes(s))) {
      return true
    }
    if (indexSearches.includes(c.sortingPlace + 1)) {
      return true
    }
    if (nameSearches.some((s) => (c.sortingPlace + 1).toString().includes(s))) {
      return true
    }

    return false
  })
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

let clusterIconColors = [] as Array<string>
if (props.clusters != undefined) {
  clusterIconColors = generateColors(props.clusters.length, 0.8, 0.5, 1)
}

function isHighlightedRow(item: ComparisonListElement) {
  return (
    props.highlightedRowIds != undefined &&
    ((item.firstSubmissionId == props.highlightedRowIds.firstId &&
      item.secondSubmissionId == props.highlightedRowIds.secondId) ||
      (item.firstSubmissionId == props.highlightedRowIds.secondId &&
        item.secondSubmissionId == props.highlightedRowIds.firstId))
  )
}

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

<style scoped lang="postcss">
.tableRow {
  @apply flex flex-row text-center;
}

.tableCellNumber {
  @apply tableCell w-12 flex-shrink-0;
}

.tableCellSimilarity {
  @apply tableCell w-40 flex-shrink-0;
}

.tableCellCluster {
  @apply tableCell w-32 flex-shrink-0;
}

.tableCellName {
  @apply tableCell flex-grow;
}

.tableCell {
  @apply mx-3 flex flex-row items-center justify-center text-center;
}

/* Tooltip arrow. Defined down here bacause of the content attribute */
.tooltipArrow::after {
  content: ' ';
  position: absolute;
  top: 50%;
  left: 100%;
  margin-top: -5px;
  border-width: 5px;
  border-style: solid;
  border-color: transparent transparent transparent rgba(0, 0, 0, 0.9);
}
</style>
