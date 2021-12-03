<template>
<div class="container">
  <div id="leftPanel">
    <img id="logo" src="@/assets/logo.png" alt="JPlag">
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
  <div id="rightPanel">
    <CodePanel :lines="filesOfFirst[selectedFileOfFirst]" :not-blurred="[]"/>
    <CodePanel :lines="filesOfSecond[selectedFileOfSecond]" :not-blurred="[]"/>
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
    const selectedFileOfFirst = ref([])
    const selectedFileOfSecond = ref([])
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

    const groupedMatches = json.matches.reduce( (acc, val) => {
      let name = val.first_file_name + " - " + val.second_file_name
      if(!acc[name]) {
        acc[name] = []
      }
      acc[name].push(val)
      return acc;
    }, {})


    const selectFiles = (e , s) => {
      let split = s.split(" - ")
      selectedFileOfFirst.value = split[0]
      selectedFileOfSecond.value = split[1]
    }

    const selectMatch = (e, i) => {
      console.log(e, i)
    }
    return {
      json, groupedMatches, filesOfFirst, filesOfSecond, selectedFileOfFirst, selectedFileOfSecond,
      selectFiles,
      selectMatch,
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

#leftPanel {
  width: 20%;
  background: #FF5353;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  padding: 1%;
}

#rightPanel {
  width: 80%;
  background: #ECECEC;
  display: flex;
  flex-wrap: nowrap;
  justify-content: stretch;
  padding: 1%;
}

#logo {
  width: 60%;
  height: 20%;
  margin-bottom: 5%;
}
</style>