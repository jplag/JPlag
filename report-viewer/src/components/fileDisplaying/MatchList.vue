<!--
  Table which contains all of the matches for a comparison with navigation links.
-->
<template>
  <div
    class="flex h-fit min-w-0 max-w-full flex-row space-x-1 overflow-x-hidden text-xs print:hidden"
  >
    <ToolTipComponent direction="right" v-if="hasBaseCode" class="pr-3">
      <template #default>
        <OptionComponent label="Base Code" :style="{ background: getMatchColor(0.3, 'base') }" />
      </template>
      <template #tooltip>
        <div class="whitespace-pre text-sm">
          Sections that are likely base code (thus ignored in similarity calculation). <br />
          <p>
            {{ store().getDisplayName(id1) }}:
            {{ basecodeInFirst.map((b) => b.match.tokens).reduce((a, b) => a + b, 0) }} Tokens,
            Lines: {{ store().getDisplayName(id1 ?? '') }}: Lines
            {{ basecodeInFirst.map((b) => `${b.start}-${b.end}`).join(',') }}
          </p>
          <p>
            {{ store().getDisplayName(id2) }}:
            {{ basecodeInSecond.map((b) => b.match.tokens).reduce((a, b) => a + b, 0) }} Tokens,
            Lines: {{ store().getDisplayName(id2 ?? '') }}: Lines
            {{ basecodeInSecond.map((b) => `${b.start}-${b.end}`).join(',') }}
          </p>
        </div>
      </template>
    </ToolTipComponent>

    <ToolTipComponent direction="right">
      <template #default>
        <OptionComponent label="Match Files: TokenCount" />
      </template>
      <template #tooltip>
        <p class="whitespace-pre text-sm">Click on a match to show it in the code view.</p>
      </template>
    </ToolTipComponent>

    <div
      class="print-excact flex w-full flex-row space-x-1 overflow-x-auto print:flex-wrap print:space-y-1 print:overflow-x-hidden"
      ref="scrollableList"
      @scroll="updateScrollOffset()"
    >
      <ToolTipComponent
        :direction="getTooltipDirection(index)"
        v-for="[index, match] in matches?.entries()"
        :key="index"
        :scrollOffsetX="scrollOffsetX"
      >
        <template #default>
          <OptionComponent
            :style="{ background: getMatchColor(0.3, match.colorIndex) }"
            @click="$emit('matchSelected', match)"
            :label="
              getFileName(match.firstFile) +
              ' - ' +
              getFileName(match.secondFile) +
              ': ' +
              match.tokens
            "
          />
        </template>
        <template #tooltip>
          <p class="whitespace-pre text-sm">
            Match between {{ getFileName(match.firstFile) }} (Line {{ match.startInFirst.line }}-{{
              match.endInFirst.line
            }}) and {{ getFileName(match.secondFile) }} (Line {{ match.startInSecond.line }}-{{
              match.endInSecond.line
            }}) <br />
            Match is {{ match.tokens }} tokens long. <br />
            <span v-if="showTokenRanges(match)">
              Token indeces of match: {{ match.startInFirst.tokenListIndex }}-{{
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
        v-bind:key="index"
        :style="{ background: getMatchColor(0.3, match.colorIndex) }"
        class="print-excact"
      >
        <td class="px-2">{{ getFileName(match.firstFile) }}</td>
        <td class="px-2">{{ match.startInFirst }} - {{ match.endInFirst }}</td>
        <td class="px-2">{{ getFileName(match.secondFile) }}</td>
        <td class="px-2">{{ match.startInSecond }} - {{ match.endInSecond }}</td>
        <td class="px-2">{{ match.tokens }}</td>
      </tr>
      <tr
        v-if="hasBaseCode"
        :style="{ background: getMatchColor(0.3, 'base') }"
        class="print-excact"
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
