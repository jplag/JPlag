<!--
  Table which contains all of the matches for a comparison with navigation links.
-->
<template>
  <div class="flex flex-row overflow-x-hidden max-w-full min-w-0 space-x-1 text-xs h-fit">
    <Interactable class="!rounded-2xl whitespace-nowrap flex items-center text-center h-6 my-2">
      Match Files: TokenCount
    </Interactable>
    <div class="w-full flex flex-row space-x-1 overflow-x-auto">
      <Interactable
        class="!rounded-2xl !bg-opacity-50 whitespace-nowrap flex items-center text-center h-6 my-2"
        :style="{ background: match.color }"
        v-for="[index, match] in matches?.entries()"
        v-bind:key="index"
        @click="$emit('matchSelected', match)"
      >
        {{ getFileName(match.firstFile) }} - {{ getFileName(match.secondFile) }}: {{ match.tokens }}
      </Interactable>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Match } from '@/model/Match'
import Interactable from './InteractableComponent.vue'

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
