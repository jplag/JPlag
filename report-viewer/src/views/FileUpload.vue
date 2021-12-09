<template>
<div class="container" @dragover.prevent @drop.prevent>
  <img src="@/assets/logo.png" alt="JPlag"/>
  <h1>JPlag Report Viewer</h1>
  <h2>Select an overview or comparison file to display.</h2>
  <div class="drop-container" @drop="uploadFile">
    <p> Drop a .json file here</p>
  </div>
  <label for="browse">Or browse for a .json file</label>
  <input type="file" id="browse" name="browse">
</div>
</template>

<script>
import { defineComponent } from "vue";
import router from "@/router";

export default defineComponent({
  name: "FileUpload",
  setup() {

    const navigateToOverview = (file) => {
      router.push({
          name: "OverviewV2",
          params: {str: file},
      }
      )
    }

    const navigateToComparisonView = (file) => {
      router.push({
        name: "ComparisonView",
        params: {str: file}
      })
    }

    const uploadFile = (e) => {
      let dropped = e.dataTransfer.files
      if (!dropped) return
      let files = [...dropped]
      if (files.length > 1 || files.length === 0) return
      let read = new FileReader()
      read.onload = (e) => {
        if(files[0].name.includes("comparison")) {
          navigateToComparisonView(e.target.result)
        } else {
          navigateToOverview(e.target.result)
        }
      }
      read.readAsText(files[0])
    }
    return {
      uploadFile
    }
  }
})
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, #FF5353,#ECECEC, #ECECEC);
}

.drop-container {
  background: lightgrey;
  border: dashed dodgerblue;
  align-items: center;
  justify-content: space-around;
  border-radius: 10px;
  padding: 2%;
}

.drop-container > p {
  color: dodgerblue;
  text-align: center;
}

input {
  display: none;
}

label {
  font-weight: bold;
  font-size: larger;
  background: #ECECEC;
  border-radius: 10px;
  box-shadow: #777777 2px 3px 3px;
  padding: 2%;
  margin-top: 1%;
}

</style>