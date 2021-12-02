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
  <div id="rightPanel"></div>
</div>
</template>

<script>
import { defineComponent } from "vue";
import TextInformation from "@/components/TextInformation";
import MatchList from "@/components/MatchList";

export default defineComponent({
  name: "ComparisonView",
  components: {MatchList, TextInformation},
  props: {
    jsonString: {
      type: String
    }
  },
  setup(props) {
    console.log(props.jsonString)
    const json = JSON.parse(props.jsonString)

    const groupedMatches = json.matches.reduce( (acc, val) => {
      let name = val.first_file_name + " - " + val.second_file_name
      if(!acc[name]) {
        acc[name] = []
      }
      acc[name].push(val)
      return acc;
    }, {})



    console.log(JSON.stringify(groupedMatches))

    const selectFiles = (e , s) => {
      console.log("Selected is " + s)
    }

    const selectMatch = (e, i) => {
      console.log("Index of selected match is " + i)
    }
    return {
      json, groupedMatches, selectFiles, selectMatch,
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
  flex-direction: column;
  flex-wrap: nowrap;
  padding: 1%;
}

#logo {
  width: 60%;
  height: 20%;
  margin-bottom: 5%;
}
</style>