/**
 * Enum for the language parsers JPlag supports
 */
enum ParserLanguage {
  JAVA = 'Java',
  PYTHON = 'Python',
  C = 'C',
  CPP_OLD = 'C/C++ [basic markup]',
  CPP = 'C++',
  CPP_2 = 'C/C++',
  C_SHARP = 'C#',
  EMF_METAMODEL_DYNAMIC = 'emf-dynamic',
  EMF_METAMODEL = 'EMF metamodel',
  EMF_MODEL = 'EMF models',
  GO = 'Go',
  KOTLIN = 'Kotlin',
  R_LANG = 'R',
  RUST = 'Rust',
  SCALA = 'Scala',
  SCHEME = 'SchemeR4RS',
  SWIFT = 'Swift',
  TEXT = 'Text',
  SCXML = 'SCXML',
  LLVM = 'LLVMIR',
  JAVASCRIPT = 'JavaScript',
  TYPESCRIPT = 'Typescript'
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

export { ParserLanguage, getLanguageParser }
