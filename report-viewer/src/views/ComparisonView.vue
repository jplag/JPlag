<template>
  <div class="container">
    <button id="show-button" :class="{hidden : !hideLeftPanel}" @click="togglePanel" title="Show sidebar">
      <img src="@/assets/double_arrow_white_24dp.svg" alt="show">
    </button>
    <div id="sidebar" :class="{ hidden : hideLeftPanel }">
      <div class="title-section">
        <h1>JPlag Comparison</h1>
        <button id="hide-button" @click="togglePanel" title="Hide sidebar">
          <img src="@/assets/keyboard_double_arrow_left_white_24dp.svg" alt="hide"></button>
      </div>
      <TextInformation label="Submission 1" :value="id1"/>
      <TextInformation label="Submission 2" :value="id2"/>
      <TextInformation label="Match %" :value="comparison.match_percentage"/>
      <MatchTable :matches="comparison.allMatches" :id1="id1" :id2="id2" @match-selected="showMatch" />
    </div>
    <FilesContainer :matches="comparison.matchesInFirstSubmission" :files="filesOfFirst" container-id="files1"
                    files-owner="Submission 1"
                    @toggle-collapse="toggleCollapseFirst"
                    @line-selected="showMatchInSecond"/>
    <FilesContainer :matches="comparison.matchesInSecondSubmissions" :files="filesOfSecond" container-id="files2"
                    files-owner="Submission 2"
                    @toggle-collapse="toggleCollapseSecond"
                    @line-selected="showMatchInFirst"/>
  </div>
</template>

<script>
import { defineComponent, ref } from "vue";
import {generateLineCodeLink} from "@/utils/Utils";
import { VueDraggableNext } from 'vue-draggable-next'
import CodePanel from "@/components/CodePanel";
import TextInformation from "@/components/TextInformation";
import MatchTable from "@/components/MatchTable";
import {ComparisonFactory} from "@/model/factories/ComparisonFactory";
import FilesContainer from "@/components/FilesContainer";

export default defineComponent({
  name: "ComparisonView",
  components: {FilesContainer, MatchTable, TextInformation },
  props: {
    id1: {
      type: String,
    },
    id2: {
      type: String,
    },
    notBlurred: {
      type: Array
    }
  },
  setup(props) {
    const fileName = props.id1.concat("-").concat(props.id2)
    // eslint-disable-next-line @typescript-eslint/no-var-requires
    const json = require(`../files/${fileName}.json`)
    const comparison = ComparisonFactory.getComparison(json)

    const filesOfFirst = ref(comparison.filesOfFirstSubmission)
    const filesOfSecond = ref(comparison.filesOfSecondSubmission)

    const toggleCollapseFirst = (title) => { filesOfFirst.value[title].collapsed = !filesOfFirst.value[title].collapsed }
    const toggleCollapseSecond = (title) => { filesOfSecond.value[title].collapsed = !filesOfSecond.value[title].collapsed }

    const showMatchInFirst = ( e, panel, file, line ) => {
      if( !filesOfFirst.value[file].collapsed ) { toggleCollapseFirst(file) }
      document.getElementById(generateLineCodeLink(panel, file, line)).scrollIntoView()
    }

    const showMatchInSecond = ( e, panel, file, line ) => {
      if( !filesOfSecond.value[file].collapsed ) { toggleCollapseSecond(file) }
      document.getElementById(generateLineCodeLink(panel, file, line)).scrollIntoView()
    }

    const showMatch = ( e, match ) => {
      showMatchInFirst(e, 1, match.firstFile, match.startInFirst)
      showMatchInSecond(e, 2, match.secondFile, match.startInSecond)
    }


    const hideLeftPanel = ref(true)
    const togglePanel = () => {
      hideLeftPanel.value = !hideLeftPanel.value
    }

    return {
      comparison,
      filesOfFirst,
      filesOfSecond,
      hideLeftPanel,

      toggleCollapseFirst,
      toggleCollapseSecond,
      showMatchInFirst,
      showMatchInSecond,
      showMatch,
      togglePanel
    }
  }
})
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

.files-container {
  display: flex;
  flex-wrap: nowrap;
  flex-direction: column;
  padding-top: 1%;
  width: 100%;
  overflow: auto;
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
</style>