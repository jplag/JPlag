import { Page, expect } from '@playwright/test'

/**
 * Selects a file in the file chooser and uploads it.
 * Expects the file to be in the tests/e2e/assets folder.
 * Expects to be on the file upload page.
 * @param fileName
 */
export async function uploadFile(fileName: string, page: Page) {
  expect(page).toHaveURL('/')

  // upload file through file chooser
  const fileChooserPromise = page.waitForEvent('filechooser')
  await page.getByText('Drag and Drop zip/Json file on this page').click()
  const fileChooser = await fileChooserPromise
  await fileChooser.setFiles(`tests/e2e/assets/${fileName}`)

  await page.waitForURL('/overview')
}
