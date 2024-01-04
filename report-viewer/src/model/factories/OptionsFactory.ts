import type { CliClusterOptions, CliMergingOptions, CliOptions } from '../CliOptions'
import { ParserLanguage } from '../Language'
import { MetricType } from '../MetricType'
import { BaseFactory } from './BaseFactory'

export class OptionsFactory extends BaseFactory {
  public static async getCliOptions(): Promise<CliOptions> {
    return this.extractOptions(JSON.parse(await this.getFile('options.json')))
  }

  private static extractOptions(json: Record<string, unknown>): CliOptions {
    return {
      language: json['language'] as ParserLanguage,
      minTokenMatch: json['min_token_match'] as number,
      submissionDirectories: json['submission_directories'] as string[],
      oldDirectories: json['old_directories'] as string[],
      baseDirectory: json['base_directory'] as string,
      subDirectoryName: (json['subdirectory_name'] as string) ?? '',
      fileSuffixes: json['file_suffixes'] as string[],
      exclusionFileName: (json['exclusion_file_name'] as string) ?? '',
      similarityMetric: json['similarity_metric'] as MetricType,
      similarityThreshold: json['similarity_threshold'] as number,
      maxNumberComparisons: json['max_comparisons'] as number,
      clusterOptions: this.extractClusterOptions(json['cluster'] as Record<string, unknown>),
      mergingOptions: this.extractMergingOptions(json['merging'] as Record<string, unknown>)
    }
  }

  private static extractClusterOptions(json: Record<string, unknown>): CliClusterOptions {
    return {
      enabled: json['enabled'] as boolean,
      similarityMetric: MetricType.AVERAGE,
      spectralBandwidth: json['spectral_bandwidth'] as number,
      spectralGaussianProcessVariance: json['spectral_gaussian_variance'] as number,
      spectralMinRuns: json['spectral_min_runs'] as number,
      spectralMaxRuns: json['spectral_max_runs'] as number,
      spectralMaxKMeansIterations: json['spectral_max_kmeans_iterations'] as number,
      agglomerativeThreshold: json['agglomerative_threshold'] as number,
      preprocessor: this.transformWord(json['preprocessor'] as string),
      algorithm: this.transformWord(json['algorithm'] as string),
      interClusterSimilarity: this.transformWord(json['inter_similarity'] as string),
      preprocessorThreshold: json['preprocessor_threshold'] as number,
      preprocessorPercentile: json['preprocessor_percentile'] as number
    }
  }

  private static extractMergingOptions(json: Record<string, unknown>): CliMergingOptions {
    return {
      enabled: json['enabled'] as boolean,
      minNeighborLength: json['min_neighbour_length'] as number,
      maxGapSize: json['max_gap_size'] as number
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
