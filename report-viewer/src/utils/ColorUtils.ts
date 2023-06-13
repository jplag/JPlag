import store from '@/stores/store'
import { computed } from 'vue'

function generateHuesForInterval(
  intervalStart: number,
  intervalEnd: number,
  numberOfColorsInInterval: number
) {
  const colors: Array<number> = []
  const interval = intervalEnd - intervalStart
  const hueDelta = Math.trunc(interval / numberOfColorsInInterval)
  for (let i = 0; i < numberOfColorsInInterval; i++) {
    const hue = intervalStart + i * hueDelta
    colors.push(hue)
  }
  return colors
}

/**
 *
 * @param hues
 * @param saturation [0,1]
 * @param lightness [0,1]
 * @param alpha [0,1]
 * @returns
 */
function toHSLAArray(
  hues: Array<number>,
  saturation: number,
  lightness: number,
  alpha: number
): Array<string> {
  const colors: Array<string> = []
  hues.forEach((hue) => {
    colors.push(`hsla(${hue}, ${saturation * 100}%, ${lightness * 100}%, ${alpha})`)
  })
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

export { generateHuesForInterval, toHSLAArray, graphColors }
