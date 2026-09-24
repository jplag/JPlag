import { Page } from '@playwright/test'

/**
 * Selects a file in the file chooser and uploads it.
 * Expects the file to be in the tests/e2e/assets folder.
 * Expects to be on the file upload page.
 * @param fileName
 */
export async function uploadFile(
  fileName: string,
  page: Page,
  waitCondition: (page: Page) => Promise<void> = async (page) =>
    await page.locator('text="JPlag Report"').waitFor({ state: 'visible' })
) {
  page.route('**/results.jplag', async (route) => {
    await route.fulfill({
      // fullfill with the file
      path: `./tests/e2e/assets/${fileName}`,
      headers: {
        'Content-Type': 'application/zip'
      }
    })
  })

  await page.goto('/')

  await waitCondition(page)
}
