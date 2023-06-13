<!--
  Table which display all of the comparisons with their participating ids and similarity percentage for the selected metric.
-->
<template>
  <div class="flex flex-col">
    <div class="font-bold">
      <!-- Header -->
      <div class="tableRow">
        <div class="tableCellName items-center">Submissions in Comparison</div>
        <div class="tableCellSimilarity !flex-col">
          <div>Similarity</div>
          <div class="flex flex-row">
            <div class="flex-auto">Average</div>
            <div class="flex-auto">Maximum</div>
          </div>
        </div>
        <div class="tableCellCluster items-center" v-if="displayClusters">Cluster</div>
      </div>
    </div>

    <!-- Body -->
    <div class="overflow-hidden flex flex-col flex-grow">
      <DynamicScroller v-if="topComparisons.length > 0" :items="topComparisons" :min-item-size="48">
        <template v-slot="{ item, index, active }">
          <DynamicScrollerItem
            :item="item"
            :active="active"
            :size-dependencies="[item.firstSubmissionId, item.secondSubmissionId]"
          >
            <!-- Row -->
            <div class="tableRow" :class="{ 'bg-accent bg-opacity-25': index % 2 == 0 }">
              <RouterLink
                :to="{
                  name: 'ComparisonView',
                  params: { firstId: item.firstSubmissionId, secondId: item.secondSubmissionId }
                }"
                class="flex flex-row flex-grow"
              >
                <!-- Names -->
                <div class="tableCellName">
                  <div class="flex-auto">
                    {{
                      isAnonymous(item.firstSubmissionId)
                        ? 'Hidden'
                        : displayName(item.firstSubmissionId)
                    }}
                  </div>
                  <div class="flex-auto">
                    {{
                      isAnonymous(item.secondSubmissionId)
                        ? 'Hidden'
                        : displayName(item.secondSubmissionId)
                    }}
                  </div>
                </div>

                <!-- Similarities -->
                <div class="tableCellSimilarity">
                  <div class="flex-auto">
                    {{ formattedMatchPercentage(item.averageSimilarity) }}%
                  </div>
                  <div class="flex-auto">
                    {{ formattedMatchPercentage(item.maximumSimilarity) }}%
                  </div>
                </div>
              </RouterLink>

              <!-- Clusters -->
              <div class="tableCellCluster flex !flex-col" v-if="displayClusters">
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
                >
                  <div>
                    {{ clusters?.[index].members?.length }}
                    <FontAwesomeIcon
                      :icon="['fas', 'user-group']"
                      :style="{ color: clusterIconColors[index] }"
                    />
                    {{
                      formattedMatchPercentage(
                        (clusters?.[index].averageSimilarity as number) / 100
                      )
                    }}%
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
import type { Ref } from 'vue'
import { ref } from 'vue'
import store from '@/stores/store'
import { DynamicScroller, DynamicScrollerItem } from 'vue-virtual-scroller'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faUserGroup } from '@fortawesome/free-solid-svg-icons'
import { generateHuesForInterval, toHSLAArray } from '@/utils/ColorUtils'

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

const displayClusters = props.clusters != undefined

/**
 * Formats the match percentage to a string with 2 decimal places.
 * @param num The number to format.
 * @returns The formatted number.
 */
function formattedMatchPercentage(num: number) {
  return (num * 100).toFixed(2)
}

const dialog: Ref<Array<boolean>> = ref([])
props.topComparisons.forEach(() => dialog.value.push(false))

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
  const hues = generateHuesForInterval(20, 80, Math.floor(props.clusters.length))
  hues.push(...generateHuesForInterval(160, 340, Math.ceil(props.clusters.length)))
  clusterIconColors = toHSLAArray(
    generateHuesForInterval(20, 340, props.clusters.length),
    0.5,
    0.5,
    1
  )
}

/**
 * @param id1 First Id to check
 * @param id2 Second Id to check
 * @returns All clusters that contain both ids.
 */
function getClusterIndexesFor(id1: string, id2: string): Array<number> {
  const indexes = [] as Array<number>
  props.clusters?.forEach((c: Cluster, index: number) => {
    if (c.members.includes(id1) && c.members.includes(id2)) {
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

.tableCellSimilarity {
  @apply w-40 tableCell;
}

.tableCellCluster {
  @apply w-32 tableCell;
}

.tableCellName {
  @apply flex-grow tableCell;
}

.tableCell {
  @apply text-center mx-3 flex flex-row justify-center;
}
</style>
