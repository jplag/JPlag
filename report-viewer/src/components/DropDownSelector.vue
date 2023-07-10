<!-- 
  Base Component for DropDownSelectors 
-->
<template>
  <Interactable class="p-0 !cursor-default">
    <select
      v-model="selectedOption"
      @change="$emit('selectionChanged', selectedOption)"
      class="bg-interactable-light dark:bg-interactable-dark w-full m-0 cursor-pointer"
    >
      <option v-for="option in options" :key="option">
        {{ option }}
      </option>
    </select>
  </Interactable>
</template>

<script setup lang="ts">
import Interactable from '@/components/InteractableComponent.vue'
import { ref } from 'vue'

const props = defineProps({
  options: {
    type: Array<string>,
    required: true
  }
})

defineEmits(['selectionChanged'])

const selectedOption = ref(props.options.length > 0 ? props.options[0] : '')
</script>
