<!--
  A view displaying the .json file of a comparison from a JPlag report.
-->
<template>
  <div class="container">
    <button
      id="show-button"
      :class="{ hidden: !hideLeftPanel }"
      title="Show sidebar"
      @click="togglePanel"
    >
      <img alt="show" src="@/assets/double_arrow_black_24dp.svg" />
    </button>
    <div id="sidebar" :class="{ hidden: hideLeftPanel }">
      <div class="title-section">
        <h1>JPlag Comparison</h1>
        <button id="hide-button" title="Hide sidebar" @click="togglePanel">
          <img
            alt="hide"
            src="@/assets/keyboard_double_arrow_left_black_24dp.svg"
          />
        </button>
      </div>
      <div>
        <button class="animated-back-button" title="Back button" @click="back">back</button>
      </div>
      <TextInformation
        :anonymous="isAnonymous(firstId)"
        :value="store.getters.submissionDisplayName(firstId)"
        label="Submission 1"
      />
      <TextInformation
        :anonymous="store.state.anonymous.has(secondId)"
        :value="store.getters.submissionDisplayName(secondId)"
        label="Submission 2"
      />
      <TextInformation :value="(comparison.similarity * 100).toFixed(2)" label="Match %" />
      <MatchTable
        :id1="firstId"
        :id2="secondId"
        :matches="comparison.allMatches"
        @match-selected="showMatch"
      />
    </div>
    <FilesContainer
      :container-id="1"
      :submission-id="firstId"
      :files="filesOfFirst"
      :matches="comparison.matchesInFirstSubmission"
      :files-owner="store.getters.submissionDisplayName(firstId)"
      :anonymous="store.state.anonymous.has(firstId)"
      files-owner-default="submission 1"
      @toggle-collapse="toggleCollapseFirst"
      @line-selected="showMatchInSecond"
    />
    <FilesContainer
      :container-id="2"
      :submission-id="secondId"
      :files="filesOfSecond"
      :matches="comparison.matchesInSecondSubmissions"
      :files-owner="store.getters.submissionDisplayName(secondId)"
      :anonymous="store.state.anonymous.has(secondId)"
      files-owner-default="submission 2"
      @toggle-collapse="toggleCollapseSecond"
      @line-selected="showMatchInFirst"
    />
  </div>
</template>

<script lang="ts">
import {defineComponent, ref} from "vue";
import { generateLineCodeLink } from "@/utils/Utils";
import TextInformation from "@/components/TextInformation.vue";
import MatchTable from "@/components/MatchTable.vue";
import { ComparisonFactory } from "@/model/factories/ComparisonFactory";
import FilesContainer from "@/components/FilesContainer.vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { Match } from "@/model/Match";
import {Comparison} from "@/model/Comparison";

export default defineComponent({
  name: "ComparisonView",
  components: { FilesContainer, MatchTable, TextInformation },
  props: {
    firstId: {
      type: String,
      required: true,
    },
    secondId: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const store = useStore();
    const router = useRouter();
    console.log("Generating comparison {%s} - {%s}...", props.firstId, props.secondId);
    let comparison;
    //getting the comparison file based on the used mode (zip, local, single)
    if (store.state.local) {
      try {
        comparison = ComparisonFactory.getComparison(
          // eslint-disable-next-line @typescript-eslint/no-var-requires
          require(`../files/${store.getters.getComparisonFileName(
            props.firstId,
            props.secondId
          )}.json`)
        );
      } catch (exception) {
        router.back();
      }
    } else if (store.state.zip) {
      let comparisonFile = store.getters.getComparisonFileForSubmissions(
        props.firstId,
        props.secondId
      );
      if (comparisonFile) {
        comparison = ComparisonFactory.getComparison(
          JSON.parse(comparisonFile)
        );
      } else {
        console.log("Comparison file not found!");
        router.push({
          name: "ErrorView",
          state: {
            message: "Comparison file not found!",
            to: "/overview",
            routerInfo: "back to overview page"
          }
        });
      }
    } else if (store.state.single) {
      try {
        comparison = ComparisonFactory.getComparison(
            JSON.parse(store.state.fileString)
        );
      }catch (e){
        router.push({
          name: "ErrorView",
          state: {
            message: "Source code of matches not found. To only see the overview, please drop the overview.json directly.",
            to: "/",
            routerInfo: "back to FileUpload page"
          }
        });
        store.commit("clearStore");
      }
    }
    if (!comparison) {
      comparison=new Comparison("","",0);
      console.log("Unable to build comparison file.");
    }
    const filesOfFirst = ref(comparison.filesOfFirstSubmission);
    const filesOfSecond = ref(comparison.filesOfSecondSubmission);

    /**
     * Collapses a file in the first files container.
     * @param title
     */
    const toggleCollapseFirst = (title: string) => {
      const file = filesOfFirst.value.get(title);
      if (file) {
        file.collapsed = !file.collapsed;
      }
    };
    /**
     * Collapses a file in the second files container.
     * @param title
     */
    const toggleCollapseSecond = (title: string) => {
      const file = filesOfSecond.value.get(title);
      if (file) {
        file.collapsed = !file.collapsed;
      }
    };
    /**
     * Shows a match in the first files container
     * @param e
     * @param panel
     * @param file
     * @param line
     */
    const showMatchInFirst = (
      e: unknown,
      panel: number,
      file: string,
      line: number
    ) => {
      if (!filesOfFirst.value.get(file)?.collapsed) {
        toggleCollapseFirst(file);
      }
      document
        .getElementById(generateLineCodeLink(panel, file, line))
        ?.scrollIntoView();
    };
    /**
     * Shows a match in the second files container.
     * @param e
     * @param panel
     * @param file
     * @param line
     */
    const showMatchInSecond = (
      e: unknown,
      panel: number,
      file: string,
      line: number
    ) => {
      if (!filesOfSecond.value.get(file)?.collapsed) {
        toggleCollapseSecond(file);
      }
      document
        .getElementById(generateLineCodeLink(panel, file, line))
        ?.scrollIntoView();
    };

    const showMatch = (e: unknown, match: Match) => {
      showMatchInFirst(e, 1, match.firstFile, match.startInFirst);
      showMatchInSecond(e, 2, match.secondFile, match.startInSecond);
    };

    const isAnonymous = (id: string) => store.state.anonymous.has(id);
    //Left panel
    const hideLeftPanel = ref(false);
    const togglePanel = () => {
      hideLeftPanel.value = !hideLeftPanel.value;
    };

    const back = () => {
      router.back();
    };

    return {
      comparison,
      filesOfFirst,
      filesOfSecond,
      hideLeftPanel,
      store,

      toggleCollapseFirst,
      toggleCollapseSecond,
      showMatchInFirst,
      showMatchInSecond,
      showMatch,
      togglePanel,
      isAnonymous,
      back,
    };
  },
});
</script>

<style scoped>
h1 {
  color: var(--on-primary-color);
  text-align: center;
}

.container {
  display: flex;
  align-items: stretch;
  flex-wrap: nowrap;
  width: 100%;
  height: 100%;
  background: var(--background-color);
}

.title-section {
  display: flex;
  justify-content: space-between;
}

.title-section > h1 {
  text-align: left !important;
}

.hidden {
  display: none !important;
}

#sidebar {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  width: 60%;
  background: var(--primary-color-light);
  padding: 1%;
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
}

#hide-button {
  display: flex;
  flex-direction: column;
  background: transparent;
  border-radius: 10px;
  height: max-content;
  border: none;
}

#hide-button:hover {
  cursor: pointer;
  background: var(--primary-color-dark);
}

#show-button {
  position: absolute;
  z-index: 1000;
  left: 0;
  background: var(--secondary-color);
  border: none;
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
  height: 100%;
  width: 1%;
}

#show-button img {
  display: none;
}

#show-button:hover {
  cursor: pointer;
  width: 3%;
}

#show-button:hover img {
  display: block;
}

.animated-back-button{
  float: right;
  height: 100%;
  position: relative;

  font-size: 1.4rem;
  background: var(--primary-color-dark);
  background-size: 46px 26px;
  border: 1px solid #555;
  color: black;
  transition: all ease 0.3s;
}

.animated-back-button::after{
  position: absolute;
  top: 50%;
  right: 0.6em;
  transform: translateY(-50%);
  content: "«";
  font-size: 1.2em;
  transition: all ease 0.3s;
  opacity: 0;
}

.animated-back-button:hover{
  padding: 20px 60px 20px 20px;
}

.animated-back-button:hover::after{
  right: 1.2em;
  opacity: 1;
}
</style>
