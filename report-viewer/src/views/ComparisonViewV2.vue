<template>
  <div class="container">
    <div class="left-drawers">
      <div id="basicInfo" class="drawer">
        <p class="drawer-title">Basic Info</p>
      </div>
      <div id="matches" class="drawer">
        <p class="drawer-title">Matches</p>
      </div>
    </div>
    <CodePanel :lines="filesOfFirst[selectedFileOfFirst]" :not-blurred="[]" panel-id="1"/>
    <CodePanel :lines="filesOfSecond[selectedFileOfSecond]" :not-blurred="[]" panel-id="2"/>
  </div>
</template>

<script>
import {defineComponent, ref} from "vue";
import CodePanel from "../components/CodePanel";
import {convertToFilesByName} from "@/utils/Utils";

export default defineComponent({
  name: "ComparisonViewV2",
  components: { CodePanel },
  props: {
    jsonString: {
      type: String
    }
  },
  setup(props) {
    const json = JSON.parse(props.jsonString)
    const selectedFileOfFirst = ref("Jumpbox12.java")
    const selectedFileOfSecond = ref("Jumpbox21.java")

    const filesOfFirst = convertToFilesByName(json.files_of_first_submission)
    const filesOfSecond = convertToFilesByName(json.files_of_second_submission)

    const generateColour = () => {
      let color = "#";
      for (let i = 0; i < 3; i++)
        color += ("0" + Math.floor(((1 + Math.random()) * Math.pow(16, 2)) / 2).toString(16)).slice(-2);
      return color;
    }

    const groupedMatches = ref(json.matches.reduce( (acc, val) => {
          let name = val.first_file_name
          let subname = val.second_file_name
          if(!acc[name]) {
            acc[name] = {}
          }
          if(!acc[name][subname]) {
            acc[name][subname] = []
          }
          let newVal = {...val, color: generateColour()}
          acc[name][subname].push(newVal)
          return acc;
        }, {})
    )

    return {
      selectedFileOfFirst,
      selectedFileOfSecond,
      filesOfFirst,
      filesOfSecond
    }
  }
})
</script>

<style scoped>
.container {
  display: flex;
  flex-wrap: nowrap;
  align-items: stretch;
  background: #ECECEC;
  height: 100%;
}

.left-drawers {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  align-items: stretch;
  height: 100%;
}

.drawer {
  display: flex;
  flex-wrap: nowrap;
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
  padding-right: 5%;
}

.drawer-title {
  writing-mode: vertical-lr;
  text-align: center;
  color: white;
  padding: 0;
  margin: 0;
}

#matches {
  background: #777777;
  height: 50%;
}

#basicInfo {
  background: #FF5353;
  height: 50%;
}

#basicInfo:hover {
  position: absolute;
  left: 0;
  z-index: 1000;
}
</style>