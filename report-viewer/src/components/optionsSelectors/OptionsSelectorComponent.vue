<!--
  Component for selecting one of multiple options.
-->
<template>
  <div class="flex flex-row text-xs h-fit items-center text-center">
    <div v-if="name != ''" class="mr-3 text-base">
      {{ name }}
    </div>
    <div v-for="[index, label] in labels.entries()" :key="index">
      <ToolTipComponent
        v-if="(label as ToolTipLabel).displayValue !== undefined"
        direction="left"
        :tooltip-text="(label as ToolTipLabel).tooltip"
      >
        <template #default>
          <OptionComponent
            :label="(label as ToolTipLabel).displayValue"
            :selected="index == selected"
            @click="select(index)"
          />
        </template>

        <template #tooltip>
          <p class="whitespace-nowrap min-h-[1.25rem] flex items-center">
            {{ (label as ToolTipLabel).tooltip }}
          </p>
        </template>
      </ToolTipComponent>
      <OptionComponent
        v-else
        :label="label as string"
        :selected="index == selected"
        @click="select(index)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRef } from 'vue'
import ToolTipComponent from '../ToolTipComponent.vue'
import OptionComponent from './OptionComponent.vue'

type ToolTipLabel = {
  displayValue: string
  tooltip: string
}

const props = defineProps({
  name: {
    type: String,
    required: false,
    default: ''
  },
  labels: {
    type: Array<string | ToolTipLabel>,
    required: true
  },
  defaultSelected: {
    type: Number,
    required: false,
    default: 0
  }
})

const emit = defineEmits(['selectionChanged'])
const selected = toRef(props.defaultSelected)

function select(index: number) {
  emit('selectionChanged', index)
  selected.value = index
}
</script>
