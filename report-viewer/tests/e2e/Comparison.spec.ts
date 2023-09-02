import { test, expect } from '@playwright/test'

test('Test comparison table and comparsion view', async ({ page }) => {
  await page.goto('/')

  // upload zip through file chooser
  const fileChooserPromise = page.waitForEvent('filechooser')
  await page.getByText('Drag and Drop zip/Json file on this page').click()
  const fileChooser = await fileChooserPromise
  await fileChooser.setFiles('tests/e2e/assets/result_small_cluster.zip')

  await page.waitForURL('/overview')
  // check for elements in average similarity table
  const bodyOverviewAverageSorted = await page.locator('body').textContent()
  expect(bodyOverviewAverageSorted).toContain('1CA')
  expect(bodyOverviewAverageSorted).toContain('2DC')

  await page.getByText('Maximum Similarity').click()
  // check for elements in maximum similarity table
  const bodyOverviewMaxSorted = await page.locator('body').textContent()
  expect(bodyOverviewMaxSorted).toContain('1CA')
  expect(bodyOverviewMaxSorted).toContain('2BC')

  await page.getByText('Hide All').click()
  // check for elements being hidden
  const bodyOverviewHidden = await page.locator('body').textContent()
  expect(bodyOverviewHidden).toContain('1HiddenHidden')
  expect(bodyOverviewHidden).toContain('3HiddenHidden')
  expect(bodyOverviewHidden).toContain('4HiddenHidden')

  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('A')
  // check for elements being unhidden and filtered
  await page.waitForTimeout(1000)
  const bodyOverviewFilteredA = await page.locator('body').textContent()
  expect(bodyOverviewFilteredA).toContain('1HiddenA')
  expect(bodyOverviewFilteredA).toContain('3HiddenA')
  // we cant check for 4Hidden because the dynamic scroller just moves it of screen, so the text is still there but not visible

  await page.getByPlaceholder('Filter/Unhide Comparisons').fill('A C')
  // check for elements being unhidden and filtered
  const bodyOverviewFilteredAC = await page.locator('body').textContent()
  expect(bodyOverviewFilteredAC).toContain('1CA')
  expect(bodyOverviewFilteredAC).toContain('3HiddenA')
  expect(bodyOverviewFilteredAC).toContain('4HiddenC')

  // go to comparison page
  await page.getByRole('link', { name: '1 C A 99.60% 99.60%' }).click()
  await page.waitForURL(/\/comparison\/.*/)

  // check for elements in comparison page
  const bodyComparison = await page.locator('body').textContent()
  expect(bodyComparison).toContain('Average Similarity: 99.59%')
  expect(bodyComparison).toContain('GSTiling.java - GSTiling.java: 308')
  expect(bodyComparison).toContain('Matches.java - Matches.java: 58')
  expect(bodyComparison).toContain('A/Match.java')
  expect(bodyComparison).toContain('C/Match.java')

  // check for being able to hide and unhide elements
  expect(bodyComparison).not.toContain('public class Match')
  await page.getByText('A/Match.java').click()
  const bodyComparisonShowMatch = await page.locator('body').textContent()
  expect(bodyComparisonShowMatch).toContain('public class Match')
  await page.getByText('A/Match.java').click()
  const bodyComparisonHideMatch = await page.locator('body').textContent()
  expect(bodyComparisonHideMatch).not.toContain('public class Match')

  // unhide elements by clicking match list
  expect(bodyComparisonHideMatch).not.toContain('public class GSTiling')
  await page.getByText('GSTiling.java - GSTiling.java: 308').click()
  const bodyComparisonShowGSTiling = await page.locator('body').textContent()
  expect(bodyComparisonShowGSTiling).toContain('public class GSTiling')
})
