<!--
  A view displaying the overview file of a JPlag report.
-->
<template>
  <div class="container">
    <div class="column-container" style="width: 30%">
      <h1>JPlag Report</h1>
      <p class="section-title">Main Info:</p>
      <div id="basicInfo">
        <TextInformation
          :has-additional-info="hasMoreSubmissionPaths"
          :value="submissionPathValue"
          additional-info-title=""
          label="Directory path"
        >
          <p
            v-for="path in overview.submissionFolderPath"
            :key="path"
            :title="path"
          >
            {{ path }}
          </p>
        </TextInformation>
        <TextInformation
          :has-additional-info="true"
          :value="overview.language"
          additional-info-title="File extensions:"
          label="Language"
        >
          <p v-for="info in overview.fileExtensions" :key="info">{{ info }}</p>
        </TextInformation>
        <TextInformation
          :value="overview.matchSensitivity"
          label="Match Sensitivity"
        />
        <TextInformation
          :has-additional-info="true"
          :value="store.getters.getSubmissionIds.size"
          additional-info-title="Submission IDs:"
          label="Submissions"
        >
          <IDsList :ids="store.getters.getSubmissionIds" @id-sent="handleId" />
        </TextInformation>
        <TextInformation
          :value="overview.dateOfExecution"
          label="Date of execution"
        />
        <TextInformation
          :value="overview.durationOfExecution"
          label="Duration (in ms)"
        />
      </div>
      <div id="logo-section">
        <img id="logo" alt="JPlag" src="@/assets/logo-nobg.png" />
      </div>
    </div>

    <div class="column-container" style="width: 35%">
      <div id="metrics">
        <p class="section-title">Metric:</p>
        <div id="metrics-list">
          <MetricButton
            v-for="(metric, index) in overview.metrics"
            :id="metric.metricName"
            :key="metric.metricName"
            :is-selected="selectedMetric[index]"
            :metric="metric"
            @click="selectMetric(index)"
          />
        </div>
      </div>
      <p class="section-title">Distribution:</p>
      <DistributionDiagram
        :distribution="distributions[selectedMetricIndex]"
        class="full-width"
      />
    </div>
    <div class="column-container" style="width: 35%">
      <p class="section-title">Top Comparisons:</p>
      <div id="comparisonsList">
        <ComparisonsTable
          :clusters="overview.clusters"
          :top-comparisons="topComps[selectedMetricIndex]"
        />
      </div>
      <div v-if="missingComparisons!==0 && !isNaN(missingComparisons)">
        <h3>Total comparisons: {{overview.totalComparisons}}, Shown comparisons: {{shownComparisons}}, Missing comparisons: {{missingComparisons}}. To see more, re-run JPlag with a higher maximum number argument.</h3>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onErrorCaptured, Ref, ref } from "vue";
import router from "@/router";
import TextInformation from "../components/TextInformation.vue";
import DistributionDiagram from "@/components/DistributionDiagram.vue";
import MetricButton from "@/components/MetricButton.vue";
import ComparisonsTable from "@/components/ComparisonsTable.vue";
import { OverviewFactory } from "@/model/factories/OverviewFactory";
import IDsList from "@/components/IDsList.vue";
import { useStore } from "vuex";
import { Overview } from "@/model/Overview";
import { ComparisonListElement } from "@/model/ComparisonListElement";

export default defineComponent({
  name: "OverviewView",
  components: {
    IDsList,
    ComparisonsTable,
    DistributionDiagram,
    MetricButton,
    TextInformation,
  },
  setup() {
    const store = useStore();
    const overviewFile = computed(() => {
      console.log("Start finding overview.json in state...")
      const index = Object.keys(store.state.files).find((name) =>
        name.endsWith("overview.json")
      );
      return index != undefined
        ? store.state.files[index]
        : console.log("Could not find overview.json");
    });

    const getOverview = (): Overview => {
      console.log("Generating overview...")
      let temp!: Overview;
      //Gets the overview file based on the used mode (zip, local, single).
      if (store.state.local) {
        try {
          // eslint-disable-next-line @typescript-eslint/no-var-requires
          temp = OverviewFactory.getOverview(require("../files/overview.json"));
        } catch (e) {
          router.back();
        }
      } else if (store.state.zip) {
        if(overviewFile.value===undefined){
          return new Overview([],"","",[],0,"",0,[],[],0, new Map<string, Map<string, string>>());
        }
        const overviewJson = JSON.parse(overviewFile.value);
        temp = OverviewFactory.getOverview(overviewJson);
      } else if (store.state.single) {
        temp = OverviewFactory.getOverview(JSON.parse(store.state.fileString));
      }
      return temp;
    };

    let overview = getOverview();


    /**
     * Handles the selection of an Id to anonymize.
     * If all submission ids are provided as parameter it hides or displays them based on their previous state.
     * If a single id is provided it hides all of the other ids except for the chosen one.
     * @param ids
     */
    const handleId = (ids: Array<string>) => {
      if (ids.length === store.getters.getSubmissionIds.length) {
        if (store.state.anonymous.size > 0) {
          store.commit("resetAnonymous");
        } else {
          store.commit("addAnonymous", ids);
        }
      } else {
        if (store.state.anonymous.has(ids[0])) {
          store.commit("removeAnonymous", ids);
        } else {
          if (store.state.anonymous.size === 0) {
            store.commit(
              "addAnonymous",
              store.getters.getSubmissionIds.filter((s: string) => s !== ids[0])
            );
          } else {
            store.commit("addAnonymous", ids);
          }
        }
      }
    };

    //Metrics
    /**
     * Current metric to display distribution and comparisons for.
     * @type {Ref<UnwrapRef<boolean[]>>}
     */
    let selectedMetric = ref(overview.metrics.map(() => false));
    /**
     * Index of current selected metric. Used to obtain information for the metric from the distribution and top
     * comparisons array.
     * @type {Ref<UnwrapRef<number>>}
     */
    let selectedMetricIndex = ref(0);
    selectedMetric.value[selectedMetricIndex.value] = true;

    const selectMetric = (metric: number) => {
      selectedMetric.value = selectedMetric.value.map(() => false);
      selectedMetric.value[metric] = true;
      selectedMetricIndex.value = metric;
    };

    //Distribution
    let distributions = ref(overview.metrics.map((m) => m.distribution));

    //Top Comparisons
    let topComps: Ref<Array<Array<ComparisonListElement>>> = ref(
      overview.metrics.map((m) => m.comparisons)
    );

    const hasMoreSubmissionPaths = overview.submissionFolderPath.length > 1;
    const submissionPathValue = hasMoreSubmissionPaths
      ? "Click arrow to see all paths"
      : overview.submissionFolderPath[0];

    const shownComparisons = computed(()=>{
      return overview.metrics[selectedMetricIndex.value]?.comparisons.length;
    });
    const missingComparisons = overview.totalComparisons - shownComparisons.value;

    onErrorCaptured(()=>{
      router.push({
        name: "ErrorView",
        state: {
          message: "Overview.json can't be found!",
          to: "/",
          routerInfo: "back to FileUpload page"
        }
      });
      store.commit("clearStore");
      return false;
    });

    return {
      overview,
      selectedMetricIndex,
      selectedMetric,
      distributions,
      topComps,
      hasMoreSubmissionPaths,
      submissionPathValue,
      shownComparisons,
      missingComparisons,
      handleId,
      selectMetric,
      store,
    };
  },
});
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
  background: linear-gradient(to right, #edf2fb, transparent, transparent);
  width: 100%;
  box-shadow: #d7e3fc 0 1px;
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
