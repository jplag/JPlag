<!--
  Table which contains all of the matches for a comparison with navigation links.
-->
<template>
  <div class="flex h-fit min-w-0 max-w-full flex-row space-x-1 overflow-x-hidden text-xs">
    <ToolTipComponent direction="right">
      <template #default>
        <OptionComponent label="Match Files: TokenCount" />
      </template>
      <template #tooltip>
        <p class="whitespace-pre text-sm">Click on a match to show it in the code view.</p>
      </template>
    </ToolTipComponent>

    <div
      class="print-excact flex w-full flex-row space-x-1 overflow-x-auto print:flex-wrap print:space-y-1 print:overflow-x-hidden"
    >
      <OptionComponent
        :style="{ background: match.color }"
        v-for="[index, match] in matches?.entries()"
        v-bind:key="index"
        @click="$emit('matchSelected', match)"
        :label="
          getFileName(match.firstFile) + ' - ' + getFileName(match.secondFile) + ': ' + match.tokens
        "
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Match } from '@/model/Match'
import OptionComponent from './optionsSelectors/OptionComponent.vue'
import ToolTipComponent from './ToolTipComponent.vue'

defineProps({
  /**
   * Matches of the comparison.
   * type: Array<Match>
   */
  matches: {
    type: Array<Match>
  },
  /**
   * ID of first submission
   */
  id1: {
    type: String
  },
  /**
   * ID of second submission
   */
  id2: {
    type: String
  }
})

defineEmits(['matchSelected'])

function getFileName(fullPath: string) {
  return fullPath.split(/[/\\]/g).pop() || ''
}
</script>
