import { expect, test } from '@playwright/test'
import { uploadFile } from './TestUtils'

test('Test information page', async ({ page }) => {
  await page.goto('/')
  await uploadFile('result_small_cluster.zip', page)

  // check displayed information on overview page
  const bodyOverview = await page.locator('body').textContent()
  expect(bodyOverview).toContain('Directory: files')
  expect(bodyOverview).toContain('Total Submissions: 4')
  expect(bodyOverview).toContain('Total Comparisons: 6')
  expect(bodyOverview).toContain('Min Token Match: 9')

  // go to information page
  await page.getByText('More', { exact: true }).click()
  await page.waitForURL('/info')

  // check displayed run options on information page
  const runOptions = await page.getByText('Run Options:Submission Directory:').textContent()
  expect(runOptions).toContain('Submission Directory: files')
  expect(runOptions).toContain('Basecode Directory:')
  expect(runOptions).toContain('Language: Javac based AST plugin')
  expect(runOptions).toContain('File Extentions: .java, .JAVA')
  expect(runOptions).toContain('Min Token Match: 9')

  const runData = await page.getByText('Run Data:Date of Execution:').textContent()
  expect(runData).toContain('Date of Execution: 02/09/23')
  expect(runData).toContain('Execution Duration: 12 ms')
  expect(runData).toContain('Total Submissions: 4')
  expect(runData).toContain('Total Comparisons: 6')
  expect(runData).toContain('Shown Comparisons: 6')
  expect(runData).toContain('Missing Comparisons: 0')
})
