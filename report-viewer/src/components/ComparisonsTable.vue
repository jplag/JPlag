<!--
  Table which display all of the comparisons with their participating ids and similarity percentage for the selected metric.
-->
<template>
  <div class="flex flex-col">
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
                <p class="w-full text-center">{{ metricToolTips[MetricType.AVERAGE].shortName }}</p>
              </template>
              <template #tooltip>
                <p class="whitespace-pre text-sm">
                  {{ metricToolTips[MetricType.AVERAGE].tooltip }}
                </p>
              </template>
            </ToolTipComponent>

            <ToolTipComponent class="flex-1" :direction="displayClusters ? 'top' : 'left'">
              <template #default>
                <p class="w-full text-center">{{ metricToolTips[MetricType.MAXIMUM].shortName }}</p>
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
      <DynamicScroller v-if="topComparisons.length > 0" :items="comparisonList" :min-item-size="48">
        <template #default="{ item, index, active }">
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
                'bg-container-secondary-light dark:bg-container-secondary-dark': item.id % 2 == 1
              }"
            >
              <div
                @click="
                  router.push({
                    name: 'ComparisonView',
                    params: { firstId: item.firstSubmissionId, secondId: item.secondSubmissionId }
                  })
                "
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
              </div>

              <!-- Clusters -->
              <div class="tableCellCluster flex !flex-col items-center" v-if="displayClusters">
                <RouterLink
                  v-for="index of getClusterIndexesFor(
                    item.firstSubmissionId,
                    item.secondSubmissionId
                  )"
                  v-bind:key="index"
                  :to="{
                    name: 'ClusterView',
                    params: { clusterIndex: index }
                  }"
                  class="tect-center flex w-full justify-center"
                >
                  <ToolTipComponent class="w-fit" direction="left">
                    <template #default>
                      {{ clusters?.[index].members?.length }}
                      <FontAwesomeIcon
                        :icon="['fas', 'user-group']"
                        :style="{ color: clusterIconColors[index] }"
                      />
                      {{ ((clusters?.[index].averageSimilarity as number) * 100).toFixed(2) }}%
                    </template>
                    <template #tooltip>
                      <p class="whitespace-nowrap text-sm">
                        {{ clusters?.[index].members?.length }} submissions in cluster with average
                        similarity of
                        {{ ((clusters?.[index].averageSimilarity as number) * 100).toFixed(2) }}%
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
</template>

<script setup lang="ts">
import type { Cluster } from '@/model/Cluster'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import { toRef } from 'vue'
import { store } from '@/stores/store'
import { DynamicScroller, DynamicScrollerItem } from 'vue-virtual-scroller'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faUserGroup } from '@fortawesome/free-solid-svg-icons'
import { generateColors } from '@/utils/ColorUtils'
import ToolTipComponent from './ToolTipComponent.vue'
import { MetricType, metricToolTips } from '@/model/MetricType'
import NameElement from './NameElement.vue'
import { router } from '@/router'

library.add(faUserGroup)

const props = defineProps({
  topComparisons: {
    type: Array<ComparisonListElement>,
    required: true
  },
  clusters: {
    type: Array<Cluster>,
    required: false
  }
})

const comparisonList = toRef(props, 'topComparisons')

const displayClusters = props.clusters != undefined

let clusterIconColors = [] as Array<string>
if (props.clusters != undefined) {
  clusterIconColors = generateColors(props.clusters.length, 0.8, 0.5, 1)
}

/**
 * @param id1 First Id to check
 * @param id2 Second Id to check
 * @returns All clusters that contain both ids.
 */
function getClusterIndexesFor(id1: string, id2: string): Array<number> {
  const indexes = [] as Array<number>
  props.clusters?.forEach((c: Cluster, index: number) => {
    if (c.members.includes(id1) && c.members.includes(id2) && c.members.length > 2) {
      indexes.push(index)
    }
  })
  return indexes
}
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
