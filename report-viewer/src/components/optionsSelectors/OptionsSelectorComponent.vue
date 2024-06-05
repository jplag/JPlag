<!--
  Component for selecting one of multiple options.
-->
<template>
  <div class="flex h-fit flex-row items-center text-center text-xs">
    <div v-if="title != ''" class="mr-3 text-base">
      {{ title }}
    </div>
    <div v-for="[index, label] in labels.entries()" :key="index">
      <ToolTipComponent
        v-if="(label as ToolTipLabel).displayValue !== undefined"
        direction="right"
        :tool-tip-container-will-be-centered="true"
      >
        <template #default>
          <OptionComponent
            :label="(label as ToolTipLabel).displayValue"
            :selected="index == getSelected()"
            @click="select(index)"
          />
        </template>

        <template #tooltip>
          <p class="whitespace-pre text-sm">
            {{ (label as ToolTipLabel).tooltip }}
          </p>
        </template>
      </ToolTipComponent>
      <OptionComponent
        v-else
        :label="label as string"
        :selected="index == getSelected()"
        @click="select(index)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ToolTipComponent from '../ToolTipComponent.vue'
import OptionComponent from './OptionComponent.vue'
import { type ToolTipLabel } from '@/model/ui/ToolTip'

const props = defineProps({
  title: {
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
