<!--
  Container containing CodePanels for all of the files in a submission.
-->
<template>
  <div class="files-container">
    <h1>Files of {{ anonymous ? filesOwnerDefault : filesOwner }}</h1>
    <VueDraggableNext handle=".mover" filter=".unmover">
      <CodePanel
        v-for="(file, index) in files.keys()"
        :key="file.concat(index.toString())"
        :collapse="files.get(file)?.collapsed"
        :file-index="index"
        :lines="files.get(file)?.lines || []"
        :matches="!matches.get(file) ? [] : matches.get(file)"
        :panel-id="containerId"
        :title="convertSubmissionIdToName(file, submissionId)"
        :filePath="file"
        @toggle-collapse="$emit('toggle-collapse', file)"
        @line-selected="lineSelected"
      />
    </VueDraggableNext>
  </div>
</template>

<script setup lang="ts">
import type { SubmissionFile } from '@/model/SubmissionFile'
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'

import CodePanel from '@/components/CodePanel.vue'
import { VueDraggableNext } from 'vue-draggable-next'
import store from '@/stores/store'

defineProps({
  /**
   * Id of the files container. We have only two so it is either 1 or 2.
   */
  containerId: {
    type: Number,
    required: true
  },
  /**
   * Id of the submission to thich the files belong.
   */
  filesOwner: {
    type: String,
    required: true
  },
  /**
   * Default value of the submission to which the files belong.
   */
  filesOwnerDefault: {
    type: String,
    required: true
  },
  /**
   * Files of the submission.
   * type: Array<SubmissionFile>
   */
  files: {
    type: Map<string, SubmissionFile>,
    required: true
  },
  /**
   * Matche of submission.
   */
  matches: {
    type: Map<string, MatchInSingleFile[]>,
    required: true
  },
  /**
   * Default value of the submission to which the files belong.
   */
  submissionId: {
    type: String,
    required: true
  },
  /**
   * The bool value of that whether id is hidden.
   */
  anonymous: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['toggle-collapse', 'lineSelected'])

/**
 * Passes lineSelected event, emitted from LineOfCode, to parent.
 * @param e event from clicking on the line
 * @param index index of the file in the files array
 * @param file path of the file
 * @param line line number of the line
 */
function lineSelected(e: unknown, index: number, file: string, line: number) {
  emit('lineSelected', e, index, file, line)
}

/**
 * converts the submissionId to the name in the path of file. If the length of path exceeds 40, then the file path displays the abbreviation.
 * @param file files path
 * @param submissionId id of submission
 * @return new path of file
 */
function convertSubmissionIdToName(file: string, submissionId: string): string {
  const displayName = store().submissionDisplayName(submissionId) || submissionId
  const filePath = file.replace(submissionId, displayName)
  const filePathLength = filePath.length
  return filePathLength > 40
    ? '..' + filePath.substring(filePathLength - 40, filePathLength)
    : filePath
}
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
