<!--
  Radar chart which displays a cluster member with their similarity to other
  participants in the cluster.
-->
<template>
  <div class="flex flex-col">
    <div v-if="!hasNoMember" class="flex-grow flex flex-col">
      <DropDownSelector
        :options="selectedOptions"
        @selectionChanged="(value) => updateChartData(value)"
      />
      <RadarChart :chartData="chartData" :options="options" class="flex-grow"></RadarChart>
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

import { computed, ref } from 'vue'
import { RadarChart } from 'vue-chart-3'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import DropDownSelector from './DropDownSelector.vue'
import store from '@/stores/store'

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

const tickColor = computed(() => {
  return store().uiState.useDarkMode ? '#ffffff' : '#000000'
})

const gridColor = computed(() => {
  return store().uiState.useDarkMode ? 'rgba(256, 256, 256, 0.2)' : 'rgba(0, 0, 0, 0.2)'
})

const radarChartStyle = {
  fill: true,
  backgroundColor: 'rgb(190, 22, 34, 0.5)',
  borderColor: 'rgb(127, 15, 24)',
  pointBackgroundColor: 'rgb(190, 22, 34, 1)',
  pointBorderColor: 'rgb(127, 15, 24)',
  borderWidth: 2
}
const radarChartOptions = computed(() => {
  return {
    legend: {
      display: false
    },
    scales: {
      r: {
        suggestedMin: 50,
        suggestedMax: 100,
        ticks: {
          color: tickColor.value,
          backdropColor: 'rgba(0,0,0,0)'
        },
        grid: {
          color: gridColor.value
        },
        angleLines: {
          color: gridColor.value
        }
      }
    },
    plugins: {
      datalabels: {
        color: tickColor.value
      }
    }
  }
})

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
