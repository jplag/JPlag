<!--
  Container containing CodePanels for all of the files in a submission.
-->
<template>
  <Container class="flex flex-col">
    <h3 class="text-left text-lg font-bold">
      Files of {{ anonymous ? filesOwnerDefault : filesOwner }}:
    </h3>
    <ScrollableComponent class="flex-grow">
      <VueDraggableNext>
        <CodePanel
          v-for="(file, index) in files.keys()"
          :key="file.concat(index.toString())"
          ref="codePanels"
          :collapse="files.get(file)?.collapsed"
          :file-index="index"
          :lines="files.get(file)?.lines || []"
          :matches="!matches.get(file) ? [] : matches.get(file)"
          :title="convertSubmissionIdToName(file, submissionId)"
          :filePath="file"
          @line-selected="(match) => $emit('line-selected', match)"
          class="mt-1"
        />
      </VueDraggableNext>
    </ScrollableComponent>
  </Container>
</template>

<script setup lang="ts">
import type { SubmissionFile } from '@/model/SubmissionFile'
import CodePanel from '@/components/CodePanel.vue'
import store from '@/stores/store'
import Container from './ContainerComponent.vue'
import ScrollableComponent from './ScrollableComponent.vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { ref } from 'vue'
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'

const props = defineProps({
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

defineEmits(['lineSelected'])

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

const codePanels = ref([])

function scrollTo(file: string, line: number) {
  console.log('scrolling to', file)
  const fileIndex = Array.from(props.files.keys()).indexOf(file)
  console.log('fileIndex', fileIndex)
  ;(codePanels.value[fileIndex] as unknown as typeof CodePanel).scrollTo(line)
}

defineExpose({
  scrollTo
})
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
}
</style>
