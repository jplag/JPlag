import store from '@/stores/store'
import { computed } from 'vue'

/**
 * Genertes an array of HSL-Colors for a given interval
 * @param intervalStart start of the interval [0,360]
 * @param intervalEnd end of the interval [0,360] and > intervalStart
 * @param numberOfColorsInInterval Number of colors to generate in the interval
 * @param saturation [0,1]
 * @param lightness [0,1]
 * @param alpha [0,1]
 * @returns Array of strings in format 'hsla(hue, saturation, lightness, alpha)'
 */
function generateColorsForInterval(
  intervalStart: number,
  intervalEnd: number,
  numberOfColorsInInterval: number,
  saturation: number,
  lightness: number,
  alpha: number
) {
  const colors: Array<string> = []
  const interval = intervalEnd - intervalStart
  const hueDelta = Math.trunc(interval / numberOfColorsInInterval)
  for (let i = 0; i < numberOfColorsInInterval; i++) {
    const hue = intervalStart + i * hueDelta
    colors.push(`hsla(${hue}, ${saturation * 100}%, ${lightness * 100}%, ${alpha})`)
  }
  return colors
}

const graphColors = {
  ticksAndFont: computed(() => {
    return store().uiState.useDarkMode ? '#ffffff' : '#000000'
  }),
  gridLines: computed(() => {
    return store().uiState.useDarkMode ? 'rgba(256, 256, 256, 0.2)' : 'rgba(0, 0, 0, 0.2)'
  }),
  contentFill: 'rgba(190, 22, 34, 0.5)',
  contentBorder: 'rgb(127, 15, 24)',
  pointFill: 'rgba(190, 22, 34, 1)'
}

export { generateColorsForInterval, graphColors }
