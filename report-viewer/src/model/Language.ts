/**
 * Enum for the language parsers JPlag supports
 */
enum ParserLanguage {
  JAVA = 'Javac based AST plugin',
  PYTHON = 'Python3 Parser',
  C = 'C Scanner',
  CPP_OLD = 'C/C++ Scanner [basic markup]',
  CPP = 'C++ Parser',
  CPP_2 = 'C/C++ Parser',
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
  LLVM = 'LLVMIR Parser',
  JAVASCRIPT = 'JavaScript',
  TYPESCRIPT = 'Typescript Parser'
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
