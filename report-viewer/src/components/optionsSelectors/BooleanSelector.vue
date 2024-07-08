<template>
  <div class="w-fit">
    <ToolTipComponent v-if="(label as ToolTipLabel).displayValue !== undefined" direction="right">
      <template #default>
        <OptionComponent
          :label="(label as ToolTipLabel).displayValue"
          :selected="selected"
          @click="selected = !selected"
        />
      </template>

      <template #tooltip>
        <p class="whitespace-pre text-sm">
          {{ (label as ToolTipLabel).tooltip }}
        </p>
      </template>
    </ToolTipComponent>
    <!-- If not ignored code highlighting breaks -->
    <!-- prettier-ignore-attribute :label -->
    <OptionComponent
      v-else
      :label="(label as string)"
      :selected="selected"
      @click="selected = !selected"
    />
  </div>
</template>

<script setup lang="ts">
import OptionComponent from './OptionComponent.vue'
import { computed, ref, watch, type PropType, type Ref } from 'vue'
import { type ToolTipLabel } from '@/model/ui/ToolTip'
import ToolTipComponent from '@/components/ToolTipComponent.vue'

const props = defineProps({
  label: {
    type: [String, Object] as PropType<string | ToolTipLabel>,
    required: true
  },
  modelValue: {
    type: Boolean,
    required: false,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const _selected: Ref<boolean | undefined> = ref(undefined)

const selected = computed({
  get: () => (_selected.value === undefined ? props.modelValue : _selected.value),
  set: (value: boolean) => {
    _selected.value = value
    emit('update:modelValue', value)
  }
})

watch(
  () => props.modelValue,
  (value) => {
    _selected.value = value
  }
)
</script>
