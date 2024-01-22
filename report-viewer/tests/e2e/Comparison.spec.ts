import { test, expect, Page } from '@playwright/test'
import { uploadFile } from './TestUtils'

test('Test comparison table and comparsion view', async ({ page }) => {
  await page.goto('/')

  await uploadFile('result_small_cluster.zip', page)

  const comparisonContainer = page.getByText(
    'Top Comparisons: Type in the name of a submission to only show comparisons that contain this submission. Fully written out names get unhidden.Hide AllSort By'
  )

  // check for elements in average similarity table
  const comparisonTableAverageSorted = await page.getByText('Cluster1').textContent()
  expect(comparisonTableAverageSorted).toContain('1CA')
  expect(comparisonTableAverageSorted).toContain('2DC')

  await comparisonContainer.getByText('Maximum Similarity', { exact: true }).click()
  // check for elements in maximum similarity table
  const comparisonTableMaxSorted = await page.getByText('Cluster1').textContent()
  expect(comparisonTableMaxSorted).toContain('1CA')
  expect(comparisonTableMaxSorted).toContain('2BC')

  await page.getByText('Hide All').click()
  // check for elements being hidden
  const comparisonTableOverviewHidden = await page.getByText('Cluster1').textContent()
  expect(comparisonTableOverviewHidden).toMatch(/1anon[0-9]+anon[0-9]+/)
  expect(comparisonTableOverviewHidden).toMatch(/3anon[0-9]+anon[0-9]+/)
  expect(comparisonTableOverviewHidden).toMatch(/4anon[0-9]+anon[0-9]+/)

  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('A')
  // check for elements being unhidden and filtered
  const comparisonTableOverviewFilteredA = await page.getByText('Cluster1').textContent()
  expect(comparisonTableOverviewFilteredA).toMatch(/1anon[0-9]+A/) //toContain('1HiddenA')
  expect(comparisonTableOverviewFilteredA).toMatch(/3anon[0-9]+A/)
  // we cant check for 4Hidden because the dynamic scroller just moves it of screen, so the text is still there but not visible

  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('A C')
  // check for elements being unhidden and filtered
  const comparisonTableOverviewFilteredAC = await page.getByText('Cluster1').textContent()
  expect(comparisonTableOverviewFilteredAC).toContain('1CA')
  expect(comparisonTableOverviewFilteredAC).toMatch(/3anon[0-9]+A/)
  expect(comparisonTableOverviewFilteredAC).toMatch(/4anon[0-9]+C/)

  // go to comparison page
  await page.getByText('1C').click()
  await page.waitForURL(/\/comparison\/.*/)

  // check for elements in comparison page
  const bodyComparison = await page.locator('body').textContent()
  expect(bodyComparison).toContain('Average Similarity: 99.59%')
  expect(bodyComparison).toContain('GSTiling.java - GSTiling.java: 308')
  expect(bodyComparison).toContain('Matches.java - Matches.java: 58')
  expect(bodyComparison).toContain('A/Match.java')
  expect(bodyComparison).toContain('C/Match.java')

  // check for being able to hide and unhide elements
  expect(await isCodeVisible(page, 'public class Match {')).toBe(false)
  await page.getByText('A/Match.java').click()
  expect(await isCodeVisible(page, 'public class Match {')).toBe(true)
  await page.getByText('A/Match.java').click()
  expect(await isCodeVisible(page, 'public class Match {')).toBe(false)

  // unhide elements by clicking match list
  expect(await isCodeVisible(page, 'public class GSTiling')).toBe(false)
  await page.getByText('GSTiling.java - GSTiling.java: 308').click()
  await page.waitForTimeout(1000)
  expect(await isCodeVisible(page, 'public class GSTiling')).toBe(true)
})

async function isCodeVisible(page: Page, codePart: string) {
  return await page.locator('pre', { hasText: codePart }).first().isVisible()
}
