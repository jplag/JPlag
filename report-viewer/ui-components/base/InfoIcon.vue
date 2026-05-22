<template>
  <span
    class="t-0 relative ml-0 hidden h-full items-center text-[0.6rem] text-slate-300 md:ml-1 md:flex dark:text-slate-600"
  >
    <FontAwesomeIcon :icon="icon" />
  </span>
</template>

<script setup lang="ts">
import { FontAwesomeIcon, FontAwesomeIconProps } from '@fortawesome/vue-fontawesome'
import { faInfoCircle } from '@fortawesome/free-solid-svg-icons'
import { PropType } from 'vue'

type IconType = FontAwesomeIconProps['icon']

defineProps({
  icon: {
    type: Object as PropType<IconType>,
    default: faInfoCircle
  }
})
</script>
