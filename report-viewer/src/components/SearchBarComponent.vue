<!--
  Base search bar component
-->
<template>
  <Interactable class="flex flex-row space-x-2 items-center py-2 px-2">
    <FontAwesomeIcon
      :icon="['fas', 'magnifying-glass']"
      class="text-gray-500"
      @click="emit('searchClicked', inputText)"
    />
    <input
      type="text"
      class="placeholder:text-gray-500 bg-transparent border-0 flex-auto outline-none"
      :placeholder="placeholder"
      v-model="inputText"
    />
  </Interactable>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import Interactable from './InteractableComponent.vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons'

library.add(faMagnifyingGlass)

defineProps({
  placeholder: {
    type: String,
    default: 'Search...',
    required: false
  }
})

const emit = defineEmits(['inputChanged', 'searchClicked'])

const inputText = ref('')

watch(inputText, (newVal) => {
  emit('inputChanged', newVal)
})
</script>
