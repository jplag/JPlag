/**
 * Enum for the language parsers JPlag supports
 */
enum LanguageParser {
  JAVA = 'Javac based AST plugin',
  PYTHON = 'Python3 Parser',
  CPP = 'C/C++ Scanner [basic markup]',
  CPP2 = 'C/C++ Parser',
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
function getLanguageParser(language: string): LanguageParser {
  for (const key in LanguageParser) {
    if (LanguageParser[key as keyof typeof LanguageParser] === language) {
      return LanguageParser[key as keyof typeof LanguageParser]
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
function getHighlightLanguage(language: LanguageParser | undefined): HighlightLanguage {
  switch (language) {
    case LanguageParser.PYTHON:
      return HighlightLanguage.PYTHON
    case LanguageParser.CPP:
    case LanguageParser.CPP2:
      return HighlightLanguage.CPP
    case LanguageParser.C_SHARP:
      return HighlightLanguage.C_SHARP
    case LanguageParser.EMF_METAMODEL:
    case LanguageParser.EMF_METAMODEL_DYNAMIC:
    case LanguageParser.EMF_MODEL:
    case LanguageParser.SCXML:
      return HighlightLanguage.XML
    case LanguageParser.GO:
      return HighlightLanguage.GO
    case LanguageParser.KOTLIN:
      return HighlightLanguage.KOTLIN
    case LanguageParser.R_LANG:
      return HighlightLanguage.R_LANG
    case LanguageParser.RUST:
      return HighlightLanguage.RUST
    case LanguageParser.SCALA:
      return HighlightLanguage.SCALA
    case LanguageParser.SCHEME:
      return HighlightLanguage.SCHEME
    case LanguageParser.SWIFT:
      return HighlightLanguage.SWIFT
    case LanguageParser.TEXT:
      return HighlightLanguage.TEXT
    case LanguageParser.JAVA:
    default:
      return HighlightLanguage.JAVA
  }
}

export { LanguageParser, getLanguageParser, HighlightLanguage, getHighlightLanguage }
