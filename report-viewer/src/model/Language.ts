/**
 * Enum for the language parsers JPlag supports
 */
enum ParserLanguage {
  JAVA = 'Java',
  PYTHON = 'Python',
  C = 'C',
  CPP = 'C++',
  C_SHARP = 'C#',
  EMF_METAMODEL_DYNAMIC = 'EMF metamodels (dynamically created token set)',
  EMF_METAMODEL = 'EMF metamodel',
  EMF_MODEL = 'EMF models (dynamically created token set)',
  GO = 'Go',
  KOTLIN = 'Kotlin',
  R_LANG = 'R',
  RUST = 'Rust',
  SCALA = 'Scala',
  SCHEME = 'Scheme',
  SWIFT = 'Swift',
  TEXT = 'Text (naive)',
  SCXML = 'SCXML',
  LLVM = 'LLVMIR',
  JAVASCRIPT = 'JavaScript',
  TYPESCRIPT = 'TypeScript'
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

export { ParserLanguage, type Language, getLanguageParser }
