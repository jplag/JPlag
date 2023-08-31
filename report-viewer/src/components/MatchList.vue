<!--
  Table which contains all of the matches for a comparison with navigation links.
-->
<template>
  <div class="flex h-fit min-w-0 max-w-full flex-row space-x-1 overflow-x-hidden text-xs">
    <Interactable class="my-2 flex h-6 items-center whitespace-nowrap !rounded-2xl text-center">
      Match Files: TokenCount
    </Interactable>
    <div class="flex w-full flex-row space-x-1 overflow-x-auto">
      <Interactable
        class="my-2 flex h-6 items-center whitespace-nowrap !rounded-2xl !bg-opacity-50 text-center"
        :style="{ background: match.color }"
        v-for="[index, match] in matches?.entries()"
        v-bind:key="index"
        @click="$emit('matchSelected', $event, match)"
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
