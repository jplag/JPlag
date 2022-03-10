<!--
  Radar chart which displays a cluster member with their similarity to other
  participants in the cluster.
-->
<template>
  <div class="wrapper">
    <select v-model="selectedMember">
      <option v-for="(member, index) in Object.keys(cluster.members)" :key="index">{{ member }}</option>
    </select>
    <RadarChart :chartData="chartData" :options="options" class="chart"></RadarChart>
  </div>
</template>

<script>
import {defineComponent, ref, watch} from "vue";
import {RadarChart} from "vue-chart-3"
import {Chart, registerables} from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';

Chart.register(...registerables);
Chart.register(ChartDataLabels);

export default defineComponent({
  name: "ClusterRadarChart",
  components: {RadarChart},
  props: {
    cluster: {}
  },
  setup(props) {
    const selectedMember = ref("")

    const createLabelsFor = (member) => {
      let matchedWith = []
      props.cluster.members[member].forEach(m => matchedWith.push(m.matchedWith))
      return matchedWith
    }
    const createDataSetFor = (member) => {
      let data = []
      props.cluster.members[member].forEach(m => data.push(m.percentage))
      return data
    }

    const chartStyle = {
      fill: true,
      backgroundColor: 'rgba(149, 168, 241, 0.5)',
      borderColor: 'rgba(149, 168, 241, 1)',
      pointBackgroundColor: 'rgba(149, 168, 241, 1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgb(255, 99, 132)'
    }

    const chartData = ref({
      labels: createLabelsFor(Object.keys(props.cluster.members)[0]),
      datasets: [{
        ...chartStyle,
        label: Object.keys(props.cluster.members)[0],
        data: createDataSetFor(Object.keys(props.cluster.members)[0])
      }]
    })

    const options = ref({
      scales: {
        r: {
          suggestedMin: 50,
          suggestedMax: 100
        }
      }
    })

    watch(() => selectedMember.value, (val) => {
      chartData.value = {
        labels: createLabelsFor(val),
        datasets: [{
          ...chartStyle,
          label: val,
          data: createDataSetFor(val),
        }]
      }
    })

    return {
      selectedMember,
      chartData,
      options
    }
  }
})
</script>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;

}

.chart {
  height: 50vw;
}
</style>