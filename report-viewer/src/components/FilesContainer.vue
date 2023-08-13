<!--
  Container containing CodePanels for all of the files in a submission.
-->
<template>
  <Container class="flex flex-col">
    <h3 class="text-left text-lg font-bold">
      Files of
      {{ fileOwnerDisplayName }}:
    </h3>
    <ScrollableComponent class="flex-grow">
      <VueDraggableNext>
        <CodePanel
          v-for="(file, index) in files"
          :key="index"
          ref="codePanels"
          :file="file"
          :matches="
            !matches.get(file.fileName) ? [] : (matches.get(file.fileName) as MatchInSingleFile[])
          "
          :highlight-language="highlightLanguage"
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
import { ref, type PropType, type Ref } from 'vue'
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'
import type { HighlightLanguage } from '@/model/Language'

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
   * Name of the owner of the files.
   */
  fileOwnerDisplayName: {
    type: String,
    required: true
  },
  /**
   * Language of the files.
   */
  highlightLanguage: {
    type: String as PropType<HighlightLanguage>,
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
