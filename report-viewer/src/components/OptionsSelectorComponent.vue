<!--
  Component for selecting one of multiple options.
-->
<template>
  <div class="flex h-fit flex-row items-center text-center text-xs">
    <div v-if="name != ''" class="mr-3 text-base">
      {{ name }}
    </div>
    <Interactable
      v-for="[index, label] in labels.entries()"
      :key="label"
      class="mr-2 box-border flex h-6 w-fit items-center justify-center !rounded-2xl px-[12px] text-center hover:!border-[2px] hover:px-[11px]"
      :class="{ '!border-accent-dark !bg-accent !bg-opacity-40': index == getSelected() }"
      @click="select(index)"
    >
      {{ label }}
    </Interactable>
  </div>
</template>

<script setup lang="ts">
import Interactable from './InteractableComponent.vue'
import { ref } from 'vue'

const props = defineProps({
  name: {
    type: String,
    required: false,
    default: ''
  },
  labels: {
    type: Array<string>,
    required: true
  },
  defaultSelected: {
    type: Number,
    required: false,
    default: 0
  }
})

const emit = defineEmits(['selectionChanged'])
const selected = ref(-1)

function getSelected() {
  if (selected.value == -1) {
    return props.defaultSelected
  }
  return selected.value
}

function select(index: number) {
  emit('selectionChanged', index)
  selected.value = index
}
</script>
