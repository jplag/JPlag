<template>
  <Interactable class="flex flex-row items-center space-x-2 px-2 py-2">
    <FontAwesomeIcon
      :icon="['fas', 'magnifying-glass']"
      class="text-gray-500"
      @click="handleSearchClick"
    />
    <input
      type="number"
      class="flex-auto border-0 bg-transparent outline-none placeholder:text-gray-500"
      :placeholder="placeholder"
      v-model="inputText"
      @input="handleInputChange"
    />
  </Interactable>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Interactable from './InteractableComponent.vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons'

library.add(faMagnifyingGlass)

const emit = defineEmits(['update:similarityThreshold'])

const inputText = ref('')

function handleInputChange() {
  const numValue = parseFloat(inputText.value) || 0
  emit('update:similarityThreshold', numValue)
}

// Define the handleSearchClick function to emit the current value
function handleSearchClick() {
  const numValue = parseFloat(inputText.value) || 0
  emit('update:similarityThreshold', numValue)
}
</script>
