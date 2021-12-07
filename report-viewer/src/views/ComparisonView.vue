<template>
<div class="container">
  <div id="leftPanel" v-bind:class="{hidden : hideLeftPanel}">
    <div class="logo-section">
      <img id="logo" src="@/assets/logo.png" alt="JPlag">
      <button id="hide-button" @click="togglePanel"><img src="@/assets/double_arrow_white_24dp.svg" alt="hide"></button>
    </div>
    <TextInformation :has-additional-info="false" value="Match Report" label=""/>
    <TextInformation label="First Submission:" value="324567" :has-additional-info="false"/>
    <TextInformation label="Second Submission:" value="789223" :has-additional-info="false"/>
    <TextInformation label="Match percentage:" value="75.34%" :has-additional-info="false"/>
    <MatchList :submission1="json.first_submission_id"
               :submission2="json.second_submission_id"
               :matches="groupedMatches"
                @selection-changed="selectFiles"
                @match-selected="selectMatch"/>
  </div>
  <div id="rightPanel" v-bind:class="{extended : hideLeftPanel}">
    <button id="show-button" v-bind:class="{hidden : !hideLeftPanel}" @click="togglePanel"><img src="@/assets/double_arrow_white_24dp.svg" alt="hide"></button>
    <CodePanel id="codePanel1" :lines="filesOfFirst[selectedFileOfFirst]" :coloring="coloringFirst" panel-id="1"/>
    <CodePanel :lines="filesOfSecond[selectedFileOfSecond]"  :coloring="coloringSecond" panel-id="2"/>
  </div>
</div>
</template>

<script>
import { defineComponent, ref } from "vue";
import TextInformation from "@/components/TextInformation";
import MatchList from "@/components/MatchList";
import CodePanel from "@/components/CodePanel";
import {convertToFilesByName, generateColoringArray, generateColor} from "@/utils/Utils";

export default defineComponent({
  name: "ComparisonView",
  components: {CodePanel, MatchList, TextInformation},
  props: {
    jsonString: {
      type: String
    }
  },
  setup(props) {
    const json = JSON.parse(props.jsonString)

    const filesOfFirst = convertToFilesByName(json.files_of_first_submission)
    const filesOfSecond = convertToFilesByName(json.files_of_second_submission)
    const selectedFileOfFirst = ref(Object.keys(filesOfFirst)[0])
    const selectedFileOfSecond = ref(Object.keys(filesOfSecond)[0])

    const groupedMatches = ref(json.matches.reduce( (acc, val) => {
          let name = val.first_file_name
          let subname = val.second_file_name
          if(!acc[name]) {
            acc[name] = {}
          }
          if(!acc[name][subname]) {
            acc[name][subname] = []
          }
          let newVal = {...val, color: generateColor()}
          acc[name][subname].push(newVal)
          return acc;
        }, {})
    )

    let coloringFirst = ref(generateColoringArray(groupedMatches.value[selectedFileOfFirst.value][selectedFileOfSecond.value], 1))
    let coloringSecond = ref(generateColoringArray(groupedMatches.value[selectedFileOfFirst.value][selectedFileOfSecond.value], 2))

    const selectFiles = (e , file1, file2) => {
      selectedFileOfFirst.value = file1
      selectedFileOfSecond.value = file2
      coloringFirst.value = generateColoringArray(groupedMatches.value[selectedFileOfFirst.value][selectedFileOfSecond.value], 1)
      coloringSecond.value = generateColoringArray(groupedMatches.value[selectedFileOfFirst.value][selectedFileOfSecond.value], 2)
    }

    const selectMatch = (e, s1, e1, s2, e2) => {
      document.getElementById("1".concat(s1)).scrollIntoView()
      document.getElementById("2".concat(s2)).scrollIntoView()
    }


    const hideLeftPanel = ref(false)
    const togglePanel = () => {
      hideLeftPanel.value = !hideLeftPanel.value
    }

    return {
      json, filesOfFirst, filesOfSecond, selectedFileOfFirst, selectedFileOfSecond, hideLeftPanel,
      coloringFirst, coloringSecond, groupedMatches,
      selectFiles,
      selectMatch,
      togglePanel
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
}

.logo-section {
  display: flex;
  justify-content: space-between;
}

.hidden {
  display: none !important;
}

.extended {
  width: 100% !important;
}

#leftPanel {
  width: 25%;
  background: #FF5353;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  padding: 1%;
}

#rightPanel {
  width: 75%;
  background: #ECECEC;
  display: flex;
  flex-wrap: nowrap;
  justify-content: stretch;
  padding: 1%;
}

#logo {
  margin-bottom: 5%;
}

#hide-button {
  height: 20%;
  background: transparent;
  border: none;
  padding: 0;
  margin: 0;
}

#show-button {
  position: absolute;
  z-index: 1000;
  left: 0;
  background: #FF5353;
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
  width: 3%;
}

#show-button:hover img {
  display: block;
}
</style>