<template>
  <div class="container">
    <button id="show-button" v-bind:class="{hidden : !hideLeftPanel}" @click="togglePanel" title="Show sidebar">
      <img src="@/assets/double_arrow_white_24dp.svg" alt="show">
    </button>
    <div id="sidebar" :class="{ hidden : hideLeftPanel }">
      <button id="hide-button" @click="togglePanel" title="Hide sidebar"><img src="@/assets/double_arrow_white_24dp.svg" alt="hide"></button>
      <TextInformation label="Submission 1" :value="id1" :has-additional-info="false"/>
      <TextInformation label="Submission 2" :value="id2" :has-additional-info="false"/>
      <TextInformation label="Match %" :value="json.match_percentage" :has-additional-info="false"/>
    </div>
    <div class="files-container" id="files1">
      <VueDraggableNext>
      <CodePanel v-for="(file, index) in Object.keys(filesOfFirst)"
                 :lines="filesOfFirst[file]"
                 :title="file"
                 :file-index="index"
                 :matches="!matchesInFirst[file] ? [] : matchesInFirst[file]"
                 :key="file.concat(index)"
                 :collapse="filesOfFirstCollapsed[index]"
                 @toggle-collapse="toggleCollapseFirst(index)"
                 @line-selected="showMatchInSecond"
                 panel-id="1"
      />
      </VueDraggableNext>
    </div>
    <div class="files-container" id="files2">
      <VueDraggableNext>
        <CodePanel v-for="(file, index) in Object.keys(filesOfSecond)"
                   :lines="filesOfSecond[file]"
                   :title="file"
                   :file-index="index"
                   :matches="!matchesInSecond[file] ? [] : matchesInSecond[file]"
                   :key="file.concat(index)"
                   :collapse="filesOfSecondCollapsed[index]"
                   @toggle-collapse="toggleCollapseSecond(index)"
                   @line-selected="showMatchInFirst"
                   panel-id="2"
                   />
      </VueDraggableNext>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from "vue";
import {convertToFilesByName, generateColor, groupMatchesByFileName} from "@/utils/Utils";
import { VueDraggableNext } from 'vue-draggable-next'
import CodePanel from "@/components/CodePanel";
import TextInformation from "@/components/TextInformation";

export default defineComponent({
  name: "ComparisonView2",
  components: {TextInformation, VueDraggableNext, CodePanel},
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

    const filesOfFirst = convertToFilesByName(json.files_of_first_submission)
    const filesOfSecond = convertToFilesByName(json.files_of_second_submission)
    const filesOfFirstCollapsed = ref(Object.keys(filesOfFirst).map( _ => false ))
    const filesOfSecondCollapsed = ref(Object.keys(filesOfSecond).map( _ => false ))
    const coloredMatches = json.matches.map( m => { return {...m, color : generateColor()} })

    const matchesInFirst = groupMatchesByFileName(coloredMatches, 1)
    const matchesInSecond = groupMatchesByFileName(coloredMatches, 2)

    const toggleCollapseFirst = (index) => { filesOfFirstCollapsed.value[index] = !filesOfFirstCollapsed.value[index] }
    const toggleCollapseSecond = (index) => { filesOfSecondCollapsed.value[index] = !filesOfSecondCollapsed.value[index] }

    const showMatchInFirst = ( e, fileIndex, matchLink ) => {
      if( !filesOfFirstCollapsed.value[fileIndex] ) { toggleCollapseFirst(fileIndex) }
      document.getElementById(matchLink).scrollIntoView()
    }

    const showMatchInSecond = ( e, fileIndex, matchLink ) => {
      if( !filesOfSecondCollapsed.value[fileIndex] ) { toggleCollapseSecond(fileIndex) }
      document.getElementById(matchLink).scrollIntoView()
    }

    const hideLeftPanel = ref(true)
    const togglePanel = () => {
      hideLeftPanel.value = !hideLeftPanel.value
    }

    return {
      json,
      filesOfFirst,
      filesOfFirstCollapsed,
      filesOfSecond,
      filesOfSecondCollapsed,
      matchesInFirst,
      matchesInSecond,
      hideLeftPanel,

      toggleCollapseFirst,
      toggleCollapseSecond,
      showMatchInFirst,
      showMatchInSecond,
      togglePanel
    }
  }
})
</script>

<style scoped>
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
  width: 100%;
  height: max-content;
  padding: 1%;
  overflow: auto;
}

.hidden {
  display: none !important;
}

#sidebar {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--primary-color-light);
  padding: 1%;
}

#hide-button {
  background: transparent;
  border: none;
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