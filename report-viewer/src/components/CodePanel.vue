<!--
  Panel which displays a submission files with its line of code.
-->
<template>
  <Interactable
    :id="
      panelId
        ?.toString()
        .concat(filePath || '')
        .concat(fileIndex?.toString() || '-1')
    "
  >
    <div @click="$emit('toggleCollapse')" class="text-center font-bold">
      {{ title }}
    </div>
    <div :class="{ hidden: !collapse }">
      <div v-if="!isEmpty(lines)" class="flex flex-col items-start w-full overflow-x-auto p-0">
        <div
          class="flex flex-row w-full"
          v-for="(line, index) in lines"
          :id="
            String(panelId)
              .concat(filePath || '')
              .concat((index + 1).toString())
          "
          :key="index"
        >
          <LineOfCode
            class="flex-grow"
            :color="coloringArray[index]"
            :is-first="isFirst[index]"
            :is-last="isLast[index]"
            :line-number="index + 1"
            :text="line"
            :visible="collapse"
            @click="
              $emit(
                'lineSelected',
                $event,
                linksArray[index].panel,
                linksArray[index].file,
                linksArray[index].line
              )
            "
          />
        </div>
      </div>
      <div v-else class="flex flex-col items-start overflow-x-auto">
        <p>Empty File</p>
      </div>
    </div>
  </Interactable>
</template>

<script setup lang="ts">
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'
import { ref, type Ref } from 'vue'
import LineOfCode from '@/components/LineOfCode.vue'
import Interactable from './InteractableComponent.vue'

const props = defineProps({
  /**
   * Path of the displayed file.
   */
  filePath: {
    type: String
  },
  /**
   * Title of the displayed file.
   */
  title: {
    type: String
  },
  /**
   * Index of file amongst other files in submission.
   */
  fileIndex: {
    type: Number
  },
  /**
   * Code lines of the file.
   * type: Array<string>
   */
  lines: {
    type: Array<string>,
    required: true
  },
  /**
   * Matches in the file
   * type: Array<MatchInSingleFile>
   */
  matches: {
    type: Array<MatchInSingleFile>
  },
  /**
   * Id of the FilesContainer. Needed for lines link generation.
   */
  panelId: {
    type: Number
  },
  /**
   * Indicates whether files is collapsed or not.
   */
  collapse: {
    type: Boolean
  }
})

defineEmits(['lineSelected', 'toggleCollapse'])

/**
 * An object containing the color of each line in code. Keys are line numbers, values are their color.
 * Example: {
 *   ...
 *   100 : "#3333"
 *   101 : "#3333"
 *   102 : "#3333"
 *   103 : "#FFFF"
 *   ...
 * }
 */
const coloringArray: Ref<Record<number, string>> = ref({})

/**
 * @param lines Array of lines to check.
 * @returns true if all lines are empty, false otherwise.
 */
function isEmpty(lines: string[]) {
  return lines.length === 0 || lines.every((line) => !line.trim())
}

/**
 * An object containing an object from which an id is to of the line to which this is linked is constructed.
 * Id object contains panel, file name, first line number of linked matched.
 * Example: {
 *   panel: 1,
 *   file: "Example.java",
 *   line: 121
 * }
 * Constructed ID (generateLineCodeLink from Utils.ts): 1Example.java121
 * When a line is clicked it uses this link id
 * to scroll into vie the linked line in the linked file of the other submission.
 * Key is line number, value is id of linked line.
 */
const linksArray: Ref<Record<number, { panel?: number; file?: string; line?: number }>> = ref({})

/**
 * Indicates whether the line is last line of match. Key is line number, value is true or false.
 * @type {Ref<UnwrapRef<{}>>}
 */
const isLast: Ref<Record<number, boolean>> = ref({})

/**
 * Indicates whether the line is the first line of a match. Key is line number, value is true or false.
 * @type {Ref<UnwrapRef<{}>>}
 */
const isFirst: Ref<Record<number, boolean>> = ref({})

// Initializing the the upper arrays.
props.matches?.forEach((m) => {
  for (let i = m.start; i <= m.end; i++) {
    //assign match color to line
    coloringArray.value[i - 1] = m.color
    //assign link object to line.
    linksArray.value[i - 1] = {
      panel: m.linked_panel,
      file: m.linked_file,
      line: m.linked_line
    }
  }
  isFirst.value[m.start - 1] = true
  isLast.value[m.end - 1] = true
})

//assign default values for all line which are not contained in matches
for (let i = 0; i < props.lines.length; i++) {
  if (!coloringArray.value[i]) {
    coloringArray.value[i] = '#FFFFFF'
    linksArray.value[i] = {}
    isFirst.value[i] = false
    isLast.value[i] = false
  }
}
</script>
