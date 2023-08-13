import type { HighlightLanguage } from '@/model/Language'
import hljs from 'highlight.js'

/**
 * Hightlights the given code with the given language
 * Splits the resulting html into seperate lines
 * Source: https://stackoverflow.com/a/70656181
 * @param code
 * @param lang
 * @returns
 */
export function highlight(code: string, lang: HighlightLanguage) {
  const highlightedCode = hljs.highlight(code, { language: lang.valueOf() }).value
  const openTags: string[] = []
  const formatedCode = highlightedCode
    .replace(/(<span [^>]*>)|(<\/span>)|(\n)/g, (match: string) => {
      if (match === '\n') {
        return '</span>'.repeat(openTags.length) + '\n' + openTags.join('')
      }

      if (match === '</span>') {
        openTags.pop()
      } else {
        openTags.push(match)
      }

      return match
    })
    .split('\n')
  return formatedCode
}
