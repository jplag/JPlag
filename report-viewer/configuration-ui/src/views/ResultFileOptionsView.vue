<template>
  <ContainerComponent>
    <div class="grid grid-cols-[auto_1fr] gap-2">
      <span>Result File Name:</span>
      <TextInput v-model="model.resultFileName" />

      <span>Shown Comparisons:</span>
      <NumberInput v-model="model.shownComparisons" />

      <span>Similarity Threshold:</span>
      <NumberInput v-model="model.similarityThreshold" />

      <label>Overwrite Existing:</label>
      <div>
        <input v-model="model.overwrite" type="checkbox"  class="accent-accent">
      </div>

      <label>CSV File:</label>
      <div>
        <input v-model="model.csvFile" type="checkbox" class="accent-accent">
      </div>
    </div>
  </ContainerComponent>
</template>

<script setup lang="ts">
import NumberInput from '../components/NumberInput.vue';
import { ContainerComponent } from '@jplag/ui-components/base';
import TextInput from '../components/TextInput.vue';
import { ResultFileOptionsState } from '../model/Steps.js';

const model = defineModel<ResultFileOptionsState>({required: true})

</script>
