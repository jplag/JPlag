<!-- 
  Base Component for DropDownSelectors 
-->
<template>
  <Interactable class="!cursor-default p-0">
    <select
      v-model="selectedOption"
      @change="$emit('selectionChanged', selectedOption)"
      class="m-0 w-full cursor-pointer bg-interactable-light dark:bg-interactable-dark"
    >
      <option v-for="option in options" :key="option" :value="option">
        {{ store().getDisplayName(option) }}
      </option>
    </select>
  </Interactable>
</template>

<script setup lang="ts">
import Interactable from '@/components/InteractableComponent.vue'
import { store } from '@/stores/store'
import { computed, ref } from 'vue'

const props = defineProps({
  options: {
    type: Array<string>,
    required: true
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
</script>
