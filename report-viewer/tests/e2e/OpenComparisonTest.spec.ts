// Ensures that different styles of passing submissions are supported
import { expect, test } from '@playwright/test'
import { uploadFile } from './TestUtils'

interface DataSet {
  datasetName: string
  firstSubmissionName: string
  secondSubmissionName: string
}

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
  // Disabled due to https://github.com/jplag/JPlag/issues/1610
  /*{ datasetName: 'fileMultiRoot-report.zip', firstSubmissionName: 'f0\\0.java', secondSubmissionName: 'f1\\1.java' },*/
  {
    datasetName: 'folderMultiRoot-report.zip',
    firstSubmissionName: 'f0\\0',
    secondSubmissionName: 'f1\\1'
  }
]

for (const testSet of testSets) {
  test(`Can open ${testSet.datasetName}`, async ({ page }) => {
    await page.goto('/')

    await uploadFile(testSet.datasetName, page)

    const comparisonTable = await page.getByText('Cluster1').textContent()
    expect(comparisonTable).toContain(
      `1${testSet.firstSubmissionName}${testSet.secondSubmissionName}`
    )
    await page.getByText(`1${testSet.firstSubmissionName}${testSet.secondSubmissionName}`).click()
    await page.waitForURL(/\/comparison\/.*/)

    const bodyComparison = await page.locator('body').textContent()
    expect(bodyComparison).toContain(
      `Comparison: ${testSet.firstSubmissionName} - ${testSet.secondSubmissionName}`
    )
  })
}
