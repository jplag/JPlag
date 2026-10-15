import { SubmissionFile, File } from '@jplag/model'

export const mockFiles: File[] = [
  {
    fileName: 'cluster.json',
    data: ''
  },
  {
    fileName: 'distribution.json',
    data: ''
  },
  {
    fileName: 'options.json',
    data: ''
  },
  {
    fileName: 'reportInformation.json',
    data: '{"secondaryMetrics":["MAX","LONGEST_MATCH","MAXIMUM_LENGTH"]}'
  },
  {
    fileName: 'runInformation.json',
    // the version check uses the raw string, so we need a version here
    data: '{"version":{"major":6,"minor":2,"patch":0}}'
  },
  {
    fileName: 'submissionMappings.json',
    data: ''
  },
  {
    fileName: 'topComparisons.json',
    data: ''
  },

  {
    fileName: 'basecode/test1.json',
    data: ''
  },
  {
    fileName: 'basecode/test2.json',
    data: ''
  },
  {
    fileName: 'basecode/test3.json',
    data: ''
  },

  {
    fileName: 'submissionFileIndex.json',
    data: ''
  },
  {
    fileName: 'comparisons/test1-test2.json',
    data: ''
  }
]

export const submissionFiles: SubmissionFile[] = [
  {
    submissionId: 'test1',
    fileName: 'test1/Submission.java',
    data: ''
  },
  {
    submissionId: 'test1',
    fileName: 'test1/Structure.java',
    data: ''
  },
  {
    submissionId: 'test2',
    fileName: 'test2/Submission.java',
    data: ''
  },
  {
    submissionId: 'test2',
    fileName: 'test2/Structure.java',
    data: ''
  },
  {
    submissionId: 'test3',
    fileName: 'test3/Submission.java',
    data: ''
  },
  {
    submissionId: 'test3',
    fileName: 'test3/Structure.java',
    data: ''
  }
]
