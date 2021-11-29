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
    let chartData = ref( {
        labels: ['0-10%', '11-20%', '21-30%', '31-40%', '41-50%', '51-60%', '61-70%', '71-80%', '81-90%', '91-100%'],
        datasets: [{
          label: 'Count',
          data: props.distribution,
          backgroundColor: '#BA1616'
        }]
    })

    watch( () => props.distribution, (val, oldVal) => {
      chartData.value = {
        labels: ['0-10%', '11-20%', '21-30%', '31-40%', '41-50%', '51-60%', '61-70%', '71-80%', '81-90%', '91-100%'],
        datasets: [{
          label: 'Count',
          data: val,
          backgroundColor: '#BA1616'
        }]
      }
    })

    const options = {
      responsive: true,
      maintainAspectRatio: false,
      plugins : {
        datalabels: {
          color: '#000000',
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

    return {
      chartData,
      options
    }
  }

})
</script>

<style scoped>
.wrapper {
  background: white;
  border-radius: 10px;
  box-shadow: #777777 2px 3px 3px;
  display: flex;
  padding: 2%;
}

.chart {
  max-height: 200px;
  width: 100%;
}
</style>