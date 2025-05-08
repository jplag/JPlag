<!--
  Table which contains all of the matches for a comparison with navigation links.
-->
<template>
  <div
    class="flex h-fit max-w-full min-w-0 flex-row flex-wrap space-x-1 gap-y-1 overflow-x-hidden text-xs md:flex-nowrap print:hidden"
  >
    <ToolTipComponent direction="right">
      <template #default>
        <OptionComponent label="Matches:" />
      </template>
      <template #tooltip>
        <p class="text-sm whitespace-pre">Click on a match to show it in the code view.</p>
      </template>
    </ToolTipComponent>

    <ToolTipComponent v-if="hasBaseCode" direction="right" class="pr-3">
      <template #default>
        <OptionComponent label="Base Code" :style="{ background: getMatchColor(0.3, 'base') }" />
      </template>
      <template #tooltip>
        <div class="text-sm whitespace-pre">
          Sections that are likely base code (thus ignored in similarity calculation). <br />
          <p>
            {{ store().getDisplayName(id1) }}:

            <span v-if="basecodeInFirst.length > 0">
              {{ basecodeInFirst.map((b) => b.match.tokens).reduce((a, b) => a + b, 0) }} Tokens,
              Lines: {{ store().getDisplayName(id1 ?? '') }}: Lines
              {{ basecodeInFirst.map((b) => `${b.start}-${b.end}`).join(',') }}
            </span>
            <span v-else>No Basecode in Submission</span>
          </p>
          <p>
            {{ store().getDisplayName(id2) }}:
            <span v-if="basecodeInSecond.length > 0">
              {{ basecodeInSecond.map((b) => b.match.tokens).reduce((a, b) => a + b, 0) }} Tokens,
              Lines: {{ store().getDisplayName(id2 ?? '') }}: Lines
              {{ basecodeInSecond.map((b) => `${b.start}-${b.end}`).join(',') }}
            </span>
            <span v-else>No Basecode in Submission</span>
          </p>
        </div>
      </template>
    </ToolTipComponent>

    <div
      ref="scrollableList"
      class="print-exact flex w-full flex-row space-x-1 overflow-x-auto print:flex-wrap print:space-y-1 print:overflow-x-hidden"
      @scroll="updateScrollOffset()"
    >
      <ToolTipComponent
        v-for="[index, match] in matches?.entries()"
        :key="index"
        :direction="getTooltipDirection(index)"
        :scroll-offset-x="scrollOffsetX"
      >
        <template #default>
          <OptionComponent
            :style="{ background: getMatchColor(0.3, match.colorIndex) }"
            :label="
              getFileName(match.firstFileName) +
              ' - ' +
              getFileName(match.secondFileName) +
              ': ' +
              match.tokens
            "
            @click="$emit('matchSelected', match)"
          />
        </template>
        <template #tooltip>
          <p class="text-sm whitespace-pre">
            Match between {{ getFileName(match.firstFileName) }} (Line
            {{ match.startInFirst.line }}-{{ match.endInFirst.line }}) and
            {{ getFileName(match.secondFileName) }} (Line {{ match.startInSecond.line }}-{{
              match.endInSecond.line
            }}) <br />
            Match is {{ match.tokens }} tokens long. <br />
            <span v-if="showTokenRanges(match)">
              Token indices of match: {{ match.startInFirst.tokenListIndex }}-{{
                match.endInFirst.tokenListIndex
              }}
              and {{ match.startInSecond.tokenListIndex }}-{{ match.endInSecond.tokenListIndex }}.
              <br />
            </span>

            Click to show in code view.
          </p>
        </template>
      </ToolTipComponent>
    </div>
  </div>

  <div class="hidden print:block">
    <table aria-describedby="List of matches for printing">
      <tr>
        <th class="px-2 text-left">File of {{ id1 }}</th>
        <th class="px-2 text-left">Starting Line - End Line</th>
        <th class="px-2 text-left">File of {{ id2 }}</th>
        <th class="px-2 text-left">Starting Line - End Line</th>
        <th class="px-2 text-left">Token Count</th>
      </tr>
      <tr
        v-for="[index, match] in matches?.entries()"
        :key="index"
        :style="{ background: getMatchColor(0.3, match.colorIndex) }"
        class="print-exact"
      >
        <td class="px-2">{{ getFileName(match.firstFileName) }}</td>
        <td class="px-2">{{ match.startInFirst }} - {{ match.endInFirst }}</td>
        <td class="px-2">{{ getFileName(match.secondFileName) }}</td>
        <td class="px-2">{{ match.startInSecond }} - {{ match.endInSecond }}</td>
        <td class="px-2">{{ match.tokens }}</td>
      </tr>
      <tr
        v-if="hasBaseCode"
        :style="{ background: getMatchColor(0.3, 'base') }"
        class="print-exact"
      >
        <td class="px-2" colspan="5">Basecode in submissions</td>
      </tr>
    </table>
  </div>
</template>

<script setup lang="ts">
import type { Match } from '@/model/Match'
import OptionComponent from '../optionsSelectors/OptionComponent.vue'
import ToolTipComponent from '@/components/ToolTipComponent.vue'
import { getMatchColor } from '@/utils/ColorUtils'
import type { ToolTipDirection } from '@/model/ui/ToolTip'
import { ref, type Ref, computed } from 'vue'
import type { BaseCodeMatch } from '@/model/BaseCodeReport'
import { store } from '@/stores/store'

const props = defineProps({
  /**
   * Matches of the comparison.
   * type: Array<Match>
   */
  matches: {
    type: Array<Match>,
    required: true
  },
  /**
   * ID of first submission
   */
  id1: {
    type: String,
    required: true
  },
  /**
   * ID of second submission
   */
  id2: {
    type: String,
    required: true
  },
  basecodeInFirst: {
    type: Array<BaseCodeMatch>,
    required: false,
    default: () => []
  },
  basecodeInSecond: {
    type: Array<BaseCodeMatch>,
    required: false,
    default: () => []
  }
})

defineEmits(['matchSelected'])

function getFileName(fullPath: string) {
  return fullPath.split(/[/\\]/g).pop() || ''
}

function getTooltipDirection(index: number): ToolTipDirection {
  if (index == 0) return 'right'
  if (index >= 2 && index + 2 >= (props.matches?.length ?? Infinity)) {
    return 'left'
  }
  return 'bottom'
}

function showTokenRanges(match: Match) {
  return (
    !isNaN(match.startInFirst.tokenListIndex) &&
    !isNaN(match.startInSecond.tokenListIndex) &&
    !isNaN(match.endInFirst.tokenListIndex) &&
    !isNaN(match.endInSecond.tokenListIndex)
  )
}

const scrollableList: Ref<HTMLElement | null> = ref(null)
const scrollOffsetX = ref(0)

function updateScrollOffset() {
  if (scrollableList.value) {
    scrollOffsetX.value = scrollableList.value.scrollLeft
  }
}

const hasBaseCode = computed(() => {
  return props.basecodeInFirst.length > 0 || props.basecodeInSecond.length > 0
})
</script>
