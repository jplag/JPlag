<template>
  <CliViewCategory :show-content="showAdvancedOptions">
    <template #heading>
      <span class="cursor-pointer" @click="showAdvancedOptions = !showAdvancedOptions">
        <div class="flex items-center gap-x-2">
          <FontAwesomeIcon :icon="showAdvancedOptions ? faCaretDown : faCaretRight" />
          Advanced Options
        </div>
      </span>
    </template>

    <CliUiOption
      label="Exlcusion File"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.EXCLUSION_FILE"
    ></CliUiOption>
    <CliUiOption label="File Extensions"></CliUiOption>
    <CliUiOption
      label="Subdirectory"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.SUBDIRECTORY"
    ></CliUiOption>
    <CliUiOption label="Debug" :scroll-offset-y="scrollOffsetY" :tooltip="CliToolTip.DEBUG">
      <SwitchComponent v-model="doDebug" />
    </CliUiOption>
    <CliUiOption label="Log Level" :scroll-offset-y="scrollOffsetY" :tooltip="CliToolTip.LOG_LEVEL">
      <OptionsSelectorComponent
        :default-selected="2"
        :labels="LogLevelList"
        @selection-changed="(i) => (logLevel = LogLevelList[i])"
      />
    </CliUiOption>
  </CliViewCategory>
</template>

<script setup lang="ts">
import { CliToolTip } from '../../model/CliToolTip'
import CliUiOption from '../CliUiOption.vue'
import CliViewCategory from '../CliViewCategory.vue'
import SwitchComponent from '../SwitchComponent.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faCaretDown, faCaretRight } from '@fortawesome/free-solid-svg-icons'
import { OptionsSelectorComponent } from '@jplag/ui-components/widget'
import { ref } from 'vue'

defineProps({
  scrollOffsetY: {
    type: Number,
    required: false,
    default: 0
  }
})

const showAdvancedOptions = ref(false)
const doDebug = defineModel<boolean>('doDebug', {
  default: false
})
const logLevel = defineModel<string>('logLevel', {
  default: 'INFO'
})

const LogLevelList = ['ERROR', 'WARN', 'INFO', 'DEBUG', 'TRACE']
</script>
