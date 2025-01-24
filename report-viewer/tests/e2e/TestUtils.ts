import { Page } from '@playwright/test'

/**
 * Selects a file in the file chooser and uploads it.
 * Expects the file to be in the tests/e2e/assets folder.
 * Expects to be on the file upload page.
 * @param fileName
 */
export async function uploadFile(fileName: string, page: Page, expectedURL: string = '/overview') {
  page.route('**/results.zip', async (route) => {
    await route.fulfill({
      // fullfill with the file
      path: `./tests/e2e/assets/${fileName}`,
      headers: {
        'Content-Type': 'application/zip'
      }
    })
  })

  await page.goto('/')

  await page.waitForURL(expectedURL)
}
