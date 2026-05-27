<!--
  Base search bar component
-->
<template>
  <Interactable class="flex flex-row items-center space-x-2 px-2 py-2">
    <FontAwesomeIcon
      :icon="faMagnifyingGlass"
      class="text-gray-500"
      @click="emit('searchClicked', value)"
    />
    <input
      v-model="value"
      type="text"
      class="flex-auto border-0 bg-transparent outline-hidden placeholder:text-gray-500"
      :placeholder="placeholder"
    />
    <InfoIcon class="ml-0!" />
  </Interactable>
</template>

<script setup lang="ts">
import Interactable from './InteractableComponent.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons'
import InfoIcon from './InfoIcon.vue'

defineProps({
  placeholder: {
    type: String,
    default: 'Search...',
    required: false
  }
})

const emit = defineEmits<{
  (e: 'inputChanged', v: string): void
  (e: 'searchClicked', v: string): void
}>()

const value = defineModel<string>({
  default: '',
  set: (v) => {
    emit('inputChanged', v)
    return v
  }
})
</script>
