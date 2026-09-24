/**
 * Generates an array of HSL-Colors
 * @param numberOfColors Number of colors to generate
 */
export function generateHues(numberOfColors: number) {
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
