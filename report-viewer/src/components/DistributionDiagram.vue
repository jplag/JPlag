<template>
  <div class="wrapper">
    <BarChart class="chart" :chartData="chartData" :options="options"/>
  </div>
</template>

<script>
import {defineComponent, ref, watch} from "vue";
import { BarChart } from "vue-chart-3"
import { Chart, registerables } from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';
Chart.register(...registerables);
Chart.register(ChartDataLabels);

export default defineComponent({
  name: "DistributionDiagram",
  components: { BarChart },
  props: {
    distribution: {
      type: Array,
      required: true
    }
  },
  setup(props) {
    let maxVal = ref(Math.max(...props.distribution));
    let chartData = ref( {
        labels: ['0-10%', '11-20%', '21-30%', '31-40%', '41-50%', '51-60%', '61-70%', '71-80%', '81-90%', '91-100%'],
        datasets: [{
          label: 'Count',
          data: props.distribution,
          backgroundColor: 'rgba(149, 168, 241, 0.5)',
          borderWidth: 2,
          borderColor: 'rgba(149, 168, 241, 1)',
          tickColor: '#860000'
        }]
    })

    const options = ref({
      responsive: true,
      maintainAspectRatio: false,
      indexAxis: 'y',
      scales: {
        x: {
          suggestedMax: maxVal.value + 5,
          ticks: {
            color: '#860000',
          }
        },
        y: {
          ticks: {
            color: '#860000',
          }
        }
      },
      plugins : {
        datalabels: {
          color: '#860000',
          font: {
            weight: 'bold'
          },
          anchor: 'end',
          align: 'end',
          clamp: true
        },
        legend: {
          display: false,
        }
      }
    })

    watch( () => props.distribution, (val, oldVal) => {
      chartData.value = {
        labels: ['0-10%', '11-20%', '21-30%', '31-40%', '41-50%', '51-60%', '61-70%', '71-80%', '81-90%', '91-100%'],
        datasets: [{
          label: 'Count',
          data: val,
          backgroundColor: 'rgba(149, 168, 241, 0.5)',
          borderWidth: 2,
          borderColor: 'rgba(149, 168, 241, 1)',
        }]
      }

      maxVal.value = Math.max(...val)
      options.value = {
        responsive: true,
        maintainAspectRatio: false,
        indexAxis: 'y',
        scales: {
          x: {
            suggestedMax: maxVal.value + 5,
            ticks: {
              color: '#860000',
            }
          },
          y: {
            ticks: {
              color: '#860000',
            }
          }
        },
        plugins: {
          datalabels: {
            color: '#be1523',
            font: {
              weight: 'bold'
            },
            anchor: 'end',
            align: 'end',
            clamp: true
          },
          legend: {
            display: false,
          }
        }
      }
    })

    return {
      chartData,
      options
    }
  }

})
</script>

<style scoped>
.wrapper {
  background: var(--background-color-accent);
  border-radius: 10px;
  box-shadow: #777777 2px 3px 3px;
  display: flex;
  height: 100%;

}

.chart {
  width: 100%;

}
</style>