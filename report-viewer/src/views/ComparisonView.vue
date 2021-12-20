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
      <TextInformation label="Submission 1" :value="id1" :has-additional-info="false"/>
      <TextInformation label="Submission 2" :value="id2" :has-additional-info="false"/>
      <TextInformation label="Match %" :value="json.match_percentage" :has-additional-info="false"/>
      <MatchTable :matches="coloredMatches" :id1="id1" :id2="id2" @match-selected="showMatch" />
    </div>
    <div class="files-container" id="files1">
      <h1>Files of {{ id1 }}</h1>
      <VueDraggableNext>
      <CodePanel v-for="(file, index) in Object.keys(filesOfFirst)"
                 :lines="filesOfFirst[file].lines"
                 :title="file"
                 :file-index="index"
                 :matches="!matchesInFirst[file] ? [] : matchesInFirst[file]"
                 :key="file.concat(index)"
                 :collapse="filesOfFirst[file].collapsed"
                 @toggle-collapse="toggleCollapseFirst(file)"
                 @line-selected="showMatchInSecond"
                 :panel-id="1"
      />
      </VueDraggableNext>
    </div>
    <div class="files-container" id="files2">
      <h1>Files of {{ id2 }}</h1>
      <VueDraggableNext>
        <CodePanel v-for="(file, index) in Object.keys(filesOfSecond)"
                   :lines="filesOfSecond[file].lines"
                   :title="file"
                   :file-index="index"
                   :matches="!matchesInSecond[file] ? [] : matchesInSecond[file]"
                   :key="file.concat(index)"
                   :collapse="filesOfSecond[file].collapsed"
                   @toggle-collapse="toggleCollapseSecond(file)"
                   @line-selected="showMatchInFirst"
                   :panel-id="2"
                   />
      </VueDraggableNext>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from "vue";
import {convertToFilesByName, generateColorsForMatches,generateLineCodeLink, groupMatchesByFileName} from "@/utils/Utils";
import { VueDraggableNext } from 'vue-draggable-next'
import CodePanel from "@/components/CodePanel";
import TextInformation from "@/components/TextInformation";
import MatchTable from "@/components/MatchTable";

export default defineComponent({
  name: "ComparisonView",
  components: {MatchTable, TextInformation, VueDraggableNext, CodePanel},
  props: {
    id1: {
      type: String,
    },
    id2: {
      type: String,
    }
  },
  setup(props) {
    const fileName = props.id1.concat("-").concat(props.id2)
    // eslint-disable-next-line @typescript-eslint/no-var-requires
    const json = require(`../files/${fileName}.json`)

    const filesOfFirst = ref(convertToFilesByName(json.files_of_first_submission))
    const filesOfSecond = ref(convertToFilesByName(json.files_of_second_submission))
    const colors = generateColorsForMatches(json.matches.length)
    const coloredMatches = json.matches.map( (m, index) => { return {...m, color : colors[index]} })

    const matchesInFirst = groupMatchesByFileName(coloredMatches, 1)
    const matchesInSecond = groupMatchesByFileName(coloredMatches, 2)

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
      showMatchInFirst(e, 1, match.first_file_name, match.start_in_first)
      showMatchInSecond(e, 2, match.second_file_name, match.start_in_second)
    }


    const hideLeftPanel = ref(true)
    const togglePanel = () => {
      hideLeftPanel.value = !hideLeftPanel.value
    }

    return {
      json,
      filesOfFirst,
      filesOfSecond,
      matchesInFirst,
      matchesInSecond,
      coloredMatches,
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