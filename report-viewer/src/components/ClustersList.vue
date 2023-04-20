<!--
  List containing all the clusters in which both comparison submissions participate.
-->
<template>
  <div class="wrapper">
    <h1>
      Clusters for comparison {{ comparison.firstSubmissionId }} >
      {{ comparison.secondSubmissionId }}
    </h1>
    <p v-for="(cluster, index) in clusters" :key="index" @click="toggleDialog">
      {{ index + 1 }}. Members: <span id="members">{{ getMemberNames(cluster) }}</span> - Average
      similarity: {{ cluster.averageSimilarity * 100 }}%
      <GDialog v-model="dialog" fullscreen>
        <div id="dialog-header">
          <button @click="toggleDialog">Close</button>
        </div>
        <ClusterRadarChart :cluster="cluster"></ClusterRadarChart>
      </GDialog>
    </p>
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import type { ClusterListElement } from '@/model/ClusterListElement'
import type { ComparisonListElement } from '@/model/ComparisonListElement'

import { ref } from 'vue'
import { GDialog } from 'gitart-vue-dialog'
import ClusterRadarChart from '@/components/ClusterRadarChart.vue'

defineProps({
  comparison: {
    type: Object as PropType<ComparisonListElement>,
    required: true
  },
  clusters: {
    type: Array<ClusterListElement>,
    required: true
  }
})

const dialog = ref(false)

function toggleDialog() {
  dialog.value = !dialog.value
}

function getMemberNames(cluster: ClusterListElement) {
  const membersIterator = cluster.members.keys()
  const members = Array.from(membersIterator)
  let concatenatedMembers = ''
  const maxMembersToShow = 5
  concatenatedMembers = members.slice(0, maxMembersToShow).join(', ')
  if (members.length > maxMembersToShow) {
    concatenatedMembers += ',...'
  }
  return concatenatedMembers
}
</script>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  padding: 1%;
  background: var(--background-color);
  font-family: Avenir, Helvetica, Arial, sans-serif;
  color: var(--on-primary-color);
}

p {
  font-family: inherit;
  font-weight: bold;
  border-radius: 10px;
  background: var(--primary-color-light);
  box-shadow: var(--shadow-color) 3px 3px 2px;
  padding: 1%;
}

p:hover {
  background: var(--primary-color-dark);
  cursor: pointer;
}
#dialog-header {
  display: flex;
  flex-direction: row-reverse;
  margin-right: 1%;
  margin-top: 0.5%;
  box-shadow: 5px 10x #888888;
}
</style>
