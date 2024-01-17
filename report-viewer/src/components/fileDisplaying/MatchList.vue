<!--
  Table which contains all of the matches for a comparison with navigation links.
-->
<template>
  <div
    class="flex h-fit min-w-0 max-w-full flex-row space-x-1 overflow-x-hidden text-xs print:hidden"
  >
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
        v-for="[index, match] in matches?.entries()"
        :style="{ background: getMatchColor(0.3, match.colorIndex) }"
        v-bind:key="index"
        @click="$emit('matchSelected', match)"
        :label="
          getFileName(match.firstFile) + ' - ' + getFileName(match.secondFile) + ': ' + match.tokens
        "
      />
    </div>
  </div>

  <div class="hidden print:block">
    <table aria-describedby="List of matches for printing">
      <tr>
        <th class="px-2 text-left">File of {{ id1 }}</th>
        <th class="px-2 text-left">Starting Line - End Line</th>
        <th class="px-2 text-left">File of {{ id2 }}</th>
        <th class="px-2 text-left">Starting Line - End Line</th>
        <th class="px-2 text-left">Token Count</th>
      </tr>
      <tr
        v-for="[index, match] in matches?.entries()"
        v-bind:key="index"
        :style="{ background: getMatchColor(0.3, match.colorIndex) }"
        class="print-excact"
      >
        <td class="px-2">{{ getFileName(match.firstFile) }}</td>
        <td class="px-2">{{ match.startInFirst }} - {{ match.endInFirst }}</td>
        <td class="px-2">{{ getFileName(match.secondFile) }}</td>
        <td class="px-2">{{ match.startInSecond }} - {{ match.endInSecond }}</td>
        <td class="px-2">{{ match.tokens }}</td>
      </tr>
    </table>
  </div>
</template>

<script setup lang="ts">
import type { Match } from '@/model/Match'
import OptionComponent from '../optionsSelectors/OptionComponent.vue'
import ToolTipComponent from '@/components/ToolTipComponent.vue'
import { getMatchColor } from '@/utils/ColorUtils'

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

<style scoped>
@media screen {
}
</style>
