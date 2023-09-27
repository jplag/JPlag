import { store } from '@/stores/store'
import { computed } from 'vue'

/**
 * Generates an array of HSL-Colors
 * @param numberOfColors Number of colors to generate
 * @param saturation Saturation of the colors [0,1]
 * @param lightness Lightness of the colors [0,1]
 * @param alpha Alpha value of the colors [0,1]
 */
function generateColors(
  numberOfColors: number,
  saturation: number,
  lightness: number,
  alpha: number
) {
  const numberOfColorsInFirstInterval = Math.round(
    ((80 - 20) / (80 - 20 + (340 - 160))) * numberOfColors
  ) // number of colors from the first interval
  const numberOfColorsInSecondInterval = numberOfColors - numberOfColorsInFirstInterval // number of colors from the second interval

  const colors: Array<string> = generateColorsForInterval(
    20,
    80,
    numberOfColorsInFirstInterval,
    saturation,
    lightness,
    alpha
  )
  colors.push(...generateColorsForInterval(160, 340, numberOfColorsInSecondInterval, 0.8, 0.5, 0.3))
  return colors
}

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

const graphRGB = {
  red: 190,
  green: 22,
  blue: 34
}
const graphColors = {
  ticksAndFont: computed(() => {
    return store().uiState.useDarkMode ? '#ffffff' : '#000000'
  }),
  gridLines: computed(() => {
    return store().uiState.useDarkMode ? 'rgba(256, 256, 256, 0.2)' : 'rgba(0, 0, 0, 0.2)'
  }),
  contentFill: `rgba(${graphRGB.red}, ${graphRGB.green}, ${graphRGB.blue}, 0.5)`,
  contentBorder: 'rgb(127, 15, 24)',
  pointFill: `rgba(${graphRGB.red}, ${graphRGB.green}, ${graphRGB.blue}, 1)`,
  contentFillAlpha(alpha: number) {
    return `rgba(${graphRGB.red}, ${graphRGB.green}, ${graphRGB.blue}, ${alpha})`
  }
}

export { generateColors, graphColors }
