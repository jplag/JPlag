<!--
  Radar chart which displays a cluster member with their similarity to other
  participants in the cluster.
-->
<template>
  <div class="wrapper">
    <div v-if="!hasNoMember" class="wrapper">
      <select v-model="selectedMember">
        <option v-for="(member, index) in cluster.members.keys()" :key="index">
          {{ member }}
        </option>
      </select>
      <RadarChart :chartData="chartData" :options="options" class="chart"></RadarChart>
    </div>
    <div v-if="hasNoMember" class="no-member">
      <span>This cluster has no members.</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PropType, Ref } from 'vue'
import type { ChartData } from 'chart.js'
import type { ClusterListElement } from '@/model/ClusterListElement'

import { ref, watch } from 'vue'
import { RadarChart } from 'vue-chart-3'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { radarChartStyle, radarChartOptions } from '@/assets/radar-chart-configuration'

Chart.register(...registerables)
Chart.register(ChartDataLabels)

const props = defineProps({
  cluster: {
    type: Object as PropType<ClusterListElement>,
    required: true
  }
})

let hasNoMember = false

function getIdOfFirstSubmission() {
  const firstMember = props.cluster.members.keys().next().value
  hasNoMember = !firstMember
  return firstMember
}

function createLabelsFor(member: string): Array<string> {
  let matchedWith = new Array<string>()
  props.cluster.members.get(member)?.forEach((m) => matchedWith.push(m.matchedWith))
  return matchedWith
}

function createDataSetFor(member: string) {
  let data = new Array<number>()
  props.cluster.members
    .get(member)
    ?.forEach((m) => data.push(roundToTwoDecimals(m.percentage * 100)))
  return data
}

function roundToTwoDecimals(num: number): number {
  return Math.round((num + Number.EPSILON) * 100) / 100
}

const selectedMember = ref(getIdOfFirstSubmission())

const chartData: Ref<ChartData<'radar', (number | null)[], unknown>> = ref({
  labels: createLabelsFor(getIdOfFirstSubmission()),
  datasets: [
    {
      ...radarChartStyle,
      label: getIdOfFirstSubmission(),
      data: createDataSetFor(getIdOfFirstSubmission())
    }
  ]
})

const options = ref(radarChartOptions)

watch(
  () => selectedMember.value,
  (val) => {
    chartData.value = {
      labels: createLabelsFor(val),
      datasets: [
        {
          ...radarChartStyle,
          label: val,
          data: createDataSetFor(val)
        }
      ]
    }
  }
)
</script>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
}
.no-member {
  display: flex;
  flex-direction: row;
  justify-content: center;
}
.chart {
  height: 50vw;
}
</style>
