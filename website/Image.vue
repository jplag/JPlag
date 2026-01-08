<template>
  <img :src="source" />
</template>

<script setup lang="ts">
import { useData } from 'vitepress'; 
import { computed } from 'vue';

const props = defineProps({
  srcLight: {
    type: String,
    required: true
  },
  srcDark: {
    type: String,
    required: false
  }
})

const isDark = useData().isDark;

const source = computed(() => {
  if (isDark.value && props.srcDark) {
    return props.srcDark;
  }
  return props.srcLight;
})
</script>