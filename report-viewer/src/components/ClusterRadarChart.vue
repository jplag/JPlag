<!--
  Radar chart which displays a cluster member with their similarity to other
  participants in the cluster.
-->
<template>
  <div class="flex max-h-full flex-col">
    <div v-if="!hasNoMember" class="flex max-h-full flex-grow flex-col overflow-hidden">
      <DropDownSelector
        :options="selectedOptions"
        @selectionChanged="(value) => (idOfShownSubmission = value)"
      />
      <div class="flex min-h-0 flex-grow justify-center">
        <Radar :data="chartData" :options="radarChartOptions" />
      </div>
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
import { Radar } from 'vue-chartjs'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import DropDownSelector from './DropDownSelector.vue'
import { graphColors } from '@/utils/ColorUtils'
import { store } from '@/stores/store'

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

const idOfShownSubmission = ref(selectedOptions.value.length > 0 ? selectedOptions.value[0] : '')

const labels = computed(() => {
  let matchedWith = new Array<string>()
  props.cluster.members
    .get(idOfShownSubmission.value)
    ?.forEach((m) => matchedWith.push(m.matchedWith))
  return matchedWith.map((m) => store().getDisplayName(m))
})

const dataSet = computed(() => {
  let data = new Array<number>()
  props.cluster.members
    .get(idOfShownSubmission.value)
    ?.forEach((m) => data.push(+(m.similarity * 100).toFixed(2)))
  return data
})

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

const chartData: Ref<ChartData<'radar', (number | null)[], unknown>> = computed(() => {
  return {
    labels: labels.value,
    datasets: [
      {
        ...radarChartStyle,
        label: store().getDisplayName(idOfShownSubmission.value),
        data: dataSet.value
      }
    ]
  }
})
</script>
