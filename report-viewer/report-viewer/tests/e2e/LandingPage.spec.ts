import { test, expect } from '@playwright/test'

test('Open report through file explorer', async ({ page }) => {
  await page.goto('/')
  expect(page).toHaveURL('/')

  const fileChooserPromise = page.waitForEvent('filechooser')
  await page.getByText('Drag and Drop report file on this page').click()
  const fileChooser = await fileChooserPromise
  await fileChooser.setFiles(`tests/e2e/assets/progpedia-report.jplag`)

  await page.waitForURL('/overview')
  expect(page).toHaveURL('/overview')
  expect(await page.locator('body').textContent()).toContain('JPlag Report')
})
