<!-- 
  Base Component for DropDownSelectors 
-->
<template>
  <Interactable class="cursor-default! px-2! py-0!">
    <select
      v-model="selectedOption"
      class="bg-interactable-light dark:bg-interactable-dark m-0 w-full cursor-pointer"
      @change="$emit('selectionChanged', selectedOption)"
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
