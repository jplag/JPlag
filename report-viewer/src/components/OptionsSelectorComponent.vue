<template>
  <div class="flex flex-row text-xs h-fit items-center text-center">
    <div v-if="name != ''" class="mr-3 text-base">
      {{ name }}
    </div>
    <Interactable
      v-for="[index, label] in labels.entries()"
      :key="label"
      class="mr-2 px-[12px] w-fit !rounded-2xl flex justify-center items-center text-center box-border h-6 hover:!border-[2px] hover:px-[11px]"
      :class="{ '!bg-accent !border-accent-dark !bg-opacity-40': index == selected }"
      @click="select(index)"
    >
      {{ label }}
    </Interactable>
  </div>
</template>

<script setup lang="ts">
import Interactable from './InteractableComponent.vue'
import { toRef } from 'vue'

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

const emit = defineEmits<{ (e: 'selectionChanged', index: number): any }>()
const selected = toRef(props.defaultSelected)

function select(index: number) {
  emit('selectionChanged', index)
  selected.value = index
}
</script>
