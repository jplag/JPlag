<template>
  <div class="flex gap-5">
    <ContainerComponent class="flex-1 h-fit">
      <div>
        <label class="flex cursor-pointer items-center gap-2">
          <input v-model="state.clustering.enabled" type="checkbox" class="accent-accent h-5 w-5" />
          <span class="flex flex-col">
            <span class="font-bold">Clustering</span>
            <span class="text-xs px-2 text-gray">Partion submissions into groups of familiar submissions</span>
          </span>
        </label>

      </div>
    </ContainerComponent>

    <ContainerComponent class="flex-1 h-fit">
      <div>
        <label class="flex cursor-pointer items-center gap-2">
          <input v-model="state.merging.enabled" type="checkbox" class="accent-accent h-5 w-5" />
          <span class="flex flex-col">
            <span class="font-bold">Match Merging</span>
            <span class="text-xs px-2 text-gray">Merge neighboring matches to prevent obfuscation (Can lead to more false positives)</span>
          </span>
        </label>
        <div v-if="state.merging.enabled" class="grid grid-cols-[auto_1fr] gap-2">
          <label>Minimum Neighbor Length:</label>
          <NumberInput v-model="state.merging.minimumNeighborLength" />

          <label>Maximum Gap Size:</label>
          <NumberInput v-model="state.merging.maximumGapSize" />

          <label>Minimum Merge Count:</label>
          <NumberInput v-model="state.merging.minimumRequiredMerges" />
        </div>
      </div>
    </ContainerComponent>
  </div>
</template>

<script setup lang="ts">
import NumberInput from '../components/NumberInput.vue';
import { ComparsionOptionsState } from '../model/Steps';
import { ContainerComponent } from '@jplag/ui-components/base'

const state = defineModel<ComparsionOptionsState>({required: true})
</script>
