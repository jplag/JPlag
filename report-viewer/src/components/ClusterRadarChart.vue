<!--
  Radar chart which displays a cluster member with their similarity to other
  participants in the cluster.
-->
<template>
  <div class="flex max-h-full flex-col overflow-hidden">
    <div
      v-if="selectedOptions.length > 0"
      class="flex max-h-full flex-grow flex-col overflow-hidden"
    >
      <DropDownSelector
        :options="selectedOptions"
        @selectionChanged="(value) => (idOfShownSubmission = value)"
      />
      <div class="flex min-h-0 flex-grow justify-center">
        <Radar :data="chartData" :options="radarChartOptions" />
      </div>
      <div class="text-xs font-bold text-gray-500 dark:text-gray-400 print:hidden">
        <p>
          This Chart shows the average similarity of the selected submission to the other
          submissions in the cluster. <br />
          The submission is selectable in the dropdown above.
        </p>
        <p
          v-if="
            selectedOptions.length < memberCount ||
            (cluster.members.get(idOfShownSubmission)?.length ?? 0) < memberCount - 1
          "
          class="mt-2"
        >
          Not all members may be selectable in the dropdown above. <br />
          There may not be a data point for every submission in the cluster for the selected one.
          <br />
          This is because not all comparisons are included in the report. To include more
          comparisons modify the number of shown comparisons in the CLI.
        </p>
      </div>
    </div>
    <div v-else class="text-xs font-bold text-gray-500 dark:text-gray-400">
      <p>
        This cluster does not have enough members with enough comparisons to provide a
        visualization. <br />
        To include more comparisons modify the number of shown comparisons in the CLI.
      </p>
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

const selectedOptions = computed(() => {
  let options: string[] = []
  props.cluster.members.forEach((matchedWith, key) => {
    if (matchedWith.length >= 3) {
      options.push(key)
    }
  })
  return options
})

const idOfShownSubmission = ref(selectedOptions.value.length > 0 ? selectedOptions.value[0] : '')

const memberCount = computed(() => props.cluster.members.size)

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
