<template>
  <ContainerComponent class="flex flex-col overflow-hidden p-0!">
    <div class="bg-container-secondary-light dark:bg-container-secondary-dark flex w-full">
      <div
        v-for="index in Array(props.tabs.length).keys()"
        :key="index"
        class="border-container-border-light dark:border-container-border-dark cursor-pointer border-r"
        :class="
          tabNames[index] == selectedTab
            ? 'bg-container-light dark:bg-container-dark border-b-0'
            : 'bg-container-secondary-light dark:bg-container-secondary-dark border-b'
        "
        @click="selectedTab = tabNames[index]"
      >
        <ToolTipComponent
          v-if="toolTips[index]"
          :direction="index < firstBottomTooltipIndex ? 'right' : 'bottom'"
          :show-info-symbol="false"
        >
          <template #default>
            <span class="flex items-center p-2 px-5">
              <p>{{ tabNames[index] }}</p>
              <InfoIcon />
            </span>
          </template>
          <template #tooltip>
            <p class="text-sm whitespace-pre">{{ toolTips[index] }}</p>
          </template>
        </ToolTipComponent>
        <p v-else class="p-2 px-5">{{ tabNames[index] }}</p>
      </div>
      <div
        class="border-container-border-light dark:border-container-border-dark flex-1 border-b"
      ></div>
    </div>
    <div class="flex flex-1 flex-col overflow-hidden p-2">
      <slot :name="selectedTab.replace(/\s/g, '-')"></slot>
    </div>
  </ContainerComponent>
</template>

<script setup lang="ts">
import { computed, ref, type Ref } from 'vue'
import ContainerComponent from './ContainerComponent.vue'
import type { ToolTipLabel } from './ToolTip'
import ToolTipComponent from './ToolTipComponent.vue'
import InfoIcon from './InfoIcon.vue'

const props = defineProps({
  tabs: {
    type: Array<string | ToolTipLabel>,
    required: true
  },
  firstBottomTooltipIndex: {
    type: Number,
    required: false,
    default: 2
  }
})

const tabNames = computed(() =>
  props.tabs.map((tab) => (typeof tab === 'string' ? tab : tab.displayValue))
)
const toolTips = computed(() =>
  props.tabs.map((tab) => (typeof tab === 'string' ? null : tab.tooltip))
)

const _selectedTab: Ref<string | null> = ref(null)
const selectedTab = computed({
  set(value: string) {
    _selectedTab.value = value
  },
  get() {
    if (!_selectedTab.value) {
      return tabNames.value[0]
    }
    return _selectedTab.value
  }
})
</script>
