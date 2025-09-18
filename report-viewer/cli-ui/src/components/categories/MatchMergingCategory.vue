<template>
  <CliViewCategory
    :class="enableMatchMerging ? 'h-full' : 'h-fit'"
    :show-content="enableMatchMerging"
  >
    <template #heading>
      <span class="flex items-center gap-2">
        <SwitchComponent v-model="enableMatchMerging" clas="col-start-1" />
        <span class="col-start-2">Match Merging</span>

        <ToolTipComponent direction="left">
          <template #tooltip>
            <p class="max-w-60 text-sm font-normal whitespace-pre-wrap">
              {{ CliToolTip.MATCH_MERGING }}
            </p>
          </template>
        </ToolTipComponent>
      </span>
    </template>

    <CliUiOption
      :error="verifyGapSize(maxGapSize, partialOptions)"
      label="Max Gap Size"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.SMM_GAP_SIZE"
    >
      <NumberInput v-model="maxGapSize" class="w-60" />
    </CliUiOption>
    <CliUiOption
      :error="verifyNeighborLength(minNeighborLength, partialOptions)"
      label="Neighbour Length"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.SMM_NEIGHBOUR_LENGTH"
    >
      <NumberInput v-model="minNeighborLength" class="w-60" />
    </CliUiOption>
    <CliUiOption
      :error="verifyMinimumRequiredMerges(minRequiedMerges)"
      label="Required Merges"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.SMM_REQUIERED_MERGES"
    >
      <NumberInput v-model="minRequiedMerges" class="w-60" />
    </CliUiOption>
  </CliViewCategory>
</template>

<script setup lang="ts">
import { verifyGapSize, verifyMinimumRequiredMerges, verifyNeighborLength } from '@/model/verifier'
import { CliToolTip } from '../../model/CliToolTip'
import CliUiOption from '../CliUiOption.vue'
import CliViewCategory from '../CliViewCategory.vue'
import SwitchComponent from '../SwitchComponent.vue'
import { computed, PropType } from 'vue'
import { CliMergingOptions } from '@jplag/model'
import NumberInput from '../NumberInput.vue'
import { MinimumTokenMatch } from '@/model/ExpandedOptions'

const props = defineProps({
  scrollOffsetY: {
    type: Number,
    required: false,
    default: 0
  },
  minimumTokenMatch: {
    type: Object as PropType<MinimumTokenMatch>,
    required: true
  }
})

const enableMatchMerging = defineModel<boolean>('enableMatchMerging', {
  default: false
})
const maxGapSize = defineModel<number>('maxGapSize', {
  default: 5
})
const minNeighborLength = defineModel<number>('minNeighborLength', {
  default: 20
})
const minRequiedMerges = defineModel<number>('minRequiedMerges', {
  default: 7
})

const partialOptions = computed(() => {
  return {
    minimumTokenMatch: props.minimumTokenMatch,
    mergingOptions: mergingOptions.value
  }
})
const mergingOptions = computed<CliMergingOptions>(() => ({
  enabled: enableMatchMerging.value,
  maximumGapSize: maxGapSize.value,
  minimumNeighborLength: minNeighborLength.value,
  minimumRequiredMerges: minRequiedMerges.value
}))
</script>
