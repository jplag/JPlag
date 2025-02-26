import { store } from '@/stores/store'
import { computed } from 'vue'

/**
 * Generates an array of HSL-Colors
 * @param numberOfColors Number of colors to generate
 */
function generateHues(numberOfColors: number) {
  const numberOfColorsInFirstInterval = Math.round(
    ((80 - 20) / (80 - 20 + (340 - 160))) * numberOfColors
  ) // number of colors from the first interval
  const numberOfColorsInSecondInterval = numberOfColors - numberOfColorsInFirstInterval // number of colors from the second interval

  const colors: Array<number> = generateColorsForInterval(20, 80, numberOfColorsInFirstInterval)
  colors.push(...generateColorsForInterval(160, 340, numberOfColorsInSecondInterval))
  return colors
}

/**
 * Generates an array of HSL-Colors for a given interval
 * @param intervalStart start of the interval [0,360]
 * @param intervalEnd end of the interval [0,360] and > intervalStart
 * @param numberOfColorsInInterval Number of colors to generate in the interval
 * @returns Array of strings in format 'hsla(hue, saturation, lightness, alpha)'
 */
function generateColorsForInterval(
  intervalStart: number,
  intervalEnd: number,
  numberOfColorsInInterval: number
) {
  const hues: Array<number> = []
  const interval = intervalEnd - intervalStart
  const hueDelta = Math.trunc(interval / numberOfColorsInInterval)
  for (let i = 0; i < numberOfColorsInInterval; i++) {
    const hue = intervalStart + i * hueDelta
    hues.push(hue)
  }
  return hues
}

/** This is the list of colors that are used as the background color of matches in the comparison view */
const matchColors: { red: number; green: number; blue: number }[] = [
  { red: 255, green: 122, blue: 0 },
  { red: 0, green: 133, blue: 255 },
  { red: 255, green: 0, blue: 122 },
  { red: 255, green: 245, blue: 0 },
  { red: 0, green: 255, blue: 255 },
  { red: 112, green: 0, blue: 255 },
  { red: 0, green: 255, blue: 133 }
]

function getMatchColorCount() {
  return matchColors.length
}

type MatchColorIndex = number | undefined | 'base'

function getMatchColor(alpha: number, index: MatchColorIndex) {
  if (index == undefined) {
    return 'rgba(0,0,0,0)'
  }
  if (index == 'base') {
    return getBaseCodeColor(alpha)
  }
  return `rgba(${matchColors[index].red}, ${matchColors[index].green}, ${matchColors[index].blue}, ${alpha})`
}

function getBaseCodeColor(opacity: number) {
  return `hsla(0, 0%, 75%, ${opacity})`
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
  additionalLine: computed(() => {
    return store().uiState.useDarkMode ? `rgba(200, 200, 200, 0.3)` : `rgba(0, 0, 0, 0.5)`
  }),
  contentFillAlpha(alpha: number) {
    return `rgba(${graphRGB.red}, ${graphRGB.green}, ${graphRGB.blue}, ${alpha})`
  }
}

export { generateHues, graphColors, getMatchColorCount, getMatchColor, type MatchColorIndex }
