import { test, expect } from '@playwright/test'
import { uploadFile } from './TestUtils'

test('Test cluster view', async ({ page }) => {
  await page.goto('/')

  await uploadFile('result_small_cluster.zip', page)

  // check for all clusters being shown
  expect(await page.getByText('submissions in cluster').all()).toHaveLength(6)

  await page.getByText('4 94.75%').first().click()
  await page.waitForURL(/\/cluster\/.*/)

  // check that the cluster graph exist
  expect(page.locator('canvas').first()).not.toBeHidden()

  // switch to cluster chart
  await page.getByText('Radar').first().click()

  // Check cluster diagram
  await page.waitForTimeout(3000)
  const radarChart = page.locator('canvas').first()
  expect(page.getByRole('combobox').first()).toHaveValue('C')
  const clusterImageC = radarChart.screenshot()
  await page.getByRole('combobox').selectOption('B')
  expect(page.getByRole('combobox').first()).toHaveValue('B')
  expect(await radarChart.screenshot()).not.toEqual(clusterImageC)

  // Check comparison table
  const comparisonTable = await page.textContent('body')
  compareTableRow(comparisonTable, 1, 'C', 'A', 99.6, 99.6)
  compareTableRow(comparisonTable, 2, 'D', 'C', 76.06, 95.93)
  compareTableRow(comparisonTable, 3, 'D', 'A', 76.06, 95.93)
  compareTableRow(comparisonTable, 4, 'B', 'D', 28.32, 80.85)
  compareTableRow(comparisonTable, 5, 'B', 'C', 23.78, 97.16)
  compareTableRow(comparisonTable, 6, 'B', 'A', 23.78, 97.16)
})

function compareTableRow(
  table: string,
  row: number,
  id1: string,
  id2: string,
  similarityAVG: number,
  similarityMAX: number
) {
  expect(table).toContain(
    `${row}${id1}${id2}${similarityAVG.toFixed(2)}% ${similarityMAX.toFixed(2)}%`
  )
}
