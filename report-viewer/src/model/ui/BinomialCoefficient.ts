import type { BucketOptions } from '../Distribution'
import BinomialCoefficientJson from './BinomialCoefficient.json'

export const binomialCoefficients = BinomialCoefficientJson as Record<BucketOptions, number[]>
