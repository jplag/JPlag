<!--
  List containing the ids of all submissions.
-->
<template>
  <div class="container">
    <div class="column-list">
      <p
        v-for="(id, index) in ids"
        :key="index"
        :class="{ selected: !store().anonymous.has(id) && store().anonymous.size !== 0 }"
        @click="emitIdsSent([id])"
      >
        {{ store().submissionDisplayName(id) }}
      </p>
    </div>
    <button @click="emitIdsSent(ids)">Hide/Show all</button>
  </div>
</template>

<script setup lang="ts">
import store from '@/stores/store'

defineProps({
  ids: {
    type: Array<string>,
    required: true
  }
})

const emit = defineEmits(['idSent'])

/**
 * Emits the ids that should be anonymous
 * @param ids The ids that should be anonymous
 */
function emitIdsSent(ids: string[]) {
  emit('idSent', ids)
}
</script>

<style scoped>
button {
  background: inherit;
  font-weight: bold;
  border: none;
  border-radius: 10px;
}

button:hover {
  background: var(--primary-color-dark);
  cursor: pointer;
}

.container {
  display: flex;
  justify-content: space-between;
  background: inherit !important;
}

.column-list {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  width: 100%;
  overflow-x: auto;
}

.column-list > p {
  width: 100%;
  font-weight: bold;
  margin: 0;
  border-radius: 10px;
}

.column-list > p:hover {
  background: var(--primary-color-dark);
  cursor: pointer;
}

.selected {
  background: var(--primary-color-dark);
}
</style>
