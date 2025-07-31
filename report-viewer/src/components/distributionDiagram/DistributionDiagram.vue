<!--
  Bar diagram, displaying the distribution for the selected metric.
-->
<template>
  <div class="flex flex-col">
    <div class="h-3/4 w-full print:h-fit print:w-fit">
      <canvas ref="graphCanvas"></canvas>
    </div>

    <DistributionDiagramOptions class="grow print:grow-0" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch, type PropType, type Ref } from 'vue'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { graphColors } from '@/utils/ColorUtils'
import type { DistributionMap } from '@/model/Distribution'
import { store } from '@/stores/store'
import DistributionDiagramOptions from './DistributionDiagramOptions.vue'

Chart.register(...registerables)
Chart.register(ChartDataLabels)

const props = defineProps({
  distributions: {
    type: Object as PropType<DistributionMap>,
    required: true
  },
  xScale: {
    type: String as PropType<'linear' | 'logarithmic'>,
    required: false,
    default: 'linear'
  }
})

const emit = defineEmits<{
  (e: 'click:upperPercentile', UpperPercentile: number): void
}>()

const graphOptions = computed(() => store().uiState.distributionChartConfig)
const distribution = computed(() => props.distributions[graphOptions.value.metric])
const distributionData = computed(() =>
  distribution.value.splitIntoBuckets(graphOptions.value.bucketCount)
)

const maxVal = computed(() => Math.max(...distributionData.value))

const labels = computed(() => {
  let labels = []
  for (let i = 0; i < distributionData.value.length; i++) {
    labels.push(getDataPointLabel(i))
  }
  return labels.reverse()
})

const dataSetStyle = computed(() => {
  return {
    label: 'Comparisons in bucket',
    backgroundColor: graphColors.contentFill,
    borderWidth: 1,
    borderColor: graphColors.contentBorder,
    tickColor: graphColors.ticksAndFont.value
  }
})

const chartData = computed(() => {
  return {
    labels: labels.value,
    datasets: [
      {
        ...dataSetStyle.value,
        data: distributionData.value
      }
    ]
  }
})

const options = computed(() => {
  return {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y' as const,
    scales: {
      x: {
        //Highest count of submissions in a percentage range. We set the diagrams maximum shown value to maxVal + 5,
        //otherwise maximum is set to the highest count of submissions and is one bar always reaches the end.
        suggestedMax:
          graphOptions.value.xScale === 'linear'
            ? maxVal.value + 5
            : 10 ** Math.ceil(Math.log10(maxVal.value + 5)),
        type: graphOptions.value.xScale,
        ticks: {
          // ensures that in log mode tick labels are not overlapping
          minRotation: graphOptions.value.xScale === 'logarithmic' ? 30 : 0,
          autoSkipPadding: 10,
          color: graphColors.ticksAndFont.value,
          // ensures that in log mode ticks are placed evenly apart
          /* eslint-disable @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in chart.js
          callback: function (value: any) {
            if (graphOptions.value.xScale === 'logarithmic' && (value + '').match(/1(0)*[^1-9.]/)) {
              return value
            }
            if (graphOptions.value.xScale !== 'logarithmic') {
              return value
            }
          }
        },
        grid: {
          color: graphColors.gridLines.value
        }
      },
      y: {
        ticks: {
          color: graphColors.ticksAndFont.value,
          /* eslint-disable @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in chart.js
          callback: function (reversedValue: any) {
            const value = distributionData.value.length - reversedValue - 1
            if (graphOptions.value.bucketCount <= 10) {
              return getDataPointLabel(value)
            } else {
              let labelBreakPoint: number
              if (graphOptions.value.bucketCount <= 25) {
                labelBreakPoint = 5
              } else {
                labelBreakPoint = Math.floor(graphOptions.value.bucketCount / 10)
              }
              if (value == graphOptions.value.bucketCount - 1 || value % labelBreakPoint == 0) {
                return getDataPointLabel(value)
              }
            }
          }
        },
        grid: {
          color: graphColors.gridLines.value
        }
      }
    },
    animation: false as const,
    plugins: {
      datalabels: {
        color: graphColors.ticksAndFont.value,
        font: {
          weight: 'bold' as const,
          size: getDataLabelFontSize()
        },
        anchor: 'end' as const,
        align: 'end' as const,
        clamp: true,
        text: 'test'
      },
      legend: {
        display: true,
        position: 'bottom' as const,
        align: 'end' as const,
        onClick: () => {}
      }
    },
    // @ts-expect-error As there is not type to satisfy both the getElementsAtEventForMode function and Chart creations we leave the type out
    onClick: (e) => {
      if (!chart.value) {
        return
      }

      const points = chart.value.getElementsAtEventForMode(e, 'nearest', { intersect: true }, true)
      if (points.length <= 0 || points.length > 1) {
        return
      }
      const index = points[0].index
      const clickedBucket = distributionData.value.length - index
      const clickedUpperPercentile = clickedBucket * (100 / graphOptions.value.bucketCount)
      emit('click:upperPercentile', clickedUpperPercentile)
    }
  }
})

function getDataPointLabel(index: number) {
  let perBucket = 100 / graphOptions.value.bucketCount
  return index * perBucket + '-' + (index * perBucket + perBucket) + '%'
}

function getDataLabelFontSize() {
  if (graphOptions.value.bucketCount == 100) {
    return 7
  }
  if (graphOptions.value.bucketCount == 50) {
    return 10
  }
  return 12
}

const chart: Ref<Chart | null> = ref(null)
const loaded: Ref<boolean> = ref(false)
const graphCanvas: Ref<HTMLCanvasElement | null> = ref(null)

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
    type: 'bar',
    data: chartData.value,
    options: options.value
  })
  loaded.value = true
}

onMounted(() => {
  drawGraph()
})
watch(
  computed(() => {
    return {
      d: chartData.value,
      o: options.value
    }
  }),
  () => {
    drawGraph()
  }
)
</script>
