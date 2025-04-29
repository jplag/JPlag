/**
 * Enum for the language parsers JPlag supports
 */
enum ParserLanguage {
  JAVA = 'java',
  PYTHON = 'python3',
  C = 'c',
  CPP = 'cpp',
  C_SHARP = 'csharp',
  EMF_METAMODEL_DYNAMIC = 'emf-dynamic',
  EMF_METAMODEL = 'emf',
  EMF_MODEL = 'emf-model',
  GO = 'go',
  KOTLIN = 'kotlin',
  R_LANG = 'rlang',
  RUST = 'rust',
  SCALA = 'scale',
  SCHEME = 'scheme',
  SWIFT = 'swift',
  TEXT = 'text',
  SCXML = 'scxml',
  LLVM = 'llvmir',
  JAVASCRIPT = 'javascript',
  TYPESCRIPT = 'typescript',
  MULTI_LANGUAGE = 'multi'
}

type Language = ParserLanguage | 'unknown language'

/**
 * Gets the LanguageParser enum value for the given language
 * @param language String representation of language the files were parsed with
 * @returns The LanguageParser enum value
 */
function getLanguageParser(language: string): Language {
  for (const key in ParserLanguage) {
    if (ParserLanguage[key as keyof typeof ParserLanguage] === language) {
      return ParserLanguage[key as keyof typeof ParserLanguage]
    }
  }

  console.warn(`Unknown language: ${language}\nCode highlighting might not work correctly.`)
  return 'unknown language'
}

const languageList = [
  ParserLanguage.JAVA,
  ParserLanguage.PYTHON,
  ParserLanguage.C,
  ParserLanguage.CPP,
  ParserLanguage.C_SHARP,
  ParserLanguage.EMF_METAMODEL_DYNAMIC,
  ParserLanguage.EMF_METAMODEL,
  ParserLanguage.EMF_MODEL,
  ParserLanguage.GO,
  ParserLanguage.KOTLIN,
  ParserLanguage.R_LANG,
  ParserLanguage.RUST,
  ParserLanguage.SWIFT,
  ParserLanguage.SCHEME,
  ParserLanguage.TEXT,
  ParserLanguage.SCXML,
  ParserLanguage.LLVM,
  ParserLanguage.JAVASCRIPT,
  ParserLanguage.TYPESCRIPT,
  ParserLanguage.MULTI_LANGUAGE
]

export { ParserLanguage, type Language, getLanguageParser, languageList }
