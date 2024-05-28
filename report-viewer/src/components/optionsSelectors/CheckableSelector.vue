<template>
  <div class="flex h-fit flex-row items-center text-center text-xs">
    <div v-if="title != ''" class="mr-3 text-base">
      {{ title }}
    </div>
    <div>
      <ToolTipComponent
        v-if="(option as ToolTipLabel).displayValue !== undefined"
        direction="right"
        :tool-tip-container-will-be-centered="true"
      >
        <template #default>
          <OptionComponent
            :label="(option as ToolTipLabel).displayValue"
            :selected="value"
            @click="value = !value"
          />
        </template>

        <template #tooltip>
          <p class="whitespace-pre text-sm">
            {{ (option as ToolTipLabel).tooltip }}
          </p>
        </template>
      </ToolTipComponent>
      <OptionComponent v-else :label="option as string" :selected="value" @click="value = !value" />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ToolTipLabel } from '@/model/ui/ToolTip'
import { computed, ref, type PropType, type Ref, watch } from 'vue'
import ToolTipComponent from '@/components/ToolTipComponent.vue'
import OptionComponent from './OptionComponent.vue'

const props = defineProps({
  title: {
    type: String,
    required: false,
    default: ''
  },
  option: {
    type: Object as PropType<string | ToolTipLabel>,
    required: true
  },
  modelValue: {
    type: Boolean,
    required: false,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const _value: Ref<boolean | undefined> = ref(undefined)
const value = computed({
  get: () => _value.value ?? props.modelValue,
  set: (val: boolean) => {
    _value.value = val
    emit('update:modelValue', val)
  }
})

watch(
  () => props.modelValue,
  (val) => {
    _value.value = val
  }
)
</script>
