/**
 * Enum for the language parsers JPlag supports
 */
enum ParserLanguage {
  JAVA = 'Javac based AST plugin',
  PYTHON = 'Python3 Parser',
  C = 'C Scanner',
  CPP = 'C++ Parser',
  C_SHARP = 'C# 6 Parser',
  EMF_METAMODEL_DYNAMIC = 'emf-dynamic',
  EMF_METAMODEL = 'EMF metamodel',
  EMF_MODEL = 'EMF models (dynamically created token set)',
  GO = 'Go Parser',
  KOTLIN = 'Kotlin Parser',
  R_LANG = 'R Parser',
  RUST = 'Rust Language Module',
  SCALA = 'Scala parser',
  SCHEME = 'SchemeR4RS Parser [basic markup]',
  SWIFT = 'Swift Parser',
  TEXT = 'Text Parser (naive)',
  SCXML = 'SCXML (Statechart XML)',
  LLVM = 'LLVMIR Parser'
}

/**
 * Gets the LanguageParser enum value for the given language
 * @param language String representation of language the files were parsed with
 * @returns The LanguageParser enum value
 */
function getLanguageParser(language: string): ParserLanguage {
  for (const key in ParserLanguage) {
    if (ParserLanguage[key as keyof typeof ParserLanguage] === language) {
      return ParserLanguage[key as keyof typeof ParserLanguage]
    }
  }

  throw new Error(`Language ${language} not found`)
}

/**
 * Enum for the highlight.js languages
 * The strings are the names of the languages in highlight.js (https://github.com/highlightjs/highlight.js/blob/main/SUPPORTED_LANGUAGES.md)
 */
enum HighlightLanguage {
  JAVA = 'java',
  PYTHON = 'python',
  CPP = 'cpp',
  C_SHARP = 'csharp',
  XML = 'xml',
  GO = 'go',
  KOTLIN = 'kotlin',
  R_LANG = 'r',
  RUST = 'rust',
  SCALA = 'scala',
  SCHEME = 'scheme',
  SWIFT = 'swift',
  TEXT = 'text'
}

/**
 * Gets the highlight.js language for the given language
 * @param language Language the files were parsed with
 * @returns The language for highlight.js
 */
function getHighlightLanguage(language: ParserLanguage | undefined): HighlightLanguage {
  switch (language) {
    case ParserLanguage.PYTHON:
      return HighlightLanguage.PYTHON
    case ParserLanguage.C:
    case ParserLanguage.CPP:
      return HighlightLanguage.CPP
    case ParserLanguage.C_SHARP:
      return HighlightLanguage.C_SHARP
    case ParserLanguage.EMF_METAMODEL:
    case ParserLanguage.EMF_METAMODEL_DYNAMIC:
    case ParserLanguage.EMF_MODEL:
    case ParserLanguage.SCXML:
      return HighlightLanguage.XML
    case ParserLanguage.GO:
      return HighlightLanguage.GO
    case ParserLanguage.KOTLIN:
      return HighlightLanguage.KOTLIN
    case ParserLanguage.R_LANG:
      return HighlightLanguage.R_LANG
    case ParserLanguage.RUST:
      return HighlightLanguage.RUST
    case ParserLanguage.SCALA:
      return HighlightLanguage.SCALA
    case ParserLanguage.SCHEME:
      return HighlightLanguage.SCHEME
    case ParserLanguage.SWIFT:
      return HighlightLanguage.SWIFT
    case ParserLanguage.TEXT:
      return HighlightLanguage.TEXT
    case ParserLanguage.JAVA:
    default:
      return HighlightLanguage.JAVA
  }
}

export { ParserLanguage, getLanguageParser, HighlightLanguage, getHighlightLanguage }
