import { test, expect, Page } from '@playwright/test'
import { uploadFile } from './TestUtils'

test('Test comparison table and comparsion view', async ({ page }) => {
  await uploadFile('progpedia-report.jplag', page)

  // check for elements in average similarity table
  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('Lazy')
  const comparisonTableAverageSorted = await page.getByText(/Cluster [0-9]/).textContent()
  expect(comparisonTableAverageSorted).toContain('101Gray WolfLazy Bobcat')

  const tableHeader = page.getByText('Submissions in ComparisonSimilarity')
  await tableHeader.getByText('MAX', { exact: true }).click()
  // check for elements in maximum similarity table
  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('Lazy')
  const comparisonTableMaxSorted = await page.getByText(/Cluster [0-9]/).textContent()
  expect(comparisonTableMaxSorted).toContain('102Gray WolfLazy Bobcat')

  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('')
  // move mouse to avoid hover effects
  await page.mouse.move(0, 0)
  await page.waitForTimeout(200)
  await page.getByText('Anonymize All').click()
  // check for elements being hidden
  const comparisonTableOverviewHidden = await page.getByText('Cluster 1').textContent()
  expect(comparisonTableOverviewHidden).toMatch(/1anon[0-9]+anon[0-9]+/)
  expect(comparisonTableOverviewHidden).toMatch(/3anon[0-9]+anon[0-9]+/)
  expect(comparisonTableOverviewHidden).toMatch(/4anon[0-9]+anon[0-9]+/)

  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('Lazy Bobcat')
  // check for elements being unhidden and filtered
  const comparisonTableOverviewFiltered = await page.getByText(/Cluster [0-9]/).textContent()
  expect(comparisonTableOverviewFiltered).toMatch(/[0-9]+anon[0-9]+Lazy Bobcat/)
  expect(comparisonTableOverviewFiltered).toMatch(/[0-9]+Lazy Bobcatanon[0-9]+/)

  await page.getByText('Anonymize All').click()
  await page.getByText('Show All').click()
  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('Lazy')
  // go to comparison page
  await page.getByText('102Gray WolfLazy Bobcat').click()
  await page.waitForURL(/\/comparison\/.*/)

  // check for elements in comparison page
  const submissionName1 = 'Gray Wolf'
  const submissionName2 = 'Lazy Bobcat'
  const fileName1 = 'Sociologia.java'
  const fileName2 = 'Daa_sociologia.java'
  const content1 = 'class Aluno'
  const content2 = 'class Node'

  const bodyComparison = await page.locator('body').textContent()
  expect(bodyComparison).toMatch(/Average Similarity:[0-9]{2}.[0-9]{2}%/)
  expect(bodyComparison).toMatch(new RegExp(`Similarity ${submissionName1}:[0-9]{2}.[0-9]{2}%`))
  expect(bodyComparison).toMatch(new RegExp(`Similarity ${submissionName2}:[0-9]{2}.[0-9]{2}%`))

  expect(bodyComparison).toMatch(new RegExp(`${fileName1} - ${fileName2}:[0-9]+`))
  expect(bodyComparison).toContain(`${submissionName1}/${fileName1}`)
  expect(bodyComparison).toContain(`${submissionName2}/${fileName2}`)

  // check for being able to hide and unhide elements
  expect(await isCodeVisible(page, content1)).toBe(true)
  await page.getByText(`${submissionName1}/${fileName1}`).click()
  expect(await isCodeVisible(page, content1)).toBe(false)
  await page.getByText(`${submissionName1}/${fileName1}`).click()
  expect(await isCodeVisible(page, content1)).toBe(true)

  // unhide elements by clicking match list
  expect(await isCodeVisible(page, content2)).toBe(true)
  await page.getByText(`${submissionName1}/${fileName1}`).click()
  await page.getByText(`${submissionName2}/${fileName2}`).click()
  expect(await isCodeVisible(page, content2)).toBe(false)
  await page.getByText(`${fileName1} - ${fileName2}:`).first().click()
  await page.waitForTimeout(1000)
  expect(await isCodeVisible(page, content2)).toBe(true)
})

async function isCodeVisible(page: Page, codePart: string) {
  return await page.locator('pre', { hasText: codePart }).first().isVisible()
}
