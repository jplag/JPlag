<!--
  Panel which displays a submission files with its line of code.
-->
<template>
  <div
    :id="
      panelId
        ?.toString()
        .concat(filePath || '')
        .concat(fileIndex?.toString() || '-1')
    "
    class="code-panel-container"
  >
    <div class="file-title mover">
      <p style="width: 90%" @click="$emit('toggleCollapse')">
        <a class="filer-header">{{ title }}</a>
      </p>
      <button class="collapse-button" style="width: 10%" @click="$emit('toggleCollapse')">
        <img
          v-if="collapse"
          alt="hide info"
          src="../assets/keyboard_double_arrow_up_black_18dp.svg"
        />
        <img
          v-else
          alt="additional info"
          src="../assets/keyboard_double_arrow_down_black_18dp.svg"
        />
      </button>
    </div>
    <div :class="{ hidden: !collapse }">
      <div v-if="!isEmpty(lines)" class="code-container">
        <LineOfCode
          v-for="(line, index) in lines"
          :id="
            String(panelId)
              .concat(filePath || '')
              .concat((index + 1).toString())
          "
          :key="index"
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
      <div v-else class="code-container">
        <p>Empty File</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'
import { defineComponent, ref, type Ref } from 'vue'
import LineOfCode from '@/components/LineOfCode.vue'

export default defineComponent({
  name: 'CodePanel',
  components: { LineOfCode },
  props: {
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
  },
  setup(props) {
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
     * @type {Ref<UnwrapRef<{}>>}
     */
    const coloringArray: Ref<Record<number, string>> = ref({})
    const isEmpty = (lines: string[]) => {
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
     * @type {Ref<UnwrapRef<{}>>}
     */
    const linksArray: Ref<Record<number, { panel?: number; file?: string; line?: number }>> = ref(
      {}
    )
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

    /**
     * Initializing the the upper arrays.
     */
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

    return {
      coloringArray,
      linksArray,
      isFirst,
      isLast,
      isEmpty
    }
  }
})
</script>

<style scoped>
.code-panel-container {
  display: flex;
  flex-direction: column;
  margin-left: 1%;
  margin-right: 1%;
  margin-bottom: 3%;
  border-radius: 10px;
  box-shadow: var(--shadow-color) 2px 3px 3px;
  background: var(--primary-color-light);
}

.file-title {
  display: flex;
}

.file-title > p {
  text-align: center;
  color: var(--on-primary-color);
  font-weight: bold;
  font-size: large;
}

.filer-header {
  cursor: grab;
}

.code-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 1%;
  padding: 1%;
  background: var(--background-color);
  box-shadow: inset var(--shadow-color) 0 0 3px 1px;
  overflow: auto;
}

.collapse-button {
  display: flex;
  justify-content: center;
  align-items: center;
  background: transparent;
  border: none;
}

.collapse-button:hover {
  background: var(--primary-color-dark);
  border-radius: 10px;
}

.hidden {
  display: none !important;
}
</style>
