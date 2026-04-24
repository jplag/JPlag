<!--
  Component for selecting one of multiple options.
-->
<template>
  <div
    class="flex h-fit flex-row flex-wrap items-center gap-y-1 text-center text-xs md:flex-nowrap"
  >
    <div v-if="title != ''" class="mr-3 text-base">
      {{ title }}
    </div>
    <div v-for="[index, label] in labels.entries()" :key="index">
      <ToolTipComponent
        v-if="(label as ToolTipLabel).displayValue !== undefined"
        :direction="tooltipDirection"
        :tool-tip-container-will-be-centered="true"
        :show-info-symbol="false"
      >
        <template #default>
          <OptionComponent
            :selected="index == getSelected()"
            :has-tool-tip="true"
            @click="select(index)"
          >
            <span class="flex items-center gap-x-1 align-middle">
              <slot :name="(label as ToolTipLabel).displayValue.replace(' ', '-').toLowerCase()" />
              <span>{{ (label as ToolTipLabel).displayValue }}</span>
            </span>
          </OptionComponent>
        </template>

        <template #tooltip>
          <p class="text-sm whitespace-pre-wrap" :style="tooltipStyle">
            {{ (label as ToolTipLabel).tooltip }}
          </p>
        </template>
      </ToolTipComponent>
      <OptionComponent v-else :selected="index == getSelected()" @click="select(index)">{{
        label
      }}</OptionComponent>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, type PropType } from 'vue'
import OptionComponent from './OptionComponent.vue'
import { type ToolTipDirection, type ToolTipLabel, ToolTipComponent } from '../../base'

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
  },
  maxToolTipWidth: {
    type: Number,
    required: false,
    default: -1
  },
  tooltipDirection: {
    type: String as PropType<ToolTipDirection>,
    required: false,
    default: 'right'
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

const tooltipStyle = computed(() => {
  return props.maxToolTipWidth > 0 ? { maxWidth: `${props.maxToolTipWidth}px` } : {}
})

defineExpose({
  select
})
</script>
