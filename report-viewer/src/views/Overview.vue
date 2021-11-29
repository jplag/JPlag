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
      <div id="metricsButtons" class="section">
        <p class="section-title">Metrics: </p>
        <ul class="metrics-list" v-for="(metric, i) in json.metrics" :key="metric.name">
          <li><MetricButton :id="metric.name"
                            :metric-threshold="metric.threshold"
                            :metric-name="metric.name"
                            :is-selected="selectedMetric[i]"
                            @click="selectMetric(i)"/></li>
        </ul>
      </div>
      <div id="distribution" class="section">
        <p class="section-title ">Distribution:</p>
        <DistributionDiagram :distribution="distributions[selectedMetricIndex]"/>
      </div>
      <div id="topComparisonsList" class="section">
        <p class="section-title">Top comparisons:</p>
        <p class="section-subtitle">(Top 25)</p>
        <ComparisonsLists :comparisons="topComps[selectedMetricIndex]"/>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent, ref, watchEffect} from "vue";
import TextInformation from "@/components/TextInformation";
import MetricButton from "@/components/MetricButton";
import DistributionDiagram from "@/components/DistributionDiagram";
import ComparisonsLists from "@/components/ComparisonsLists";

export default defineComponent({
  name: "Overview",
  components: {ComparisonsLists, DistributionDiagram, MetricButton, TextInformation},
  props: {
    jsonString: {
      type: String,

    }
  },
  setup(props) {
    const json = JSON.parse(props.jsonString)
    let selectedMetric = ref(json.metrics.map( () => false ))
    let distributions = ref(json.metrics.map( (m) =>  m.distribution ))
    let topComps = ref(json.metrics.map((m) => m.topComparisons))
    console.log(JSON.stringify(topComps.value))
    let selectedMetricIndex = ref(0)
    selectedMetric.value[0] = true;

    const selectMetric = (metric) => {
      selectedMetric.value = selectedMetric.value.map( () => { return false })
      selectedMetric.value[metric] = true
      selectedMetricIndex.value = metric
    }

    watchEffect( () => {
      console.log("Overview - selected dist " + JSON.stringify(distributions.value[selectedMetricIndex.value]))
      console.log("Overview - selected topComp " + JSON.stringify(topComps.value[selectedMetricIndex.value]))
    })

    return {
      json,
      selectedMetric,
      selectedMetricIndex,
      distributions,
      topComps,
      selectMetric
    }
  }
})
</script>

<style scoped>
.section {
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
}

.container {
  display: flex;
  align-items: stretch;
  width: 100%;
  height: 100%;
  margin: 0;
}

.section-title {
  font-size: x-large;
  font-weight: bold;
  text-align: start;
  margin-top: 3%;
  margin-bottom: 1%;
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
  flex-direction: column;
  flex-wrap: nowrap;
  padding: 1%;
}

#metricsButtons {
  flex-direction: row;
  flex-shrink: 3;
  align-items: flex-start;
}

#metricsButtons > * {
  margin-right: 1%;
}

#distribution {
  flex-shrink: 2;
  align-items: stretch;
}

#topComparisonsList {
  min-height: 0;
  align-items: stretch;
}


#logo {
  width: 40%;
  height: 20%;
  margin-bottom: 5%;
}




</style>