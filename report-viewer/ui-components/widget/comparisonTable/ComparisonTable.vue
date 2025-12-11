<!--
  Table which display all of the comparisons with their participating ids and similarity percentage for the selected metric.
-->
<template>
  <div class="flex flex-col">
    <ComparisonTableFilter
      v-model:search-string="searchString"
      v-model:secondary-metric="secondaryMetricModel"
      :header="header"
      :all-are-anonymized="allAreAnonymized"
      @change-anonymous-for-all="emit('changeAnonymousForAll')"
    />

    <div class="flex flex-1 flex-col overflow-hidden">
      <div class="flex h-full max-h-full flex-col overflow-x-scroll">
        <div class="flex h-full max-h-full min-w-fit flex-col overflow-hidden">
          <div class="min-w-fit font-bold">
            <!-- Header -->
            <div class="tableRow">
              <div class="tableCellNumber tableCell"></div>
              <div class="tableCellName tableCell items-center">Submissions in Comparison</div>
              <div class="tableCellSimilarity tableCell flex-col!">
                <div>Similarity</div>
                <div class="flex w-full flex-row">
                  <ToolTipComponent
                    class="flex-1 cursor-pointer"
                    :direction="displayClusters ? 'top' : 'left'"
                    @click="setSorting('averageSimilarity')"
                  >
                    <template #default>
                      <p class="w-full text-center">
                        {{ MetricTypes.AVERAGE_SIMILARITY.shortName }}
                        <FontAwesomeIcon
                          :icon="
                            tableSorting.column.id == 'averageSimilarity'
                              ? tableSorting.direction.icon
                              : faSort
                          "
                          class="ml-1"
                        />
                      </p>
                    </template>
                    <template #tooltip>
                      <p class="max-w-80 text-sm whitespace-pre-wrap">
                        {{ MetricTypes.AVERAGE_SIMILARITY.tooltip }}
                      </p>
                    </template>
                  </ToolTipComponent>

                  <ToolTipComponent
                    class="flex-1 cursor-pointer"
                    :direction="displayClusters ? 'top' : 'left'"
                    @click="setSorting(secondaryMetric.sorting.id)"
                  >
                    <template #default>
                      <p class="w-full text-center">
                        {{ secondaryMetric.shortName }}
                        <FontAwesomeIcon
                          :icon="
                            tableSorting.column.id == secondaryMetric.sorting.id
                              ? tableSorting.direction.icon
                              : faSort
                          "
                          class="ml-1 cursor-pointer"
                        />
                      </p>
                    </template>
                    <template #tooltip>
                      <p class="max-w-80 text-sm whitespace-pre-wrap">
                        {{ secondaryMetric.tooltip }}
                      </p>
                    </template>
                  </ToolTipComponent>
                </div>
              </div>
              <div
                v-if="displayClusters"
                class="tableCellCluster tableCell cursor-pointer items-center"
                @click="setSorting('cluster')"
              >
                Cluster
                <FontAwesomeIcon
                  :icon="tableSorting.column.id == 'cluster' ? tableSorting.direction.icon : faSort"
                  class="ml-1"
                />
              </div>
            </div>
          </div>

          <!-- Body -->
          <div class="flex w-full grow flex-col overflow-hidden">
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
                    isAnonymous(item.firstSubmissionId),
                    isAnonymous(item.secondSubmissionId)
                  ]"
                  :data-index="index"
                  class="min-w-fit"
                >
                  <!-- Row -->
                  <div
                    class="tableRow min-w-fit"
                    :class="{
                      'bg-container-secondary-light dark:bg-container-secondary-dark':
                        item.id % 2 == 1,
                      'bg-accent/30!': isHighlightedRow(item)
                    }"
                    @mouseover="
                      () =>
                        emit('lineHovered', {
                          firstId: item.firstSubmissionId,
                          secondId: item.secondSubmissionId
                        })
                    "
                    @mouseleave="() => emit('lineHovered', null)"
                  >
                    <RouterLink
                      :to="{
                        name: 'ComparisonView',
                        params: {
                          firstSubmissionId: item.firstSubmissionId,
                          secondSubmissionId: item.secondSubmissionId
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
                        <NameElement
                          :display-name="getDisplayName(item.firstSubmissionId)"
                          :is-anonymous="isAnonymous(item.firstSubmissionId)"
                          :displayed-name="getDisplayName(item.firstSubmissionId)"
                          class="h-full w-1/2 px-2"
                          @change-anonymous="(e) => changeAnonymous(e, item.firstSubmissionId)"
                        />
                        <NameElement
                          :display-name="getDisplayName(item.secondSubmissionId)"
                          :is-anonymous="isAnonymous(item.secondSubmissionId)"
                          :displayed-name="getDisplayName(item.secondSubmissionId)"
                          class="h-full w-1/2 px-2"
                          @change-anonymous="(e) => changeAnonymous(e, item.secondSubmissionId)"
                        />
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
                          {{ secondaryMetric.format(item.similarities[secondaryMetricModel]) }}
                        </div>
                      </div>
                    </RouterLink>

                    <!-- Clusters -->
                    <div
                      v-if="displayClusters"
                      class="tableCellCluster tableCell flex flex-col! items-center"
                    >
                      <RouterLink
                        v-if="(item as ComparisonListElement).cluster"
                        :to="{
                          name: 'ClusterView',
                          params: { clusterIndex: item.cluster.index }
                        }"
                        class="flex w-full justify-center text-center"
                      >
                        <ToolTipComponent
                          class="w-fit"
                          direction="left"
                          :tool-tip-container-will-be-centered="true"
                        >
                          <template #default>
                            {{ item.cluster.members.length }}
                            <FontAwesomeIcon
                              :icon="faUserGroup"
                              :style="{ color: clusterIconColors[item.cluster.index] }"
                            />
                            {{ ((item.cluster.averageSimilarity as number) * 100).toFixed(2) }}%
                          </template>
                          <template #tooltip>
                            <p class="text-sm whitespace-nowrap">
                              {{ item.cluster.members?.length }} submissions in cluster with average
                              similarity of
                              {{ ((item.cluster.averageSimilarity as number) * 100).toFixed(2) }}%
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, watch, computed, ref, type Ref } from 'vue'
import { type Cluster, type ComparisonListElement, MetricJsonIdentifier } from '@jplag/model'
import { DynamicScroller, DynamicScrollerItem } from 'vue-virtual-scroller'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faSort, faUserGroup } from '@fortawesome/free-solid-svg-icons'
import { generateHues } from './HueGenerator'
import { ToolTipComponent } from '../../base'
import NameElement from '../NameElement.vue'
import ComparisonTableFilter from './ComparisonTableFilter.vue'
import { Column, ComparisonTableSorting, Direction, type ColumnId } from './ComparisonSorting'
import { MetricTypes } from '../MetricType'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'

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
  },
  getDisplayName: {
    type: Function as PropType<(id: string) => string>,
    required: false,
    default: (id: string) => id
  },
  getPlainDisplayName: {
    type: Function as PropType<(id: string) => string>,
    required: false,
    default: (id: string) => id
  },
  getAnonymousName: {
    type: Function as PropType<(id: string) => string>,
    required: false,
    default: (id: string) => id
  },
  isAnonymous: {
    type: Function as PropType<(id: string) => boolean>,
    required: false,
    default: () => false
  },
  allAreAnonymized: {
    type: Boolean,
    required: false,
    default: false
  },
  useDarkMode: {
    type: Boolean,
    required: false,
    default: false
  }
})

const emit = defineEmits<{
  (event: 'lineHovered', value: { firstId: string; secondId: string } | null): void
  (event: 'changeAnonymous', id: string): void
  (event: 'changeAnonymousForAll'): void
}>()

const secondaryMetricModel = defineModel<MetricJsonIdentifier>('secondaryMetric', {
  default: MetricJsonIdentifier.MAXIMUM_SIMILARITY
})
const tableSorting = defineModel<ComparisonTableSorting>('sorting', {
  default: {
    column: Column.columns.averageSimilarity,
    direction: Direction.descending
  }
})

const secondaryMetric = computed(() => MetricTypes.METRIC_MAP[secondaryMetricModel.value])

const displayedComparisons = computed(() => {
  const comparisons = getFilteredComparisons(getSortedComparisons(Array.from(props.topComparisons)))
  let index = 1
  comparisons.forEach((c) => {
    c.id = index++
  })
  return comparisons
})

const searchString = defineModel<string>('searchString', {
  default: ''
})

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

  const metricSearches = searches.filter((s) => /((avg|max|long|len):)?([<>])=?[0-9]+%?/.test(s))

  return comparisons.filter((c) => {
    // name search
    const name1 = props.getPlainDisplayName(c.firstSubmissionId).toLowerCase()
    const anonName1 = props.isAnonymous(c.firstSubmissionId)
      ? props.getAnonymousName(c.firstSubmissionId).toLowerCase()
      : ''
    const name2 = props.getPlainDisplayName(c.secondSubmissionId).toLowerCase()
    const anonName2 = props.isAnonymous(c.secondSubmissionId)
      ? props.getAnonymousName(c.secondSubmissionId).toLowerCase()
      : ''
    if (
      searches.some(
        (s) =>
          name1.includes(s) || name2.includes(s) || anonName1.includes(s) || anonName2.includes(s)
      )
    ) {
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
      const regexResult = /^(?:(avg|max|long|len):)([<>]=?[0-9]+%?$)/.exec(s)
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
        const metricObject = MetricTypes.METRIC_MAP[metric]
        if (
          evaluateMetricComparison(
            metricObject.getTableFilterValue(c.similarities[metric]),
            operator,
            value
          )
        ) {
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
  const sorting = tableSorting.value
  comparisons.sort((a, b) => {
    const numsA = sorting.column.value(a)
    const numsB = sorting.column.value(b)
    for (let i = 0; i < numsA.length; i++) {
      const comparison = sorting.direction.comparator(numsA[i], numsB[i])
      if (comparison != 0) {
        return comparison
      }
    }
    return 0
  })

  let index = 0
  comparisons.forEach((c) => {
    c.sortingPlace = index++
  })
  return comparisons
}

function setSorting(column: ColumnId) {
  if (tableSorting.value.column.id == column) {
    tableSorting.value.direction = tableSorting.value.direction.next
  } else {
    tableSorting.value.column = Column.columns[column]
    tableSorting.value.direction = Direction.descending
  }
}

const displayClusters = computed(() => props.clusters != undefined)

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
    return `hsla(${h}, ${props.useDarkMode ? darkmodeSaturation : lightmodeSaturation}%, ${
      props.useDarkMode ? darkmodeLightness : lightmodeLightness
    }%, ${props.useDarkMode ? darkmodeAlpha : lightmodeAlpha})`
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

function changeAnonymous(event: Event, submissionId: string) {
  event.stopPropagation()
  event.preventDefault()
  emit('changeAnonymous', submissionId)
}

function scrollToItem(itemIndex?: number) {
  if (itemIndex !== undefined && itemIndex >= 0) {
    dynamicScroller.value?.scrollToItem(itemIndex)
  }
}
defineExpose({
  scrollToItem
})

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
@reference "../../style/style.css";

.tableCell {
  @apply mx-3 flex flex-row items-center justify-center text-center;
}

.tableRow {
  @apply flex flex-row text-center;
}

.tableCellNumber {
  @apply w-12 min-w-12 shrink-0;
}

.tableCellSimilarity {
  @apply w-48 min-w-48 shrink-0;
}

.tableCellCluster {
  @apply w-32 min-w-32 shrink-0;
}

.tableCellName {
  @apply min-w-36 grow;
}
</style>
