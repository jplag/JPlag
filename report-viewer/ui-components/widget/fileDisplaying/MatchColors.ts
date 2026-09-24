import { MatchColorIndex } from '@jplag/model'

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

export function getMatchColor(alpha: number, index: MatchColorIndex) {
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
