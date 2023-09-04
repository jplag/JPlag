import { test, expect, Page } from '@playwright/test'
import { uploadFile } from './TestUtils'

test('Test cluster view', async ({ page }) => {
  await page.goto('/')

  await uploadFile('result_small_cluster.zip', page)

  // check for all clusters being shown
  expect(await page.getByText('submissions in cluster').all()).toHaveLength(6)

  await page.getByText('4 94.75%').first().click()
  await page.waitForURL(/\/cluster\/.*/)

  // Check cluster diagramm
  await compareClusterDiagramm(page, 'C')
  await page.getByRole('combobox').selectOption('B')
  await compareClusterDiagramm(page, 'B')

  // Check comparison table
  const comparisonTable = await page
    .getByText('Comparisons of Cluster Members:Submissions in Comparison')
    .textContent()
  compareTableRow(comparisonTable, 1, 'C', 'A', 99.6, 99.6)
  compareTableRow(comparisonTable, 2, 'D', 'C', 76.06, 95.93)
  compareTableRow(comparisonTable, 3, 'D', 'A', 76.06, 95.93)
  compareTableRow(comparisonTable, 4, 'B', 'D', 28.32, 80.85)
  compareTableRow(comparisonTable, 5, 'B', 'C', 23.78, 97.16)
  compareTableRow(comparisonTable, 6, 'B', 'A', 23.78, 97.16)
})

/**
 *
 * @param page Page currently tested on
 * @param submissionId Id of the selected submission
 */
async function compareClusterDiagramm(page: Page, submissionId: string) {
  expect(page.getByRole('combobox').first()).toHaveValue(submissionId)
  // This timeout is so that the screenshot is taken after the animation is finished
  await page.waitForTimeout(3000)
  const radarChart = await page.locator('canvas').first().screenshot()
  expect(radarChart).toMatchSnapshot(`screenshots/Small_Cluster_${submissionId}.png`)
}

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
