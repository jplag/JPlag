<template>
  <CliViewCategory>
    <template #heading> Comparison Options </template>

    <CliUiOption label="Language" :scroll-offset-y="scrollOffsetY" :tooltip="CliToolTip.LANGUAGE">
      <DropDownSelector v-model="language" :options="languageList" class="w-60" />
    </CliUiOption>
    <CliUiOption
      label="Minimum Token Match"
      :error="verifyMinTokenMatch(minTokenMatch)"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.MIN_TOKENS"
    >
      <InputWrapper v-model="_minTokenMatch" class="w-60" />
    </CliUiOption>
    <CliUiOption label="Normalize" :scroll-offset-y="scrollOffsetY" :tooltip="CliToolTip.NORMALIZE">
      <SwitchComponent v-model="doNormalization" />
    </CliUiOption>
  </CliViewCategory>
</template>
<script setup lang="ts">
import { CliToolTip } from '../../model/CliToolTip'
import SwitchComponent from '../SwitchComponent.vue'
import { DropDownSelector } from '@jplag/ui-components/base'
import CliUiOption from '../CliUiOption.vue'
import { ParserLanguage } from '@jplag/model'
import { languageList } from '@/model/languageList'
import { verifyMinTokenMatch } from '@/model/verifier'
import CliViewCategory from '../CliViewCategory.vue'
import { MinimumTokenMatch } from '@/model/ExpandedOptions'
import InputWrapper from '../InputWrapper.vue'
import { computed } from 'vue'

defineProps({
  scrollOffsetY: {
    type: Number,
    required: false,
    default: 0
  }
})

const language = defineModel<ParserLanguage>('language', {
  default: ParserLanguage.JAVA
})
const _minTokenMatch = computed({
  get: () => {
    if (minTokenMatch.value === 'default') {
      return 'default'
    }
    return minTokenMatch.value.toString()
  },
  set: () => {
    if (_minTokenMatch.value === 'default') {
      minTokenMatch.value = 'default'
    } else {
      const parsed = parseInt(_minTokenMatch.value)
      minTokenMatch.value = parsed
    }
  }
})
const minTokenMatch = defineModel<MinimumTokenMatch>('minTokenMatch', {
  default: 'default'
})
const doNormalization = defineModel<boolean>('doNormalization', {
  default: true
})
</script>
