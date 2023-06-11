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
  alpha
): Array<string> {
  const colors: Array<string> = []
  hues.forEach((hue) => {
    colors.push(`hsla(${hue}, ${saturation * 100}%, ${lightness * 100}%, ${alpha})`)
  })
  return colors
}

export { generateHuesForInterval, toHSLAArray }
