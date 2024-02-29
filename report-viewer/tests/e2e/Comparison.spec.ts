import { test, expect, Page } from '@playwright/test'
import { uploadFile } from './TestUtils'

test('Test comparison table and comparsion view', async ({ page }) => {
  await page.goto('/')

  await uploadFile('progpedia.zip', page)

  const comparisonContainer = page.getByText(
    'Top Comparisons: Type in the name of a submission to only show comparisons that contain this submission. Fully written out names get unhidden.Hide AllSort By'
  )

  // check for elements in average similarity table
  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('Purple')
  const comparisonTableAverageSorted = await page.getByText(/Cluster[0-9]/).textContent()
  expect(comparisonTableAverageSorted).toContain('100Purple FishBeige Dog')

  await comparisonContainer.getByText('Maximum Similarity', { exact: true }).click()
  // check for elements in maximum similarity table
  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('Blue')
  const comparisonTableMaxSorted = await page.getByText(/Cluster[0-9]/).textContent()
  expect(comparisonTableMaxSorted).toContain('100Blue AntelopeLime Lynx')

  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('')
  await page.getByText('Hide All').click()
  // check for elements being hidden
  const comparisonTableOverviewHidden = await page.getByText('Cluster1').textContent()
  expect(comparisonTableOverviewHidden).toMatch(/1anon[0-9]+anon[0-9]+/)
  expect(comparisonTableOverviewHidden).toMatch(/3anon[0-9]+anon[0-9]+/)
  expect(comparisonTableOverviewHidden).toMatch(/4anon[0-9]+anon[0-9]+/)

  // Temporarily disabled due to https://github.com/jplag/JPlag/issues/1629
  /*await page.getByPlaceholder('Filter/Unhide Comparisons').fill('A')
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
  expect(comparisonTableOverviewFilteredAC).toMatch(/4anon[0-9]+C/)+/*/

  await page.getByText('Show All').click()
  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('Blue')
  // go to comparison page
  await page.getByText('Blue AntelopeLime Lynx').click()
  await page.waitForURL(/\/comparison\/.*/)

  // check for elements in comparison page
  const bodyComparison = await page.locator('body').textContent()
  expect(bodyComparison).toMatch(/Average Similarity: [0-9]{2}.[0-9]{2}%/)
  expect(bodyComparison).toMatch(/Similarity Blue Antelope: [0-9]{2}.[0-9]{2}%/)
  expect(bodyComparison).toMatch(/Similarity Lime Lynx: [0-9]{2}.[0-9]{2}%/)

  expect(bodyComparison).toMatch(/sociologia.java - Sociologia.java: [0-9]+/)
  expect(bodyComparison).toContain('Blue Antelope/sociologia.java')
  expect(bodyComparison).toContain('Lime Lynx/Sociologia.java')

  // check for being able to hide and unhide elements
  expect(await isCodeVisible(page, 'class No')).toBe(false)
  await page.getByText('Blue Antelope/sociologia.java').click()
  expect(await isCodeVisible(page, 'class No')).toBe(true)
  await page.getByText('Blue Antelope/sociologia.java').click()
  expect(await isCodeVisible(page, 'class No')).toBe(false)

  // unhide elements by clicking match list
  expect(await isCodeVisible(page, 'class Node')).toBe(false)
  await page.getByText('sociologia.java - Sociologia.java:').first().click()
  await page.waitForTimeout(1000)
  expect(await isCodeVisible(page, 'class Node')).toBe(true)
})

async function isCodeVisible(page: Page, codePart: string) {
  return await page.locator('pre', { hasText: codePart }).first().isVisible()
}
