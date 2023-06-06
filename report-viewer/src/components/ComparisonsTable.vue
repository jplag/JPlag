<!--
  Table which display all of the comparisons with their participating ids and similarity percentage for the selected metric.
-->
<template>
  <div class="flex flex-col">
    <div class="font-bold">
      <div class="tableRow">
        <div class="tableCellName items-center">Submissions in Comparison</div>
        <div class="tableCellSimilarity !flex-col">
          <div>Similarity</div>
          <div class="flex flex-row">
            <div class="flex-auto">Average</div>
            <div class="flex-auto">Maximum</div>
          </div>
        </div>
        <div class="tableCellCluster items-center">Cluster</div>
      </div>
    </div>
    <div class="overflow-hidden flex flex-col flex-grow">
      <DynamicScroller v-if="topComparisons.length > 0" :items="topComparisons" :min-item-size="48">
        <template v-slot="{ item, index, active }">
          <DynamicScrollerItem
            :item="item"
            :active="active"
            :size-dependencies="[item.firstSubmissionId, item.secondSubmissionId]"
          >
            <div class="tableRow" :class="{ 'bg-accent bg-opacity-25': index % 2 == 0 }">
              <RouterLink
                :to="{
                  name: 'ComparisonView',
                  params: { firstId: item.firstSubmissionId, secondId: item.secondSubmissionId }
                }"
                class="flex flex-row flex-grow"
              >
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
                <div class="tableCellSimilarity">
                  <div class="flex-auto">{{ formattedMatchPercentage(item.similarity) }}%</div>
                  <div class="flex-auto">{{ formattedMatchPercentage(item.similarity) }}%</div>
                </div>
              </RouterLink>
              <div class="tableCellCluster">
                <RouterLink :to="'test'">
                  <div v-if="isInCluster(item.firstSubmissionId, item.secondSubmissionId)">
                    {{
                      getClustersFor(item.firstSubmissionId, item.secondSubmissionId)[0].members
                        .size
                    }}
                    <FontAwesomeIcon :icon="['fas', 'user-group']" />
                    {{
                      formattedMatchPercentage(
                        getClustersFor(item.firstSubmissionId, item.secondSubmissionId)[0]
                          .averageSimilarity / 100
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
import type { ClusterListElement } from '@/model/ClusterListElement'
import type { Ref } from 'vue'
import { ref } from 'vue'
import store from '@/stores/store'
import { DynamicScroller, DynamicScrollerItem } from 'vue-virtual-scroller'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faUserGroup } from '@fortawesome/free-solid-svg-icons'

library.add(faUserGroup)

const props = defineProps({
  topComparisons: {
    type: Array<ComparisonListElement>,
    required: true
  },
  clusters: {
    type: Array<Cluster>,
    required: true
  }
})

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
 * @param id1 First Id
 * @param id2 Second Id
 * @returns Whether the two ids are in a cluster together.
 */
function isInCluster(id1: string, id2: string) {
  return props.clusters.some((c: Cluster) => c.members.includes(id1) && c.members.includes(id2))
}

/**
 * @param id SubmissionId to check
 * @returns Whether the name should be hidden.
 */
function isAnonymous(id: string) {
  return store().anonymous.has(id)
}

/**
 * @param id First Id
 * @param others Other Ids that need to be in the cluster
 * @returns The clusters that the two ids are in together.
 */
function getParticipatingMatchesForId(id: string, others: Array<string>) {
  let matches: Array<{ matchedWith: string; percentage: number }> = []
  props.topComparisons.forEach((comparison) => {
    if (
      comparison.firstSubmissionId.includes(id) &&
      others.includes(comparison.secondSubmissionId)
    ) {
      matches.push({
        matchedWith: comparison.secondSubmissionId,
        percentage: comparison.similarity
      })
    } else if (
      comparison.secondSubmissionId.includes(id) &&
      others.includes(comparison.firstSubmissionId)
    ) {
      matches.push({
        matchedWith: comparison.firstSubmissionId,
        percentage: comparison.similarity
      })
    }
  })
  return matches
}

const clustersWithParticipatingMatches: Array<ClusterListElement> = props.clusters.map(
  (cluster) => {
    let membersArray = new Map<string, Array<{ matchedWith: string; percentage: number }>>()
    cluster.members.forEach((member: string) => {
      let others = cluster.members.filter((m) => !m.includes(member))
      membersArray.set(member, getParticipatingMatchesForId(member, others))
    })

    return {
      averageSimilarity: cluster.averageSimilarity,
      strength: cluster.strength,
      members: membersArray
    }
  }
)

/**
 * @param id1 First Id to check
 * @param id2 Second Id to check
 * @returns All clusters that contain both ids.
 */
function getClustersFor(id1: string, id2: string): Array<ClusterListElement> {
  return clustersWithParticipatingMatches.filter((c) => c.members.has(id1) && c.members.has(id2))
}
</script>

<style scoped lang="postcss">
.tableRow {
  @apply flex flex-row text-center;
}

.tableCellSimilarity {
  @apply w-64 tableCell;
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
