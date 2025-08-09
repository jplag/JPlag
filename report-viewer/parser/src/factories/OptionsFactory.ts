import {
  type CliClusterOptions,
  type CliOptions,
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
      language: getLanguageParser(json.language),
      clusteringOptions: this.extractClusterOptions(json.clusteringOptions)
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
