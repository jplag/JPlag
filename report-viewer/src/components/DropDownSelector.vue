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
      <option v-for="option in options" :key="option">
        {{ option }}
      </option>
    </select>
  </Interactable>
</template>

<script setup lang="ts">
import Interactable from '@/components/InteractableComponent.vue'
import { computed } from 'vue'

const props = defineProps({
  options: {
    type: Array<string>,
    required: true
  }
})

defineEmits(['selectionChanged'])

const selectedOption = computed(() => (props.options.length > 0 ? props.options[0] : ''))
</script>
