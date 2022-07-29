<!--
  Container containing CodePanels for all of the files in a submission.
-->
<template>
  <div class="files-container">
    <h1>Files of {{ filesOwner }}</h1>
    <VueDraggableNext>
      <CodePanel
        v-for="(file, index) in files.keys()"
        :key="file.concat(index.toString())"
        :collapse="files.get(file)?.collapsed"
        :file-index="index"
        :lines="!files.get(file)?.lines ? [] : files.get(file)?.lines"
        :matches="!matches.get(file) ? [] : matches.get(file)"
        :panel-id="containerId"
        :title="file"
        @toggle-collapse="$emit('toggle-collapse', file)"
        @line-selected="lineSelected"
      />
    </VueDraggableNext>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import CodePanel from "../components/CodePanel.vue";
import { VueDraggableNext } from "vue-draggable-next";
import { SubmissionFile } from "@/model/SubmissionFile";
import { MatchInSingleFile } from "@/model/MatchInSingleFile";

export default defineComponent({
  name: "FilesContainer",
  components: { CodePanel, VueDraggableNext },
  props: {
    /**
     * Id of the files container. We have only two so it is either 1 or 2.
     */
    containerId: {
      type: Number,
      required: true,
    },
    /**
     * Id of the submission to thich the files belong.
     */
    filesOwner: {
      type: String,
      required: true,
    },
    /**
     * Files of the submission.
     * type: Array<SubmissionFile>
     */
    files: {
      type: Map<string,SubmissionFile>,
      required: true,
    },
    /**
     * Matche of submission.
     */
    matches: {
     type: Map<string,MatchInSingleFile>,
      required: true,
    },
  },

  setup(props, { emit }) {
    /**
     * Passes lineSelected event, emitted from LineOfCode, to parent.
     * @param e
     * @param index
     * @param file
     * @param line
     */
    const lineSelected = (e: unknown, index: number, file: string, line: number) => {
      emit("lineSelected", e, index, file, line);
    };
    return {
      lineSelected,
    };
  },
});
</script>

<style scoped>
h1 {
  text-align: center;
}

.files-container {
  display: flex;
  flex-wrap: nowrap;
  flex-direction: column;
  padding-top: 1%;
  width: 100%;
  overflow: auto;
}
</style>
