import { type Language, ParserLanguage } from '@/model/Language'
import hljs from 'highlight.js'
import scheme from 'highlight.js/lib/languages/scheme'
import llvm from 'highlight.js/lib/languages/llvm'
import typescript from 'highlight.js/lib/languages/typescript'

/**
 * Hightlights the given code with the given language.
 * Splits the resulting html into seperate lines.
 * The returned string is an array of html lines, consisting of spans with the hljs classes and the code.
 * Source: https://stackoverflow.com/a/70656181
 * @param code Code to highlight
 * @param lang Language to highlight the code with
 * @returns
 */
export function highlight(code: string, lang: Language) {
  const highlightedCode = hljs.highlight(code, { language: getHighlightLanguage(lang) }).value
  const openTags: string[] = []
  const formattedCode = highlightedCode
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
  return formattedCode
}

function getHighlightLanguage(lang: Language) {
  switch (lang) {
    case ParserLanguage.PYTHON:
      return 'python'
    case ParserLanguage.C:
      return 'c'
    case ParserLanguage.CPP:
    case ParserLanguage.CPP_OLD:
    case ParserLanguage.CPP_2:
      return 'cpp'
    case ParserLanguage.C_SHARP:
      return 'csharp'
    case ParserLanguage.EMF_METAMODEL:
    case ParserLanguage.EMF_METAMODEL_DYNAMIC:
    case ParserLanguage.EMF_MODEL:
    case ParserLanguage.SCXML:
      return 'xml'
    case ParserLanguage.GO:
      return 'go'
    case ParserLanguage.KOTLIN:
      return 'kotlin'
    case ParserLanguage.R_LANG:
      return 'r'
    case ParserLanguage.RUST:
      return 'rust'
    case ParserLanguage.SCALA:
      return 'scala'
    case ParserLanguage.SCHEME:
      hljs.registerLanguage('scheme', scheme)
      return 'scheme'
    case ParserLanguage.SWIFT:
      return 'swift'
    case ParserLanguage.TEXT:
      return 'plaintext'
    case ParserLanguage.LLVM:
      hljs.registerLanguage('llvm', llvm)
      return 'llvm'
    case ParserLanguage.JAVASCRIPT:
      return 'javascript'
    case ParserLanguage.TYPESCRIPT:
      hljs.registerLanguage('typescript', typescript)
      return 'typescript'
    case ParserLanguage.JAVA:
    default:
      return 'java'
  }
}
