<template>
  <ToolTipComponent :direction="direction"><template #tooltip>{{ text }}</template></ToolTipComponent>
</template>

<script setup lang="ts">
import { ToolTipDirection } from '@jplag/ui-components/base';
import ToolTipComponent from '@jplag/ui-components/base/ToolTipComponent.vue';
import { PropType } from 'vue';

defineProps({
  direction: {
    type: String as PropType<ToolTipDirection>,
    required: false,
    default: 'left'
  },
  text: {
    type: String,
    required: true
  }
})
</script>