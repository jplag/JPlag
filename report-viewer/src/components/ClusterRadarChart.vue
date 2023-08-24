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
import { graphColors } from '@/utils/ColorUtils'

Chart.register(...registerables)
Chart.register(ChartDataLabels)

const props = defineProps({
  cluster: {
    type: Object as PropType<ClusterListElement>,
    required: true
  }
})

let hasNoMember = props.cluster.members.size == 0

const selectedOptions = computed(() => Array.from(props.cluster.members.keys()))

const idOfFirstSubmission = selectedOptions.value.length > 0 ? selectedOptions.value[0] : ''

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
  props.cluster.members.get(member)?.forEach((m) => data.push(+(m.similarity * 100).toFixed(2)))
  return data
}

const radarChartStyle = {
  fill: true,
  backgroundColor: graphColors.contentFill,
  borderColor: graphColors.contentBorder,
  pointBackgroundColor: graphColors.pointFill,
  pointBorderColor: graphColors.contentBorder,
  borderWidth: 1
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
          color: graphColors.ticksAndFont.value,
          backdropColor: 'rgba(0,0,0,0)'
        },
        grid: {
          color: graphColors.gridLines.value
        },
        angleLines: {
          color: graphColors.gridLines.value
        }
      }
    },
    plugins: {
      datalabels: {
        color: graphColors.ticksAndFont.value
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
