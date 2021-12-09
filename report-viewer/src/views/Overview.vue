<template>
  <div class="container">
    <div id="leftPanel">
      <img id="logo" src="@/assets/logo.png" alt="JPlag">
      <TextInformation label="Directory path: " :value="json.submission_folder_path" :has-additional-info="false"/>
      <TextInformation label="Base code path: " :value="json.base_code_folder_path" :has-additional-info="false"/>
      <TextInformation label="Language: " :value="json.language" :has-additional-info="true" additional-info-label="see extensions"/>
      <TextInformation label="Submissions: " :value="json.submission_ids.length" :has-additional-info="true" additional-info-label="see ids"/>
      <TextInformation label="Failed to parse: " value="No info yet" :has-additional-info="true" additional-info-label="see ids"/>
      <TextInformation label="Date of execution: " :value="json.date_of_execution" :has-additional-info="false"/>
      <TextInformation label="Duration (in ms): " :value="json.execution_time" :has-additional-info="false"/>
    </div>
    <div id="rightPanel">
      <div class="right-panel-section">
        <div id="metrics">
          <p class="section-title">Select Metric:</p>
          <div id="metrics-list">
            <MetricButton v-for="(metric, i) in json.metrics" :key="metric.name"
                          :id="metric.name"
                          :metric-threshold="metric.threshold"
                          :metric-name="metric.name"
                          :is-selected="selectedMetric[i]"
                          @click="selectMetric(i)"/>
          </div>
        </div>
        <div id="diagram">
          <p class="section-title">Distribution:</p>
          <DistributionDiagram :distribution="distributions[selectedMetricIndex]"/>
        </div>
      </div>
      <div id="topComparisons" class="right-panel-section">
        <p class="section-title">Top Comparisons:</p>
        <p class="section-subtitle">(Top n comparisons)</p>
        <ComparisonListElement v-for="(comparison, index) in topComps[selectedMetricIndex]"
                               :key = "comparison.first_submission
                                        + comparison.second_submission
                                        + comparison.match_percentage"
                               :index="index + 1"
                               :submission1="comparison.first_submission"
                               :submission2="comparison.second_submission"
                               :match-percentage="comparison.match_percentage"
        />
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent, ref} from "vue";
import TextInformation from "../components/TextInformation";
import DistributionDiagram from "@/components/DistributionDiagram";
import MetricButton from "@/components/MetricButton";
import ComparisonListElement from "@/components/ComparisonListElement";
import Overview from "../files/overview.json"

export default defineComponent({
  name: "OverviewV2",
  components: {ComparisonListElement, DistributionDiagram, MetricButton, TextInformation },
  props: {
    jsonString: {
      type: String,
    }
  },
  setup(props) {
    const json = Overview

    //Metrics
    let selectedMetric = ref(json.metrics.map( () => false ))
    selectedMetric.value[0] = true;

    let selectedMetricIndex = ref(0)

    const selectMetric = (metric) => {
      selectedMetric.value = selectedMetric.value.map( () => { return false })
      selectedMetric.value[metric] = true
      selectedMetricIndex.value = metric
    }

    //Distribution
    let distributions = ref(json.metrics.map( (m) =>  m.distribution ))

    //Top Comparisons
    let topComps = ref(json.metrics.map((m) => m.topComparisons))

    return {
      json,
      selectedMetricIndex,
      selectedMetric,
      distributions,
      topComps,

      selectMetric
    }
  }
})
</script>

<style scoped>
.container {
  display: flex;
  align-items: stretch;
  width: 100%;
  height: 100%;
  margin: 0;
  overflow: auto;
}

.right-panel-section {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  width: 50%;
  padding-left: 1%;
  padding-top: 1%;
  padding-bottom: 1%;
}

.section-title {
  font-size: x-large;
  font-weight: bold;
  text-align: start;
  margin: 0;
  padding: 0;
}

.section-subtitle {
  font-size: small;
  text-align: start;
  margin-top: 0;
}


.metrics-list {
  list-style: none;
  display: flex;
  padding: 0 1% 0 0;
  margin-bottom: 0;

}

#leftPanel {
  width: 30%;
  background: #FF5353;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  padding: 1%;
}

#rightPanel {
  width: 70%;
  background: #ECECEC;
  display: flex;
  flex-wrap: nowrap;
  padding: 0;
}

#metrics {
  display: flex;
  flex-direction: column;
  margin-bottom: 3%;
}

#metrics-list {
  display: flex;
}

#diagram {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  height: 100%;
}

#topComparisons {
  padding-right: 1%;
}

#logo {
  width: 40%;
  height: 20%;
  margin-bottom: 5%;
}


</style>