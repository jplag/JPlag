import { expect, test } from '@playwright/test'
import { uploadFile } from './TestUtils'

test('Test information page', async ({ page }) => {
  await page.goto('/')
  await uploadFile('progpedia-report.zip', page)

  // check displayed information on overview page
  const bodyOverview = await page.locator('body').textContent()
  expect(bodyOverview).toContain('Directory: ')
  expect(bodyOverview).toMatch(/Total Submissions: [0-9]+/)
  expect(bodyOverview).toMatch(/Total Comparisons: [0-9]+/)
  expect(bodyOverview).toMatch(/Min Token Match: [0-9]+/)

  // go to information page
  await page.getByText('More', { exact: true }).click()
  await page.waitForURL('/info')

  // check displayed run options on information page
  const runOptions = await page.getByText('Run Options:Language:').textContent()
  expect(runOptions).toContain('Submission Directories: ')
  expect(runOptions).toContain('Base Directory: ')
  expect(runOptions).toContain('Language: ')
  expect(runOptions).toContain('File Suffixes: ')
  expect(runOptions).toMatch(/Min Token Match: [0-9]+/)

  const runData = await page.getByText('Run Data:Date of Execution:').textContent()
  expect(runData).toMatch(/Date of Execution: [0-9]{2}\/[0-9]{2}\/[0-9]{2}/)
  expect(runData).toMatch(/Execution Duration: [0-9]+ ms/)
  expect(runData).toMatch(/Total Submissions: [0-9]+/)
  expect(runData).toMatch(/Total Comparisons: [0-9]+/)
  expect(runData).toMatch(/Shown Comparisons: [0-9]+/)
  expect(runData).toMatch(/Missing Comparisons: [0-9]+/)
})
