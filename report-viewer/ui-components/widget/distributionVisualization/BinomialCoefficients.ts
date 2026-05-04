import { BucketOptions } from '@jplag/model'
import BinomialCoefficientJson from './binomialCoefficients.json'

export const binomialCoefficients = BinomialCoefficientJson as Record<BucketOptions, number[]>
