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
               :matches="json.matches"
                @selection-changed="selectFiles"
                @match-selected="selectMatch"/>
  </div>
  <div id="rightPanel" v-bind:class="{extended : hideLeftPanel}">
    <button id="show-button" v-bind:class="{hidden : !hideLeftPanel}" @click="togglePanel"><img src="@/assets/double_arrow_white_24dp.svg" alt="hide"></button>
    <CodePanel :lines="filesOfFirst[selectedFileOfFirst]" :not-blurred="notBlurredInFirst" panel-id="1"/>
    <CodePanel :lines="filesOfSecond[selectedFileOfSecond]" :not-blurred="notBlurredInSecond" panel-id="2"/>
  </div>
</div>
</template>

<script>
import { defineComponent, ref } from "vue";
import TextInformation from "@/components/TextInformation";
import MatchList from "@/components/MatchList";
import CodePanel from "@/components/CodePanel";

export default defineComponent({
  name: "ComparisonView",
  components: {CodePanel, MatchList, TextInformation},
  props: {
    jsonString: {
      type: String
    }
  },
  setup(props) {
    const selectedFileOfFirst = ref(0)
    const selectedFileOfSecond = ref(0)
    const hideLeftPanel = ref(false)
    const notBlurredInFirst = ref([])
    const notBlurredInSecond = ref([])
    const json = JSON.parse(props.jsonString)

    const filesOfFirst = json.files_of_first_submission.reduce( (acc, val) => {
      if(!acc[val.file_name]) {
        acc[val.file_name] = []
      }
      acc[val.file_name] = val.lines
      return acc
    }, {})
    const filesOfSecond = json.files_of_second_submission.reduce( (acc, val) => {
      if(!acc[val.file_name]) {
        acc[val.file_name] = []
      }
      acc[val.file_name] = val.lines
      return acc
    }, {})

    const selectFiles = (e , file1, file2) => {
      console.log(file1, " ", file2)
      selectedFileOfFirst.value = file1
      selectedFileOfSecond.value = file2
    }

    const selectMatch = (e, s1, e1, s2, e2) => {
      let notBlurred1 = []
      let notBlurred2 = []
      for (let i = s1; i <= e1; i++) {
        notBlurred1.push(i)
      }
      notBlurredInFirst.value = notBlurred1
      for (let i = s2; i <= e2; i++) {
        notBlurred2.push(i)
      }
      notBlurredInSecond.value = notBlurred2
      document.getElementById("1".concat(notBlurred1[0])).scrollIntoView()
      document.getElementById("2".concat(notBlurred2[0])).scrollIntoView()
    }

    const togglePanel = () => {
      console.log("hiding showing panel")
      hideLeftPanel.value = !hideLeftPanel.value
    }

    return {
      json, filesOfFirst, filesOfSecond, selectedFileOfFirst, selectedFileOfSecond, hideLeftPanel,
      notBlurredInFirst, notBlurredInSecond,
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
  height: 5%;
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