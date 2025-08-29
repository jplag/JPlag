const graphRGB = {
  red: 190,
  green: 22,
  blue: 34
}

interface GraphColors {
  ticksAndFont: string
  gridLines: string
  contentFill: string
  contentBorder: string
  pointFill: string
  additionalLine: string
  contentFillAlpha(alpha: number): string
  highlightedLineRGB: { red: number; green: number; blue: number }
  highlightedLine(alpha: number): string
}

export function graphColors(useDarkMode: boolean = false): GraphColors {
  return {
    ticksAndFont: useDarkMode ? '#ffffff' : '#000000',
    gridLines: useDarkMode ? 'rgba(256, 256, 256, 0.2)' : 'rgba(0, 0, 0, 0.2)',
    contentFill: `rgba(${graphRGB.red}, ${graphRGB.green}, ${graphRGB.blue}, 0.5)`,
    contentBorder: 'rgb(127, 15, 24)',
    pointFill: `rgba(${graphRGB.red}, ${graphRGB.green}, ${graphRGB.blue}, 1)`,
    additionalLine: useDarkMode ? `rgba(200, 200, 200, 0.3)` : `rgba(0, 0, 0, 0.5)`,
    contentFillAlpha(alpha: number) {
      return `rgba(${graphRGB.red}, ${graphRGB.green}, ${graphRGB.blue}, ${alpha})`
    },
    highlightedLineRGB: {
      red: useDarkMode ? 100 : 20,
      green: useDarkMode ? 200 : 50,
      blue: 255
    },
    highlightedLine(alpha: number) {
      return `rgba(${this.highlightedLineRGB.red}, ${this.highlightedLineRGB.green}, ${this.highlightedLineRGB.blue}, ${alpha})`
    }
  }
}
