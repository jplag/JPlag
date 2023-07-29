<!--
  Container containing CodePanels for all of the files in a submission.
-->
<template>
  <Container class="flex flex-col">
    <h3 class="text-left text-lg font-bold">
      Files of
      {{ anonymous ? anonymousFilesOwnerDefault : store().submissionDisplayName(submissionId) }}:
    </h3>
    <ScrollableComponent class="flex-grow">
      <VueDraggableNext>
        <CodePanel
          v-for="(file, index) in files"
          :key="index"
          ref="codePanels"
          :file="file"
          :matches="!matches.get(file.fileName) ? [] : matches.get(file.fileName)"
          @line-selected="(match) => $emit('line-selected', match)"
          class="mt-1"
        />
      </VueDraggableNext>
    </ScrollableComponent>
  </Container>
</template>

<script setup lang="ts">
import type { SubmissionFile } from '@/stores/state'
import CodePanel from '@/components/CodePanel.vue'
import Container from './ContainerComponent.vue'
import ScrollableComponent from './ScrollableComponent.vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { ref, type Ref } from 'vue'
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'
import store from '@/stores/store'

const props = defineProps({
  /**
   * Files of the submission.
   */
  files: {
    type: Array<SubmissionFile>,
    required: true
  },
  /**
   * Matches of submission.
   */
  matches: {
    type: Map<string, MatchInSingleFile[]>,
    required: true
  },
  /**
   * Submission id of the files.
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
  },
  /**
   * The default value of the owner of the files.
   */
  anonymousFilesOwnerDefault: {
    type: String,
    required: true
  }
})

defineEmits(['lineSelected'])

const codePanels: Ref<(typeof CodePanel)[]> = ref([])

function scrollTo(file: string, line: number) {
  console.log('scrolling to', file)
  const fileIndex = Array.from(props.files).findIndex((f) => f.fileName === file)
  if (fileIndex !== -1) {
    codePanels.value[fileIndex].scrollTo(line)
  }
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
