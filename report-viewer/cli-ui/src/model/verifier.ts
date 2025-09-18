import { CliMergingOptions } from '@jplag/model'
import { ExpandedOptions, MinimumTokenMatch } from './ExpandedOptions'

type Verifier<T extends CliOptionsValues, D extends Partial<ExpandedOptions>> = (
  value: T,
  options: D
) => string | undefined

/**
 *
 * @param value Value to verify
 * @param isInt if true, value must be an integer
 * @param min minimum value (inclusive)
 * @param max maximum value (inclusive)
 * @returns an error message if the value is invalid, otherwise undefined
 */
function verifyNumber(value: number, isInt = true, min: number = 0, max: number = Infinity) {
  if (isNaN(value)) {
    return 'Entered value needs to be a number'
  }
  if (isInt && Math.round(value) !== value) {
    return 'Entered value needs to be an integer'
  }
  if (value < min) {
    return `Value must be greater than ${min}`
  }
  if (value > max) {
    return `Value must be less than ${max}`
  }

  return undefined
}

function acceptingVerifier() {
  return undefined
}

export function verifyMinTokenMatch(v: MinimumTokenMatch) {
  if (v === 'default') {
    return undefined
  }
  return verifyNumber(v, true)
}

export function verifyResultFileName(name: string) {
  if (name.trim() === '') {
    return 'File name cannot be empty'
  }
  return undefined
}

export const verifyShownComparisons = (v: number) => verifyNumber(v, true)
export const verifySimilarityyThreshold = (v: number) => verifyNumber(v, false, 0, 1)

export function verifySubsmissionDirectories(v: string[]) {
  if (v.length === 0) {
    return 'At least one submission directory must be selected'
  }
  return undefined
}

function verifySMMNumber(
  v: number,
  options: { minimumTokenMatch: MinimumTokenMatch; mergingOptions: CliMergingOptions }
) {
  if (!options.mergingOptions.enabled) {
    return undefined
  }
  const minTokenMatch =
    options?.minimumTokenMatch === 'default' ? Infinity : options.minimumTokenMatch
  return verifyNumber(v, true, 0, minTokenMatch)
}
export const verifyNeighborLength = verifySMMNumber
export const verifyGapSize = verifySMMNumber
export const verifyMinimumRequiredMerges = (v: number) => verifyNumber(v, true, 0)
export function verifyMergingOptions(
  v: CliMergingOptions,
  options: { minimumTokenMatch: MinimumTokenMatch; mergingOptions: CliMergingOptions }
) {
  if (!v.enabled) {
    return undefined
  }
  const minNeighborLengthError = verifyNeighborLength(v.minimumNeighborLength, options)
  if (minNeighborLengthError) {
    return `Minimum neighbor length: ${minNeighborLengthError}`
  }
  const maxGapSizeError = verifyGapSize(v.maximumGapSize, options)
  if (maxGapSizeError) {
    return `Maximum gap size: ${maxGapSizeError}`
  }
  const minRequiredMergesError = verifyNumber(v.minimumRequiredMerges, true, 0)
  if (minRequiredMergesError) {
    return `Minimum required merges: ${minRequiredMergesError}`
  }
  return undefined
}

type CliOptionsKeys = keyof ExpandedOptions
type RequieredOptionArguments = {
  [K in CliOptionsKeys]: Partial<ExpandedOptions>
} & { mergingOptions: { minimumTokenMatch: MinimumTokenMatch; mergingOptions: CliMergingOptions } }
type Verifiers = {
  [K in CliOptionsKeys]?: Verifier<ExpandedOptions[K], RequieredOptionArguments[K]>
}
type CliOptionsValues = ExpandedOptions[keyof ExpandedOptions]

const verifiers: Verifiers = {
  minimumTokenMatch: verifyMinTokenMatch,
  resultFileName: verifyResultFileName,
  maximumNumberOfComparisons: verifyShownComparisons,
  similarityThreshold: verifySimilarityyThreshold,
  submissionDirectories: verifySubsmissionDirectories,
  mergingOptions: verifyMergingOptions
}

export function getVerifier<K extends CliOptionsKeys>(
  key: K
): Verifier<ExpandedOptions[K], RequieredOptionArguments[K]> {
  return verifiers[key] ?? acceptingVerifier
}

export function verifyOptions(options: ExpandedOptions): boolean {
  const keys = Object.keys(options) as CliOptionsKeys[]
  for (const k of keys) {
    const verifier = verifiers[k]
    if (verifier) {
      const error = verifier(options[k], options)
      if (error) {
        return false
      }
    }
  }
  return true
}
