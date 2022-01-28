<template>
  <div class="container">
    <div class="column-container" style="width: 30%">
      <h1>JPlag Report</h1>
      <p class="section-title">Main Info:</p>
      <div id="basicInfo">
        <TextInformation label="Directory path" :value="overview.submissionFolderPath"/>
        <TextInformation label="Language" :value="overview.language" :has-additional-info="true" additional-info-title="File extensions:">
          <p v-for="info in overview.fileExtensions" :key="info">{{ info }}</p>
        </TextInformation>
        <TextInformation :value="overview.matchSensitivity" label="Match Sensitivity"/>
        <TextInformation label="Submissions" :value="overview.submissionIds.length" :has-additional-info="true" additional-info-title="Submission IDs:">
          <IDsList :ids="overview.submissionIds" @id-sent="handleId"/>
        </TextInformation>
        <TextInformation label="Date of execution" :value="overview.dateOfExecution"/>
        <TextInformation label="Duration (in ms)" :value="overview.durationOfExecution"/>
      </div>
      <div id="logo-section">
        <img id="logo" src="@/assets/logo-nobg.png" alt="JPlag">
      </div>
    </div>

      <div class="column-container" style="width: 35%">
        <div id="metrics">
          <p class="section-title">Metric:</p>
          <div id="metrics-list">
            <MetricButton v-for="(metric, i) in overview.metrics" :key="metric.metricName"
                          :id="metric.metricName"
                          :metric-threshold="metric.metricThreshold"
                          :metric-name="metric.metricName"
                          :is-selected="selectedMetric[i]"
                          @click="selectMetric(i)"/>
          </div>
        </div>
        <p class="section-title">Distribution:</p>
        <DistributionDiagram class="full-width" :distribution="distributions[selectedMetricIndex]"/>
      </div>
    <div class="column-container" style="width: 35%">
        <p class="section-title">Top Comparisons:</p>
        <p class="section-subtitle">(Top n comparisons)</p>
        <div id="comparisonsList">
          <ComparisonsTable :top-comparisons="topComps[selectedMetricIndex]" :not-anonymized="notAnonymized"/>
        </div>
    </div>
  </div>
</template>

<script>
import {defineComponent, ref} from "vue";
import TextInformation from "../components/TextInformation";
import DistributionDiagram from "@/components/DistributionDiagram";
import MetricButton from "@/components/MetricButton";
import Overview from "../files/overview.json"
import ComparisonsTable from "@/components/ComparisonsTable";
import {OverviewFactory} from "@/model/factories/OverviewFactory";
import IDsList from "@/components/IDsList";

export default defineComponent({
  name: "Overview",
  components: {IDsList, ComparisonsTable, DistributionDiagram, MetricButton, TextInformation },
  props: {
  },
  setup() {
    const overview = OverviewFactory.getOverview(Overview)
    let notAnonymized = ref([...overview.submissionIds])

    const handleId = (...id) => {
      if( id.length === overview.submissionIds.length) {
        if(notAnonymized.value.length > 0) {
          notAnonymized.value = []
        } else {
          notAnonymized.value = [...overview.submissionIds]
        }
      } else {
          if ( notAnonymized.value.length === overview.submissionIds.length) {
            notAnonymized.value = []
          }
          if (notAnonymized.value.includes(id[0])) {
            notAnonymized.value.splice(notAnonymized.value.indexOf(id[0]), 1)
          } else {
            notAnonymized.value.push(id[0])
          }
      }
    }


    //Metrics
    let selectedMetric = ref(overview.metrics.map( () => false ))
    let selectedMetricIndex = ref(0)
    selectedMetric.value[0] = true;
    
    const selectMetric = (metric) => {
      selectedMetric.value = selectedMetric.value.map( () => { return false })
      selectedMetric.value[metric] = true
      selectedMetricIndex.value = metric
    }

    //Distribution
    let distributions = ref(overview.metrics.map( (m) =>  m.distribution ))

    //Top Comparisons
    let topComps = ref(overview.metrics.map((m) => m.comparisons ))


    return {
      overview,
      selectedMetricIndex,
      selectedMetric,
      distributions,
      topComps,
      notAnonymized,
      handleId,
      selectMetric
    }
  }
})
</script>

<style scoped>
h1 {
  text-align: left;
  margin-top: 2%;
  color: var(--on-background-color);
}

hr {
  border: 0;
  height: 2px;
  background: linear-gradient(to right, #EDF2FB, transparent, transparent);
  width: 100%;
  box-shadow: #D7E3FC 0 1px;
}

.container {
  display: flex;
  align-items: stretch;
  width: 100%;
  height: 100%;
  margin: 0;
  overflow: auto;
  background: var(--background-color);
}

.column-container {
  display: flex;
  flex-direction: column;
  padding: 1%;
}

.full-width {
  width: 100%;
}

.section-title {
  font-size: x-large;
  font-weight: bold;
  text-align: start;
  margin: 0;
  padding: 0;
  color: var(--on-background-color);
}

.section-subtitle {
  font-size: small;
  text-align: start;
  margin-top: 0;
}


#basicInfo {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  padding: 3%;
  margin-top: 1%;
  background: var(--primary-color-light);
  border-radius: 10px;
  box-shadow: var(--shadow-color) 2px 3px 3px;
}

#metrics {
  display: flex;
  justify-content: start;
  margin-bottom: 1%;
}

#metrics-list {
  display: flex;
  margin-left: 2%;
}

#comparisonsList {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  padding: 2%;
  background: var(--primary-color-light);
  border-radius: 10px;
  box-shadow: var(--shadow-color) 2px 3px 3px;
}

#logo-section {
  justify-content: center;
  align-items: center;
  padding: 5%;
  display: flex;
}

#logo {
  flex-shrink: 2;
}


</style>