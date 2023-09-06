import { test, expect, Page } from '@playwright/test'
import { uploadFile } from './TestUtils'

test('Test all distribution combinations', async ({ page }) => {
  await page.goto('/')

  await uploadFile('result_small_cluster.zip', page)

  const options = getAllOptionCombinations()
  for (const option of options) {
    await compareDistributionDiagramm(page, option)
  }
})

/**
 * Checks if the distribution diagramm is correct for the given options
 * @param page Page currently tested on
 * @param options Options to be selected
 */
async function compareDistributionDiagramm(page: Page, options: string[]) {
  const distributionDiagrammContainer = page.getByText('Distribution of Comparisons:Options:')
  for (const option of options) {
    await distributionDiagrammContainer.getByText(option).click()
  }
  // This timeout is so that the screenshot is taken after the animation is finished
  await page.waitForTimeout(3000)
  const distributionDiagramm = await page.locator('canvas').first().screenshot()
  expect(distributionDiagramm).toMatchSnapshot(`distribution_${options.join('_')}.png`)
}

function getAllOptionCombinations() {
  const options = [
    ['Average', 'Maximum'],
    ['Linear', 'Logarithmic']
  ]

  function combine(a: string[][], b: string[]) {
    const combinations: string[][] = []
    for (let i = 0; i < a.length; i++) {
      for (let j = 0; j < b.length; j++) {
        combinations.push(a[i].concat(b[j]))
      }
    }
    return combinations
  }

  let combinations: string[][] = []
  for (let i = 0; i < options[0].length; i++) {
    combinations.push([options[0][i]])
  }

  for (let i = 1; i < options.length; i++) {
    combinations = combine(combinations, options[i])
  }
  return combinations
}
