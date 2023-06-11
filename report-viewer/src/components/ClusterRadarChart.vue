<!--
  Radar chart which displays a cluster member with their similarity to other
  participants in the cluster.
-->
<template>
  <div>
    <div v-if="!hasNoMember">
      <DropDownSelector
        :options="selectedOptions"
        @selectionChanged="(value) => updateChartData(value)"
      />
      <RadarChart :chartData="chartData" :options="options" class="chart"></RadarChart>
    </div>
    <div v-else>
      <span>This cluster has no members.</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PropType, Ref } from 'vue'
import type { ChartData } from 'chart.js'
import type { ClusterListElement } from '@/model/ClusterListElement'

import { ref } from 'vue'
import { RadarChart } from 'vue-chart-3'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { radarChartStyle, radarChartOptions } from '@/assets/radar-chart-configuration'
import DropDownSelector from './DropDownSelector.vue'

Chart.register(...registerables)
Chart.register(ChartDataLabels)

const props = defineProps({
  cluster: {
    type: Object as PropType<ClusterListElement>,
    required: true
  }
})

let hasNoMember = props.cluster.members.size == 0

const selectedOptions = Array.from(props.cluster.members.keys())

const idOfFirstSubmission = selectedOptions.length > 0 ? selectedOptions[0] : ''

/**
 * @param member The member to create the labels for.
 * @returns The labels for the member.
 */
function createLabelsFor(member: string) {
  let matchedWith = new Array<string>()
  props.cluster.members.get(member)?.forEach((m) => matchedWith.push(m.matchedWith))
  return matchedWith
}

/**
 * @param member The member to create the data set for.
 * @returns The data set for the member.
 */
function createDataSetFor(member: string) {
  let data = new Array<number>()
  props.cluster.members
    .get(member)
    ?.forEach((m) => data.push(roundToTwoDecimals(m.similarity * 100)))
  return data
}

/**
 * @param num The number to round.
 * @returns The rounded number.
 */
function roundToTwoDecimals(num: number): number {
  return Math.round((num + Number.EPSILON) * 100) / 100
}

const chartData: Ref<ChartData<'radar', (number | null)[], unknown>> = ref({
  labels: createLabelsFor(idOfFirstSubmission),
  datasets: [
    {
      ...radarChartStyle,
      label: idOfFirstSubmission,
      data: createDataSetFor(idOfFirstSubmission)
    }
  ]
})

const options = ref(radarChartOptions)

function updateChartData(value: string) {
  chartData.value.datasets[0].data = createDataSetFor(value)
  chartData.value.datasets[0].label = value
  chartData.value.labels = createLabelsFor(value)
}
</script>
