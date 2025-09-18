<template>
  <CliViewCategory>
    <template #heading> Report Options </template>

    <CliUiOption
      label="Result File Name"
      :error="verifyResultFileName(resultFileName)"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.RESULT_FILE"
    >
      <InputWrapper v-model="resultFileName" class="w-60" />
    </CliUiOption>
    <CliUiOption
      :error="verifyShownComparisons(shownComparisons)"
      label="Shown Comparisons"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.SHOWN_COMPARISONS"
    >
      <NumberInput v-model="shownComparisons" class="w-60" />
    </CliUiOption>
    <CliUiOption
      :error="verifySimilarityyThreshold(similarityThreshold)"
      label="Similarity Treshold"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.SIMILARITY_TRESHOLD"
    >
      <NumberInput v-model="similarityThreshold" class="w-60" />
    </CliUiOption>
    <CliUiOption
      label="Overwrite Existing"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.OVERWRITE"
    >
      <SwitchComponent v-model="overwriteResultFile" />
    </CliUiOption>
    <CliUiOption label="CSV File" :scroll-offset-y="scrollOffsetY" :tooltip="CliToolTip.CSV">
      <SwitchComponent v-model="generateCsvFile" />
    </CliUiOption>
  </CliViewCategory>
</template>

<script setup lang="ts">
import { CliToolTip } from '../../model/CliToolTip'
import SwitchComponent from '../SwitchComponent.vue'
import CliUiOption from '../CliUiOption.vue'
import InputWrapper from '../InputWrapper.vue'
import NumberInput from '../NumberInput.vue'
import CliViewCategory from '../CliViewCategory.vue'
import {
  verifyResultFileName,
  verifyShownComparisons,
  verifySimilarityyThreshold
} from '@/model/verifier'

defineProps({
  scrollOffsetY: {
    type: Number,
    required: false,
    default: 0
  }
})

const resultFileName = defineModel<string>('resultFileName', {
  default: 'results.jplag'
})
const shownComparisons = defineModel<number>('shownComparisons', {
  default: 2500
})
const similarityThreshold = defineModel<number>('similarityThreshold', {
  default: 0
})
const overwriteResultFile = defineModel<boolean>('overwriteResultFile', {
  default: false
})
const generateCsvFile = defineModel<boolean>('generateCsvFile', {
  default: false
})
</script>
