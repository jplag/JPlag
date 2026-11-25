import {
  type CliClusterOptions,
  type CliOptions,
  type CliParsingOptions,
  Language,
  type ReportFormatCliOptions,
  getLanguageParser
} from '@jplag/model'

export class OptionsFactory {
  public static getCliOptions(optionsFile: string): CliOptions {
    return this.extractOptions(JSON.parse(optionsFile))
  }

  private static extractOptions(json: ReportFormatCliOptions): CliOptions {
    return {
      ...json,
      parsingOptions: this.extractParsingOptions(json.parsingOptions),
      clusteringOptions: this.extractClusterOptions(json.clusteringOptions)
    }
  }

  private static extractParsingOptions(
    json: CliParsingOptions<string>
  ): CliParsingOptions<Language> {
    return {
      ...json,
      language: getLanguageParser(json.language)
    }
  }

  private static extractClusterOptions(json: CliClusterOptions): CliClusterOptions {
    return {
      ...json,
      preprocessor: this.transformWord(json.preprocessor),
      algorithm: this.transformWord(json.algorithm),
      agglomerativeInterClusterSimilarity: this.transformWord(
        json.agglomerativeInterClusterSimilarity
      )
    }
  }

  private static transformWord(word: string | undefined): string {
    if (word == undefined) {
      return ''
    }
    return word
      .split('_')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ')
  }
}
