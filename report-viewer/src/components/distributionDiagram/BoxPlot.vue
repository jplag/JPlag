<template>
  <div class="flex flex-col">
    <div class="h-3/4 w-full print:h-fit print:w-fit">
      <canvas ref="graphCanvas"></canvas>
      <div v-if="!loaded">Could not display boxplot</div>
    </div>

    <BoxPlotDiagramOptions class="grow print:grow-0" />
  </div>
</template>

<script setup lang="ts">
import { Chart, registerables } from 'chart.js'
import { computed, onMounted, ref, watch, type PropType, type Ref } from 'vue'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { BoxAndWiskers, BoxPlotChart, BoxPlotController } from '@sgratzl/chartjs-chart-boxplot'
import { store } from '@/stores/store'
import { graphColors } from '@/utils/ColorUtils'
import type { DistributionMap } from '@/model/Distribution'
import BoxPlotDiagramOptions from './BoxPlotDiagramOptions.vue'

Chart.register(...registerables)
Chart.register(ChartDataLabels)
Chart.register(BoxAndWiskers)
Chart.register(BoxPlotController)
Chart.register(BoxPlotChart)

const props = defineProps({
  distributions: {
    type: Object as PropType<DistributionMap>,
    required: true
  }
})

const distribution = computed(() => {
  const metric = store().uiState.distributionChartConfig.metric
  return Array.from(props.distributions[metric].splitIntoBuckets(100)).reverse()
})

const totalValues = computed(() => distribution.value.reduce((acc, val) => acc + val, 0))
// https://www.data-to-viz.com/caveat/boxplot.html
const median = computed(() => {
  let i = 0
  for (let j = 0; j < distribution.value.length; j++) {
    if (i >= totalValues.value / 2) {
      return j
    }
    i += distribution.value[j]
  }
  return NaN
})
const q1 = computed(() => {
  let i = 0
  for (let j = 0; j < distribution.value.length; j++) {
    if (i >= totalValues.value / 4) {
      return j
    }
    i += distribution.value[j]
  }
  return NaN
})
const q3 = computed(() => {
  let i = 0
  for (let j = 0; j < distribution.value.length; j++) {
    if (i >= (3 * totalValues.value) / 4) {
      return j
    }
    i += distribution.value[j]
  }
  return NaN
})
const iqr = computed(() => q3.value - q1.value)
const minValueInData = computed(() => {
  for (let i = 0; i < distribution.value.length; i++) {
    if (distribution.value[i] > 0) {
      return i
    }
  }
  return NaN
})
const maxValueInData = computed(() => {
  for (let i = distribution.value.length - 1; i >= 0; i--) {
    if (distribution.value[i] > 0) {
      return i
    }
  }
  return NaN
})
const min = computed(() => Math.max(q1.value - 1.5 * iqr.value, minValueInData.value))
const max = computed(() => Math.min(q3.value + 1.5 * iqr.value, maxValueInData.value))
const avg = computed(() => {
  let sum = 0
  for (let i = 0; i < distribution.value.length; i++) {
    if (distribution.value[i] > 0) {
      sum += i * distribution.value[i]
    }
  }
  return totalValues.value > 0 ? sum / totalValues.value : NaN
})
const outliers = computed(() => {
  const outliers = []
  for (let i = 0; i < distribution.value.length; i++) {
    if (distribution.value[i] > 0 && (i < min.value || i > max.value)) {
      outliers.push(i)
    }
  }
  return outliers
})

const dataSetStyle = computed(() => ({
  backgroundColor: graphColors.contentFillAlpha(0.4),
  borderWidth: 1,
  borderColor: graphColors.ticksAndFont.value,
  meanBorderColor: graphColors.ticksAndFont.value,
  meanBackgroundColor: graphColors.contentFill,
  outlierBorderColor: graphColors.ticksAndFont.value
}))

const graphData = computed(() => ({
  labels: ['Submissions'],
  datasets: [
    {
      ...dataSetStyle.value,
      label: 'Submissions',
      data: [
        {
          min: min.value,
          q1: q1.value,
          median: median.value,
          q3: q3.value,
          max: max.value,
          mean: avg.value,
          outliers: outliers.value
        }
      ]
    }
  ]
}))
const graphOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      suggestedMin: 0,
      suggestedMax: 100,
      grid: {
        color: graphColors.gridLines.value
      },
      ticks: {
        color: graphColors.ticksAndFont.value
      }
    },
    x: {
      grid: {
        color: graphColors.gridLines.value
      }
    }
  },
  animation: false as const,
  plugins: {
    datalabels: {
      display: false
    },
    legend: {
      display: true,
      position: 'bottom' as const,
      align: 'end' as const,
      onClick: () => {}
    },
    tooltip: {
      enabled: true,
      displayColors: false,
      callbacks: {
        /* eslint-disable-next-line @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in the library
        label: (e: any) => {
          // This type is only a partial definition of e but everything needed here
          const hoverItem = e as unknown as { formattedValue: { hoveredOutlierIndex: number } }
          if (hoverItem.formattedValue.hoveredOutlierIndex < 0) {
            return `min: ${min.value}, Q1: ${q1.value}, median: ${median.value}, Q3: ${q3.value}, max: ${max.value}, mean: ${avg.value.toFixed(2)}, IQR: ${iqr.value}`
          }
          const outlier = outliers.value[hoverItem.formattedValue.hoveredOutlierIndex]
          return `${distribution.value[outlier]} submissions with value between ${outlier}% and ${outlier + 1}%`
        }
      }
    }
  }
}))

const chart: Ref<Chart | null> = ref(null)
const graphCanvas: Ref<HTMLCanvasElement | null> = ref(null)
const loaded = ref(false)

function drawGraph() {
  if (chart.value != null) {
    chart.value.destroy()
  }
  if (graphCanvas.value == null) {
    loaded.value = false
    return
  }
  const ctx = graphCanvas.value.getContext('2d')
  if (ctx == null) {
    loaded.value = false
    return
  }
  chart.value = new Chart(ctx, {
    type: 'boxplot',
    data: graphData.value,
    options: graphOptions.value
  })
  loaded.value = true
}

onMounted(() => {
  drawGraph()
})

watch(
  computed(() => {
    return {
      d: graphData.value,
      o: graphOptions.value
    }
  }),
  () => {
    drawGraph()
  }
)
</script>
