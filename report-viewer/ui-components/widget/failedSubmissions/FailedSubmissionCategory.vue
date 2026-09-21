<template>
  <div>
    <div class="cursor-pointer select-none" @click="expanded = !expanded">
      <FontAwesomeIcon :icon="expanded ? faCaretDown : faCaretRight" />{{ title }} ({{
        submissionIds.length
      }})
    </div>
    <div v-if="expanded" class="pl-5">
      <div v-for="id in sortedSubmissionIds" :key="id">
        {{ id }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { faCaretDown, faCaretRight } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { SubmissionState } from '@jplag/model'
import { computed, PropType, ref } from 'vue'

const props = defineProps({
  category: {
    type: String as PropType<SubmissionState>,
    required: true
  },
  submissionIds: {
    type: Array as PropType<string[]>,
    required: true
  }
})

const expanded = ref(false)

const sortedSubmissionIds = computed(() => {
  return [...props.submissionIds].sort()
})

const title = computed(() => {
  const parts = props.category.split('_')
  return parts.map((part) => part.charAt(0).toUpperCase() + part.slice(1).toLowerCase()).join(' ')
})
</script>
