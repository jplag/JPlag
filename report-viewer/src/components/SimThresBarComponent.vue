<template>
  <Interactable class="flex flex-row items-center space-x-2 px-2 py-2">
    <FontAwesomeIcon
      :icon="['fas', 'magnifying-glass']"
      class="text-gray-500"
      @click="emit('searchClicked', parseFloat(inputText))"
    />
    <input
      type="number"
      class="flex-auto border-0 bg-transparent outline-none placeholder:text-gray-500"
      :placeholder="placeholder"
      v-model="inputText"
    />
  </Interactable>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Interactable from './InteractableComponent.vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons'

library.add(faMagnifyingGlass)

const props = defineProps({
  placeholder: {
    type: String,
    default: 'Enter similarity threshold',
    required: false
  },
  modelValue: {
    type: Number,
    default: 0,
    required: false
  }
})

const emit = defineEmits<{
  (e: 'inputChanged', v: number): void
  (e: 'searchClicked', v: number): void
  (e: 'update:modelValue', v: number): void
}>()

const inputText = computed({
  get: () => props.modelValue.toString(),
  set: (value) => {
    const numValue = parseFloat(value)
    emit('update:modelValue', numValue)
    emit('inputChanged', numValue)
  }
})
</script>
