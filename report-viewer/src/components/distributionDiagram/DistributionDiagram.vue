<!--
  Bar diagram, displaying the distribution for the selected metric.
-->
<template>
  <div class="flex flex-col">
    <div class="h-3/4 w-full print:h-fit print:w-fit">
      <Bar :data="chartData" :options="options" />
    </div>

    <DistributionDiagramOptions class="flex-grow print:grow-0" />
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'
import { Bar } from 'vue-chartjs'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { graphColors } from '@/utils/ColorUtils'
import type { Distribution } from '@/model/Distribution'
import { MetricType } from '@/model/MetricType'
import { store } from '@/stores/store'
import DistributionDiagramOptions from './DistributionDiagramOptions.vue'

Chart.register(...registerables)
Chart.register(ChartDataLabels)

console.log('AAA')

const props = defineProps({
  distributions: {
    type: Object as PropType<Record<MetricType, Distribution>>,
    required: true
  },
  xScale: {
    type: String as PropType<'linear' | 'logarithmic'>,
    required: false,
    default: 'linear'
  }
})

const graphOptions = computed(() => store().uiState.distributionChartConfig)
const distribution = computed(() => props.distributions[graphOptions.value.metric])

const maxVal = computed(() => Math.max(...distribution.value.splitIntoTenBuckets()))
const labels = [
  '91-100%',
  '81-90%',
  '71-80%',
  '61-70%',
  '51-60%',
  '41-50%',
  '31-40%',
  '21-30%',
  '11-20%',
  '0-10%'
]
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
    labels: labels,
    datasets: [
      {
        ...dataSetStyle.value,
        data: distribution.value.splitIntoTenBuckets()
      }
    ]
  }
})

const options = computed(() => {
  return {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y' as 'y',
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
          // ensures that in log mode tick labels are not overlappein
          minRotation: graphOptions.value.xScale === 'logarithmic' ? 30 : 0,
          autoSkipPadding: 10,
          color: graphColors.ticksAndFont.value,
          // ensures that in log mode ticks are placed evenly appart
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
          color: graphColors.ticksAndFont.value
        },
        grid: {
          color: graphColors.gridLines.value
        }
      }
    },
    plugins: {
      datalabels: {
        color: graphColors.ticksAndFont.value,
        font: {
          weight: 'bold' as 'bold'
        },
        anchor: 'end' as 'end',
        align: 'end' as 'end',
        clamp: true
      },
      legend: {
        display: true,
        position: 'bottom',
        align: 'end',
        onClick: () => {}
      }
    }
  }
})
</script>
