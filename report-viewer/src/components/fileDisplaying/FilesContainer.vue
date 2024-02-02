<!--
  Container containing CodePanels for all of the files in a submission.
-->
<template>
  <Container class="flex flex-col print:!px-1">
    <div class="mb-2 mr-2 flex space-x-2">
      <h3 class="flex-grow text-left text-lg font-bold">
        Files of
        {{ fileOwnerDisplayName }}:
      </h3>
      <Button @click="collapseAll()" class="space-x-2 print:hidden"
        ><FontAwesomeIcon :icon="['fas', 'compress-alt']" />
        <p>Collapse All</p></Button
      >
    </div>

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
          @line-selected="(match) => $emit('lineSelected', match)"
          class="mt-1 first:mt-0"
        />
      </VueDraggableNext>
    </ScrollableComponent>
  </Container>
</template>

<script setup lang="ts">
import type { SubmissionFile } from '@/stores/state'
import CodePanel from './CodePanel.vue'
import Container from '../ContainerComponent.vue'
import Button from '../ButtonComponent.vue'
import ScrollableComponent from '../ScrollableComponent.vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { ref, type PropType, type Ref } from 'vue'
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faCompressAlt } from '@fortawesome/free-solid-svg-icons'
import { library } from '@fortawesome/fontawesome-svg-core'
import type { ParserLanguage } from '@/model/Language'

library.add(faCompressAlt)

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
    type: String as PropType<ParserLanguage>,
    required: true
  }
})

defineEmits(['lineSelected'])

const codePanels: Ref<(typeof CodePanel)[]> = ref([])

/**
 * Scrolls to the given file and line in the container.
 * @param file Name of the file to scroll to.
 * @param line Line to scroll to.
 */
function scrollTo(file: string, line: number) {
  const fileIndex = Array.from(props.files).findIndex((f) => f.fileName === file)
  if (fileIndex !== -1) {
    codePanels.value[fileIndex].scrollTo(line)
  }
}

/**
 * Collapses all of the code panels.
 */
function collapseAll() {
  codePanels.value.forEach((panel) => panel.collapse())
}

defineExpose({
  scrollTo
})
</script>
