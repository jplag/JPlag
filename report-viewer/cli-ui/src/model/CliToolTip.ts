export enum CliToolTip {
  LANGUAGE = 'Select the language of the submissions.',
  ROOT_DIRS = 'Root-directories with submissions to check for plagiarism.',
  BASE_CODE = 'Path to the base code directory (common framework used in all submissions).',
  SHOWN_COMPARISONS = 'The maximum number of comparisons that will be shown in the generated report, if set to -1 all comparisons will be shown',
  NORMALIZE = 'Activate the normalization of tokens. Supported for languages: Java, C++',
  OLD = 'Root-directories with prior submissions to compare against.',
  RESULT_FILE = 'Name of the file in which the comparison results will be stored. Missing .jplag endings will be automatically added.',
  MIN_TOKENS = 'Tunes the comparison sensitivity by adjusting the minimum token required to be counted as a matching section. A smaller value increases the sensitivity but might lead to more false-positives.',

  CSV = 'Export pairwise similarity values as a CSV file.',
  DEBUG = 'Store on-parsable files in error folder.',
  LOG_LEVEL = 'Set the log level for the cli.',
  SIMILARITY_TRESHOLD = 'Comparison similarity threshold [0.0-1.0]: All comparisons above this threshold will be saved',
  OVERWRITE = 'Existing result files will be overwritten.',
  SUBDIRECTORY = 'Look in directories <root-dir>/*/<dir> for programs.',
  EXCLUSION_FILE = 'All files named in this file will be ignored in the comparison (line-separated list).',

  CLUSTERING = 'Calculate groups of similar sets of more then two submissions.',
  CLUSTER_ALGORITHM = 'Specifies the clustering algorithm.',
  CLUSTER_METRIC = 'The similarity metric used for clustering. Available metrics: average similarity, minimum similarity, maximal similarity, matched tokens',

  MATCH_MERGING = 'Enables merging of neighboring matches to counteract obfuscation attempts.',
  SMM_GAP_SIZE = 'Maximal gap between neighboring matches to be merged(between 1 and minTokenMatch).',
  SMM_NEIGHBOUR_LENGTH = 'Minimal length of neighboring matches to be merged(between 1 and minTokenMatch).',
  SMM_REQUIERED_MERGES = 'Minimal required merges for the merging to be applied'
}
