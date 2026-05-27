<!-- 
  Base Component for DropDownSelectors 
-->
<template>
  <Interactable class="cursor-default! p-0">
    <select
      v-model="selectedOption"
      class="bg-interactable-light dark:bg-interactable-dark m-0 w-full cursor-pointer"
      @change="$emit('selectionChanged', selectedOption)"
    >
      <option v-for="option in options" :key="option" :value="option">
        {{ getDisplayName(option) }}
      </option>
    </select>
  </Interactable>
</template>

<script setup lang="ts">
import Interactable from './InteractableComponent.vue'
import { computed, PropType, ref, watch } from 'vue'

const props = defineProps({
  options: {
    type: Array<string>,
    required: true
  },
  getDisplayName: {
    type: Function as PropType<(option: string) => string>,
    default: (option: string) => option
  }
})

defineEmits(['selectionChanged'])

const _selectedOption = ref('')

const selectedOption = computed({
  get: () => {
    if (_selectedOption.value === '') {
      return props.options[0]
    }
    return _selectedOption.value
  },
  set: (value) => (_selectedOption.value = value)
})

watch(
  () => props.getDisplayName,
  () => {
    // This is enough to trigger a reload
  },
  { immediate: true }
)
</script>
