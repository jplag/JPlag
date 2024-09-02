// Ensures that different styles of passing submissions are supported
import { expect, test } from '@playwright/test'
import { uploadFile } from './TestUtils'

interface DataSet {
  datasetName: string
  firstSubmissionName: string
  secondSubmissionName: string
}

const regexpPathSeparator = '(\\\\|\\/)';

const testSets: DataSet[] = [
  {
    datasetName: 'fileSingleRoot-report.zip',
    firstSubmissionName: '0.java',
    secondSubmissionName: '1.java'
  },
  {
    datasetName: 'folderSingleRoot-report.zip',
    firstSubmissionName: '0',
    secondSubmissionName: '1'
  },
  {
    datasetName: 'fileMultiRoot-report.zip',
    firstSubmissionName: 'f0\\\\|/0.java',
    secondSubmissionName: 'f1\\\\|/1.java'
  },
  {
    datasetName: 'mixedBaseFile-report.zip',
    firstSubmissionName: 'f0\\\\|/0.java',
    secondSubmissionName: 'f1\\\\|/1'
  },
  {
    datasetName: 'mixedBaseFolder-report.zip',
    firstSubmissionName: 'f0\\\\|/0.java',
    secondSubmissionName: 'f1\\\\|/1'
  },
  {
    datasetName: 'folderMultiRoot-report.zip',
    firstSubmissionName: 'f0\\\\|/0',
    secondSubmissionName: 'f1\\\\|/1'
  },
  {
    datasetName: 'singleNewSubmission-report.zip',
    firstSubmissionName: `2023${regexpPathSeparator}QSort2023.java`,
    secondSubmissionName: `2022${regexpPathSeparator}QSort2022.java`,
  },
  {
    datasetName: 'submissionsWithSameName-report.zip',
    firstSubmissionName: `20\\d\\d${regexpPathSeparator}gr\\d`,
    secondSubmissionName: `20\\d\\d${regexpPathSeparator}gr\\d`
  },
  {
    datasetName: 'python-report.zip',
    firstSubmissionName: '01.py',
    secondSubmissionName: '02.py'
  },
  {
    datasetName: 'cpp-report.zip',
    firstSubmissionName: '01.cpp',
    secondSubmissionName: '02.cpp'
  },
  {
    datasetName: 'csharp-report.zip',
    firstSubmissionName: '01.cs',
    secondSubmissionName: '02.cs'
  }
]

for (const testSet of testSets) {
  test(`Can open ${testSet.datasetName}`, async ({ page }) => {
    await page.goto('/')

    await uploadFile(testSet.datasetName, page)

    const comparisonTable = await page.getByText('Cluster1').textContent()

    const lineRegEx = RegExp('1' + testSet.firstSubmissionName + testSet.secondSubmissionName)
    expect(comparisonTable).toMatch(lineRegEx)
    await page.getByText(lineRegEx).click()
    await page.waitForURL(/\/comparison\/.*/)

    const bodyComparison = await page.locator('body').textContent()
    expect(bodyComparison).toMatch(
      RegExp(`Comparison: ${testSet.firstSubmissionName} - ${testSet.secondSubmissionName}`)
    )
  })
}
