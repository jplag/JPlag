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
          <div class="flex flex-row w-full">
            <div class="flex-1">Average</div>
            <div class="flex-1">Maximum</div>
          </div>
        </div>
        <div class="tableCellCluster items-center" v-if="displayClusters">Cluster</div>
      </div>
    </div>

    <!-- Body -->
    <div class="overflow-hidden flex flex-col flex-grow">
      <DynamicScroller v-if="topComparisons.length > 0" :items="comparisonList" :min-item-size="48">
        <template v-slot="{ item, index, active }">
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
          >
            <!-- Row -->
            <div
              class="tableRow"
              :class="{
                'bg-container-secondary-light dark:bg-container-secondary-dark': item.index % 2 == 1
              }"
            >
              <RouterLink
                :to="{
                  name: 'ComparisonView',
                  params: { firstId: item.firstSubmissionId, secondId: item.secondSubmissionId }
                }"
                class="flex flex-row flex-grow"
              >
                <!-- Index in sorted list -->
                <div class="tableCellNumber">
                  <div class="w-full text-center">{{ item.sortingPlace + 1 }}</div>
                </div>

                <!-- Names -->
                <div class="tableCellName">
                  <div
                    class="w-1/2 px-2 break-anywhere"
                    :class="{ 'blur-[1px]': isAnonymous(item.firstSubmissionId) }"
                  >
                    {{
                      isAnonymous(item.firstSubmissionId)
                        ? 'Hidden'
                        : displayName(item.firstSubmissionId)
                    }}
                  </div>
                  <div
                    class="w-1/2 px-2 break-anywhere"
                    :class="{ 'blur-[1px]': isAnonymous(item.secondSubmissionId) }"
                  >
                    {{
                      isAnonymous(item.secondSubmissionId)
                        ? 'Hidden'
                        : displayName(item.secondSubmissionId)
                    }}
                  </div>
                </div>

                <!-- Similarities -->
                <div class="tableCellSimilarity">
                  <div class="w-1/2">{{ (item.averageSimilarity * 100).toFixed(2) }}%</div>
                  <div class="w-1/2">{{ (item.maximumSimilarity * 100).toFixed(2) }}%</div>
                </div>
              </RouterLink>

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
                  class="w-full tect-center flex justify-center"
                >
                  <div class="group relative w-fit">
                    {{ clusters?.[index].members?.length }}
                    <FontAwesomeIcon
                      :icon="['fas', 'user-group']"
                      :style="{ color: clusterIconColors[index] }"
                    />
                    {{ ((clusters?.[index].averageSimilarity as number) * 100).toFixed(2) }}%
                    <div
                      class="hidden group-hover:flex absolute z-50 top-0 left-[-400px] text-sm h-full items-center text-white bg-gray-950 bg-opacity-90 px-2 rounded-sm tooltipArrow"
                    >
                      {{ clusters?.[index].members?.length }} submissions in cluster with average
                      similarity of
                      {{ ((clusters?.[index].averageSimilarity as number) * 100).toFixed(2) }}%
                    </div>
                  </div>
                </RouterLink>
              </div>
            </div>
          </DynamicScrollerItem>
        </template>
      </DynamicScroller>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Cluster } from '@/model/Cluster'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import { toRef } from 'vue'
import store from '@/stores/store'
import { DynamicScroller, DynamicScrollerItem } from 'vue-virtual-scroller'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faUserGroup } from '@fortawesome/free-solid-svg-icons'
import { generateColors } from '@/utils/ColorUtils'

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

/**
 * @param submissionId Id to get name for
 * @returns The display name of the submission with the given id.
 */
function displayName(submissionId: string) {
  return store().submissionDisplayName(submissionId)
}

/**
 * @param id SubmissionId to check
 * @returns Whether the name should be hidden.
 */
function isAnonymous(id: string) {
  return store().state.anonymous.has(id)
}

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
  @apply table-cell w-12 flex-shrink-0;
}

.tableCellSimilarity {
  @apply w-40 tableCell flex-shrink-0;
}

.tableCellCluster {
  @apply w-32 tableCell flex-shrink-0;
}

.tableCellName {
  @apply flex-grow tableCell;
}

.tableCell {
  @apply text-center mx-3 flex flex-row justify-center items-center;
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
