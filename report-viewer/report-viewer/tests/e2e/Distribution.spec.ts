import { test, expect, Page } from '@playwright/test'
import { uploadFile } from './TestUtils'

const testData: TestData[] = [
  {
    name: 'Distribution Diagram',
    tabName: 'Distribution',
    options: getDistributionDiagramTestCombinations()
  },
  {
    name: 'Boxplot',
    tabName: 'Boxplot',
    options: [['Maximum Similarity'], ['Average Similarity']]
  }
]

for (const data of testData) {
  test(`Test ${data.name}`, async ({ page }) => {
    test.slow()
    await uploadFile('progpedia-report.jplag', page)

    const tabBar = page.getByText('DistributionBoxplot')
    await tabBar.getByText(data.tabName).click()

    const options = data.options
    await selectOptions(page, options[0])
    const canvas = page.locator('canvas').first()
    let lastImage = await canvas.screenshot()
    for (const option of options.slice(1)) {
      await selectOptions(page, option)
      const newImage = await canvas.screenshot()
      expect(newImage).not.toEqual(lastImage)
      lastImage = newImage
    }
  })
}

/**
 * Checks if the distribution diagram is correct for the given options
 * @param page Page currently tested on
 * @param options Options to be selected
 */
async function selectOptions(page: Page, options: string[]) {
  const distributionDiagramContainer = page.getByText('DistributionBoxplotOptions:')
  for (const option of options) {
    await distributionDiagramContainer.getByText(option, { exact: true }).click()
  }
  // This timeout is so that the screenshot is taken after the animation is finished
  await page.waitForTimeout(100)
}

function getDistributionDiagramTestCombinations() {
  const options = [
    ['Average Similarity', 'Maximum Similarity'],
    ['Linear', 'Logarithmic'],
    ['10', '20', '25', '50', '100']
  ]

  const baseOptions = options.map((o) => o[0])
  const combinations: string[][] = [baseOptions]

  for (let i = 0; i < options.length; i++) {
    for (let j = 1; j < options[i].length; j++) {
      const combination = Array.from(baseOptions)
      combination[i] = options[i][j]
      combinations.push(combination)
    }
  }
  return combinations
}

interface TestData {
  name: string
  tabName: string
  options: string[][]
}
