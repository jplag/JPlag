<!--
  Base search bar component
-->
<template>
  <Interactable class="flex flex-row items-center space-x-2 px-2 py-2">
    <FontAwesomeIcon
      :icon="['fas', 'magnifying-glass']"
      class="text-gray-500"
      @click="emit('searchClicked', inputText)"
    />
    <input
      type="text"
      class="flex-auto border-0 bg-transparent outline-none placeholder:text-gray-500"
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
